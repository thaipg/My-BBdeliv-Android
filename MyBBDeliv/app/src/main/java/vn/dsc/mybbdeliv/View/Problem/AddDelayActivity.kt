package vn.dsc.mybbdeliv.View.Problem

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import com.google.zxing.integration.android.IntentIntegrator
import dsc.vn.mybbdeliv.Base.BaseActivity
import dsc.vn.mybbdeliv.Camera.ZXingUtils
import dsc.vn.mybbdeliv.Model.Reason
import dsc.vn.mybbdeliv.Model.Shipment
import dsc.vn.mybbdeliv.Process.GeneralProcess
import dsc.vn.mybbdeliv.Process.ShipmentProcess
import dsc.vn.mybbdeliv.R
import dsc.vn.mybbdeliv.Utils.ToastUtils
import kotlinx.android.synthetic.main.content_add_delay.*
import org.json.JSONObject

class AddDelayActivity : BaseActivity() , View.OnClickListener{

    var selectedItems: MutableList<Shipment> = mutableListOf()
    private var selectledScanType: Int = 0
    private var selectedReason: Reason? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_add_delay)
        super.onCreate(savedInstanceState)
        prepareUI()
        bindData()
    }
    private fun prepareUI()
    {
        setupUI(findViewById<View>(clLayout.id))
        btnUpdate.setOnClickListener(this)
        btScanType.setOnClickListener(this)
        btSelectReason.setOnClickListener(this)
        imageView_scan_camera.setOnClickListener(this)
    }

    private fun bindData()
    {
    }

    override fun onClick(v: View?) {

        if (v != null) {
            when {
                v.id == imageView_scan_camera.id -> {
                    getBarcodeScan()
                }
                v.id == btnUpdate.id -> {
                    if (txtShipmentCode.text.isEmpty())
                    {
                        ToastUtils.error(this,"Chưa nhập mã !")
                        return
                    }
                    if(selectedReason == null)
                    {
                        ToastUtils.error(this,"Vui lòng chọn lý do !")
                        return
                    }
                    if(editDelayTime.text.isEmpty())
                    {
                        ToastUtils.error(this,"Chưa nhập thời gian delay")
                        return
                    }

                    val json = JSONObject()
                    if (selectledScanType == 0) {
                        json.put("ShipmentNumber", txtShipmentCode.text)
                    }
                    else {
                        json.put("ListGoodsCode", txtShipmentCode.text)
                    }
                    json.put("DelayReasonId", selectedReason!!.id)
                    json.put("DelayTime", editDelayTime.text)
                    json.put("DelayNote", editTextNote.text)

                    ShipmentProcess().addDelay(this, json)
                    {
                        Log.v("result", it)
                        ToastUtils.error(this,it)
                        resetForm()
                    }
                }
                v.id == btScanType.id -> {
                    val pickScanTypeName: ArrayList<String> = ArrayList()
                    pickScanTypeName.add("Quét mã vận đơn bị delay")
                    pickScanTypeName.add("Quét mã bảng kê bị delay")
                    val arrayAdapter = ArrayAdapter<String>(
                            this, // Context
                            android.R.layout.simple_list_item_single_choice, // Layout
                            pickScanTypeName // List
                    )

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Chọn lý do")
                    builder.setSingleChoiceItems(
                            arrayAdapter, // Items list
                            -1 // Index of checked item (-1 = no selection)
                    ) { v , i ->
                        // Item click listener
                        // Get the alert dialog selected item's text
                        selectledScanType = i
                        btScanType.setText(pickScanTypeName[i])
                        if (selectledScanType == 0)
                        {
                            txtShipmentCode.hint = "Quét mã vận đơn"
                        }
                        else
                        {
                            txtShipmentCode.hint = "Quét mã bảng kê"
                        }
                        v.dismiss()
                        // Display the selected item's text on snack bar
                    }

                    builder.show()
                }
                v.id == btSelectReason.id -> GeneralProcess().getReasonDelay(this) {
                    val allReason = Reason.Deserializer().deserialize(it)!!
                    val pickRejectReason: ArrayList<Reason> = ArrayList()
                    val pickRejectReasonName: ArrayList<String> = ArrayList()
                    allReason.forEach { r ->
                        pickRejectReason.add(r)
                        pickRejectReasonName.add(r.name)
                    }

                    val arrayAdapter = ArrayAdapter<String>(
                            this, // Context
                            android.R.layout.simple_list_item_single_choice, // Layout
                            pickRejectReasonName // List
                    )

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Chọn lý do")
                    builder.setSingleChoiceItems(
                            arrayAdapter, // Items list
                            -1 // Index of checked item (-1 = no selection)
                    ) { v , i ->
                        // Item click listener
                        // Get the alert dialog selected item's text
                        selectedReason = pickRejectReason[i]
                        btSelectReason.setText(selectedReason!!.name)
                        v.dismiss()
                        // Display the selected item's text on snack bar
                    }

                    builder.show()
                }
            }
        }
    }

    fun resetForm()
    {
        txtShipmentCode.setText("")
        editDelayTime.setText("")
        editTextNote.setText("")
    }

    private fun setupUI(view: View) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                view.clearFocus()
                hideSoftKeyboard(this@AddDelayActivity)
                false
            }
        }

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            (0 until view.childCount)
                    .map { view.getChildAt(it) }
                    .forEach(this::setupUI)
        }
    }

    private fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus?.windowToken, 0)
    }

    private fun getBarcodeScan() {
        ZXingUtils.initiateScan(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            try {
                if (requestCode == ZXingUtils.REQUEST_CODE) {
                    val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                    Log.d("RequestCode", "ZXingUtils.REQUEST_CODE: " + ZXingUtils.REQUEST_CODE + " - " + result!!.contents)
                    txtShipmentCode.setText(result.contents)
                }
            } catch (e: OutOfMemoryError) {
                ToastUtils.error(this, "Không đủ dung lượng để lưu ảnh!")
            } catch (e: Exception) {
                ToastUtils.error(this, e.localizedMessage)
            }
        }
    }
}
