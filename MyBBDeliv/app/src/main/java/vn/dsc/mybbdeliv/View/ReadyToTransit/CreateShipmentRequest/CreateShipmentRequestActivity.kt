package dsc.vn.mybbdeliv.View.ReadyToTransit.CreateShipmentRequest

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import com.google.zxing.integration.android.IntentIntegrator
import dsc.vn.mybbdeliv.Base.BaseSubActivity
import dsc.vn.mybbdeliv.Camera.ZXingUtils
import dsc.vn.mybbdeliv.Extension.isNotEmptyTextField
import dsc.vn.mybbdeliv.Model.Department
import dsc.vn.mybbdeliv.Model.Shipment
import dsc.vn.mybbdeliv.Process.CustomerProcess
import dsc.vn.mybbdeliv.Process.ShipmentProcess
import dsc.vn.mybbdeliv.R
import dsc.vn.mybbdeliv.Service.JsonWebservice
import dsc.vn.mybbdeliv.Utils.ToastUtils
import dsc.vn.mybbdeliv.View.ReadyToTransit.ReadyToTransitActivity
import kotlinx.android.synthetic.main.content_create_shipment_request.*
import java.util.*

class CreateShipmentRequestActivity : BaseSubActivity() , View.OnClickListener {

    private var shipment: Shipment = Shipment()
    private var department: Department? = null

    override val layoutResourceId: Int
        get() {
            return R.layout.activity_create_shipment_request
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_shipment_request)
        prepareUI()
        bindData()
    }

    private fun prepareUI() {
        setupUI(findViewById<View>(clLayout.id))
    }

    private fun bindData() {
        val intent = intent
        val bundle = intent.extras
        shipment = (bundle!!.getSerializable("Shipments") as Shipment)
        txtShipmentCode.setText(shipment.shipmentNumber)
        imageView_camera.setOnClickListener(this)
        btDepartment.setOnClickListener(this)
        btSubmit.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when {
                v.id == btSubmit.id -> submit()
                v.id == imageView_camera.id -> getBarcodeScan()
                v.id == btDepartment.id -> getDepartment()
            }
        }
    }

    private fun getDepartment() {
        CustomerProcess().getDepartment(this, shipment.senderId!!.toString(), {
            val allDepartment = Department.Deserializer().deserialize(it)!!
            val pickDepartment: ArrayList<Department> = ArrayList()
            val pickDepartmentName: ArrayList<String> = ArrayList()
            allDepartment.forEach { r ->
                pickDepartment.add(r)
                pickDepartmentName.add(r.name)
            }

            val arrayAdapter = ArrayAdapter<String>(
                    this, // Context
                    android.R.layout.simple_list_item_single_choice, // Layout
                    pickDepartmentName // List
            )

            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Chọn phòng ban")
            builder.setSingleChoiceItems(
                    arrayAdapter, // Items list
                    -1 // Index of checked item (-1 = no selection)
            ) { v, i ->
                // Item click listener
                // Get the alert dialog selected item's text
                department = pickDepartment[i]
                btDepartment.text = pickDepartment[i].name
                v.dismiss()
                // Display the selected item's text on snack bar
            }
            builder.show()
        })
    }

    private fun submit() {
        if (txtShipmentCode.isNotEmptyTextField(this, "Vui lòng nhập mã vận đơn")) {
            return
        }
        if (txtCountShipment.isNotEmptyTextField(this, "Vui lòng nhập trọng lượng!")) {
            return
        }
        if (txtWeight.isNotEmptyTextField(this, "Vui lòng nhập trọng lượng!")) {
            return
        }
        if (txtCalWeight.isNotEmptyTextField(this, "Vui lòng nhập trọng lượng quy đổi!")) {
            return
        }
        if (txtTotalBox.isNotEmptyTextField(this, "Vui lòng nhập số kiện!")) {
            return
        }

        shipment.shipmentNumber = txtShipmentCode.text.toString()
        shipment.countShipment = txtCountShipment.text.toString().toInt()
        if (JsonWebservice().post_api_endPoint == "http://postapi.bbdeliv.vn/api")
        {
            shipment.weight = txtWeight.text.toString().toDouble()
            shipment.calWeight = txtCalWeight.text.toString().toDouble()
        }
        else
        {
            shipment.weight = txtWeight.text.toString().toDouble() * 1000
            shipment.calWeight = txtCalWeight.text.toString().toDouble() * 1000
        }
        shipment.totalBox = txtTotalBox.text.toString().toInt()

        if (shipment.countShipment!! <= 0)
        {
            txtCountShipment.requestFocus()
            txtCountShipment.error = "Số vận đơn phải lớn hơn 0!"
            return
        }

        shipment.fromWard = null
        shipment.toWard = null
        shipment.service = null
        shipment.sender = null
        shipment.ladingSchedules = null
        if (department != null)
        {
            shipment.cusDepartmentId = department!!.id
        }
        if (!txtNote.text.isEmpty())
        {
            shipment.note = txtNote.text.toString()
        }
        val message = "Mã đơn hàng tổng : " + shipment.shipmentNumber +
                "\nSố lượng đơn hàng : " + shipment.countShipment.toString() +
                "\nKhối lượng (kg) : " + txtWeight.text.toString() +
                "\nKhối lượng quy đổi (kg) : " + txtCalWeight.text.toString() +
                "\nSố kiện : " + shipment.totalBox.toString()

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Xác nhận")
        dialogBuilder.setMessage(message)
        dialogBuilder.setPositiveButton("Đóng", null)
        dialogBuilder.setNegativeButton("Xác nhận", { v , w ->
            ShipmentProcess().acceptCompleteByCurrentEmp(
                    this,
                    shipment,
                    {
                        val message2 = "Đã lấy hàng thành công . Số mã vận đơn tổng : " + shipment.shipmentNumber +
                                ".Số lượng đơn hàng : " + shipment.countShipment.toString()
                        val intent = Intent(this, ReadyToTransitActivity::class.java)
                        startActivity(intent)
                        ToastUtils.success(this,message2)
                    }
            )
        })
        dialogBuilder.show()
    }


    private fun getBarcodeScan() {
        ZXingUtils.initiateScan(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == ZXingUtils.REQUEST_CODE) {
                val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                Log.d("RequestCode", "ZXingUtils.REQUEST_CODE: " + ZXingUtils.REQUEST_CODE + " - " + result!!.contents)
                if (result.contents != null) {
                    txtShipmentCode.setText(result.contents)
                }
            }
        }
    }

    private fun setupUI(view: View) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                view.clearFocus()
                hideSoftKeyboard(this@CreateShipmentRequestActivity)
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
}
