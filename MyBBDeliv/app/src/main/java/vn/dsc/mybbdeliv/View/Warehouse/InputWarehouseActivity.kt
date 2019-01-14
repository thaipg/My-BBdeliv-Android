package vn.dsc.mybbdeliv.View.Warehouse

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.google.zxing.integration.android.IntentIntegrator
import dsc.vn.mybbdeliv.Base.BaseActivity
import dsc.vn.mybbdeliv.Camera.ZXingUtils
import dsc.vn.mybbdeliv.ListViewAdapter.CustomAdapter
import dsc.vn.mybbdeliv.Model.Shipment
import dsc.vn.mybbdeliv.R
import dsc.vn.mybbdeliv.Utils.ToastUtils
import kotlinx.android.synthetic.main.content_input_warehouse.*
import org.json.JSONObject
import vn.dsc.mybbdeliv.Model.ListGoodsInput
import vn.dsc.mybbdeliv.Process.WarehouseProcess

class InputWarehouseActivity: BaseActivity() , View.OnClickListener {

    private var allShipment: MutableList<Shipment> = mutableListOf()
    private var listGoodsId: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_input_warehouse)
        super.onCreate(savedInstanceState)

        prepareUI()
        bindData()
    }

    private fun prepareUI() {
        setupUI(findViewById<View>(clLayout.id))
        imageView_scan_camera.setOnClickListener(this)
    }

    private fun bindData() {
        listView.adapter = CustomAdapter(this, ArrayList(allShipment))
        val empty = findViewById<TextView>(empty.id)
        listView.emptyView = empty
        (listView.adapter as CustomAdapter).notifyDataSetChanged()
        textView_shipment_count.text =  listView.count.toString()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when {
                v.id == imageView_scan_camera.id -> getBarcodeScan()
            }
        }
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
                    if (result.contents != null) {
                        if (txtBKCode.text.isEmpty())
                        {
                            WarehouseProcess().createListGoods(this)
                            {
                                val warehouse = ListGoodsInput.Deserializer().deserializeSingle(it)
                                val json = JSONObject()
                                listGoodsId = warehouse.id
                                json.put("listGoodsId", warehouse.id)
                                json.put("typeWareHousing", 2)
                                json.put("shipmentNumber", result.contents)
                                txtBKCode.setText(warehouse.code)

                                WarehouseProcess().receiptWarehousing(this, json)
                                {
                                    val shipment = Shipment.Deserializer().deserializeSingle(it)!!
                                    allShipment.add(shipment)
                                    bindData()
                                }
                            }
                        }
                        else
                        {
                            if (listGoodsId != null)
                            {
                                val json = JSONObject()
                                json.put("listGoodsId", listGoodsId!!)
                                json.put("typeWareHousing", 2)
                                json.put("shipmentNumber", result.contents)

                                WarehouseProcess().receiptWarehousing(this, json)
                                {
                                    val shipment = Shipment.Deserializer().deserializeSingle(it)!!
                                    allShipment.add(shipment)
                                    bindData()
                                }
                            }
                        }
                    }
                }
            } catch (e: OutOfMemoryError) {
                ToastUtils.error(this, "Không đủ dung lượng để lưu ảnh!")
            } catch (e: Exception) {
                ToastUtils.error(this, e.localizedMessage)
            }
        }
    }

    private fun setupUI(view: View) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                view.clearFocus()
                hideSoftKeyboard(this@InputWarehouseActivity)
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
