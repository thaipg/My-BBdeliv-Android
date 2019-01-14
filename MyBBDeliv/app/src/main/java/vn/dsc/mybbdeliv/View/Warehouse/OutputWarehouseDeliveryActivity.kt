package vn.dsc.mybbdeliv.View.Warehouse

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator
import dsc.vn.mybbdeliv.Base.BaseActivity
import dsc.vn.mybbdeliv.Camera.ZXingUtils
import dsc.vn.mybbdeliv.Model.Item
import dsc.vn.mybbdeliv.Model.Shipment
import dsc.vn.mybbdeliv.Process.GeneralProcess
import dsc.vn.mybbdeliv.Process.ShipmentProcess
import dsc.vn.mybbdeliv.R
import dsc.vn.mybbdeliv.Utils.ToastUtils
import kotlinx.android.synthetic.main.content_output_warehouse_delivery.*
import org.json.JSONObject
import vn.dsc.mybbdeliv.Model.ListGoodsInput
import vn.dsc.mybbdeliv.Model.Truck
import vn.dsc.mybbdeliv.Process.WarehouseProcess

class OutputWarehouseDeliveryActivity : BaseActivity() , View.OnClickListener {

    private var allShipment: MutableList<Shipment> = mutableListOf()

    private var resultListEmp:MutableList<Item> = mutableListOf()
    private var resultListTruck:MutableList<Truck> = mutableListOf()

    private var adapterEmp: ArrayAdapter<String>? = null
    private var adapterTruck: ArrayAdapter<String>? = null
    private var adapterShipment: ArrayAdapter<String>? = null

    private var selectListGoods: ListGoodsInput? = null
    private var selectEmp: Item? = null
    private var selectTruck: Truck? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_output_warehouse_delivery)
        super.onCreate(savedInstanceState)

        prepareUI()
        bindData()
    }

    private fun prepareUI() {
        setupUI(findViewById<View>(clLayout.id))
        imageView_scan_camera.setOnClickListener(this)
        imageView_shipment_plus.setOnClickListener(this)
        btnUpdate.setOnClickListener(this)

        txtEmployee.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if (s.length >= 2) {
                        GeneralProcess().getEmployeeSearchByValue(s.toString()) {
                            Log.v("Emp :", it)
                            // If the response is JSONObject instead of expected JSONArray
                            resultListEmp = Item.Deserializer().deserialize(it)!!
                            setupAutocompleteViewEmployee()
                        }
                    }
                }
            }
        })

        txtEmployee.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            selectEmp = resultListEmp[position]
            WarehouseProcess().getListGoodsDeliveryNew(selectEmp!!.id.toString())
            {
                Log.v("BK :", it)
                if (it == "[]")
                {
                    WarehouseProcess().listGoodsCreateDelivery(selectEmp!!.id.toString())
                    {
                        Log.v("BK : ", it);
                        val listGood = ListGoodsInput.Deserializer().deserializeSingle(it)
                        selectListGoods = listGood
                        txtBKCode.setText(selectListGoods!!.code)
                        this.bindData()
                    }
                }
                else {
                    val listGoods = ListGoodsInput.Deserializer().deserialize(it)
                    if (listGoods != null) {
                        for (listGood in listGoods) {
                            selectListGoods = listGood
                            txtBKCode.setText(selectListGoods!!.code)
                            this.bindData()
                        }
                    }
                }
            }
        }


        txtTruck.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if (s.length >= 2) {
                        WarehouseProcess().searchByTruckNumber(s.toString()) {
                            Log.v("Truck :", it)
                            // If the response is JSONObject instead of expected JSONArray
                            resultListTruck = Truck.Deserializer().deserialize(it)!!
                            setupAutocompleteViewTruck()
                        }
                    }
                }
            }
        })

        txtTruck.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            selectTruck = resultListTruck[position]
            if (selectTruck!!.truckOwnershipId == 2)
            {
                txtFeeRent.visibility = View.VISIBLE
            }
            else
            {
                txtFeeRent.visibility = View.GONE
            }
        }
    }

    private fun setupAutocompleteViewEmployee()
    {
        // Get the string array
        val pickEmp = java.util.ArrayList<Item>()
        val pickEmpName = java.util.ArrayList<String>()
        // Create the adapter and set it to the AutoCompleteTextView
        adapterEmp = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_dropdown_item, pickEmpName)
        for (r in resultListEmp) {
            pickEmp.add(r)
            pickEmpName.add(r.code + " - " + r.name)
        }
        txtEmployee.setAdapter(adapterEmp)
        txtEmployee.setTextColor(Color.BLACK)
        txtEmployee.setHintTextColor(Color.LTGRAY)
        txtEmployee.highlightColor = Color.WHITE
        txtEmployee.setLinkTextColor(Color.WHITE)
        txtEmployee.showDropDown()
    }

    private fun setupAutocompleteViewTruck()
    {
        // Get the string array
        val pickTruck = java.util.ArrayList<Truck>()
        val pickTruckName = java.util.ArrayList<String>()
        // Create the adapter and set it to the AutoCompleteTextView
        adapterTruck = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_dropdown_item, pickTruckName)
        for (r in resultListTruck) {
            pickTruck.add(r)
            pickTruckName.add(r.truckNumber + " - " + r.truckOwnershipName + " - " + r.truckTypeName)
        }
        txtTruck.setAdapter(adapterTruck)
        txtTruck.setTextColor(Color.BLACK)
        txtTruck.setHintTextColor(Color.LTGRAY)
        txtTruck.highlightColor = Color.WHITE
        txtTruck.setLinkTextColor(Color.WHITE)
        txtTruck.showDropDown()
    }
    private fun bindData() {
        if (selectListGoods != null) {
            ShipmentProcess().getByListGoodsId(this, selectListGoods!!.id)
            {
                allShipment = Shipment.Deserializer().deserialize(it)!!
                val shipmentName = java.util.ArrayList<String>()
                // Create the adapter and set it to the AutoCompleteTextView
                adapterShipment = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_dropdown_item, shipmentName)
                for (r in allShipment) {
                    shipmentName.add(r.shipmentNumber)
                }
                listView.adapter = adapterShipment
                val empty = findViewById<TextView>(empty.id)
                listView.emptyView = empty
                (listView.adapter as ArrayAdapter<*>).notifyDataSetChanged()
                textView_shipment_count.text = listView.count.toString()
            }
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when {
                v.id == imageView_scan_camera.id -> {
                    if (selectListGoods != null && selectEmp != null) {
                        getBarcodeScan()
                    } else {
                        ToastUtils.error(this, "Chưa chọn nhân viên báo phát !")
                    }
                }
                v.id == imageView_shipment_plus.id -> {
                    if (selectListGoods != null && selectEmp != null) {
                        ShipmentProcess().issueDelivery(this, selectListGoods!!.id, txtShipmentCode.text.toString()) {
                            ToastUtils.success(this, "Xuất kho trung chuyển mã vận đơn [${txtShipmentCode.text}] !")
                            this.bindData()
                        }
                    } else {
                        ToastUtils.error(this, "Chưa chọn nhân viên báo phát !")
                    }
                }
                v.id == btnUpdate.id -> {
                        if (selectEmp != null)
                        {
                            selectListGoods!!.listGoodsTypeId = 3
                            selectListGoods!!.empId = selectEmp!!.id
                            if (selectTruck!!.truckOwnershipId == 2)
                            {
                                if(txtFeeRent.text.isEmpty())
                                {
                                    ToastUtils.error(this, "Chưa thêm phí trung chuyển !")
                                    return
                                }
                                else
                                {
                                    if (java.lang.Double.parseDouble(txtFeeRent.text.toString()) == 0.0)
                                    {
                                        ToastUtils.error(this, "Phí trung chuyển phải > 0!")
                                        return
                                    }
                                    else
                                    {
                                        selectListGoods!!.feeRent = java.lang.Double.parseDouble(txtFeeRent.text.toString())
                                    }
                                }
                            }
                            completeListGoods()
                        }
                        else
                        {
                            ToastUtils.error(this, "Chưa chọn nhân viên trung chuyển !")
                            return
                        }
                }
            }
        }
    }

    private fun completeListGoods() {
        val jsonString = Gson().toJson(selectListGoods)
        val json = JSONObject(jsonString)
        WarehouseProcess().blockListGoods(this, json) {
            Log.v("Result :", it)
            clearAll()
            ToastUtils.success(this, "Xuất kho phát thành công!")
        }
    }

    private fun clearAll() {
        txtBKCode.setText("")
        txtEmployee.setText("")
        txtTruck.setText("")
        txtTruck.visibility = View.GONE
        txtFeeRent.setText("")
        txtFeeRent.visibility = View.GONE
        txtShipmentCode.setText("")
        selectListGoods = null
        selectEmp = null
        selectTruck = null
        allShipment = mutableListOf()
        val shipmentName = java.util.ArrayList<String>()
        // Create the adapter and set it to the AutoCompleteTextView
        adapterShipment = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_dropdown_item, shipmentName)
        for (r in allShipment) {
            shipmentName.add(r.shipmentNumber)
        }
        listView.adapter = adapterShipment
        val empty = findViewById<TextView>(empty.id)
        listView.emptyView = empty
        (listView.adapter as ArrayAdapter<*>).notifyDataSetChanged()
        textView_shipment_count.text = listView.count.toString()
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
                    if (selectListGoods != null && selectEmp != null) {
                        ShipmentProcess().issueDelivery(this, selectListGoods!!.id, result.contents) {
                            ToastUtils.success(this, "Xuất kho trung chuyển mã vận đơn [${result.contents}] !")
                            this.bindData()
                        }
                    }
                    else
                    {
                        ToastUtils.error(this, "Chưa chọn nhân viên trung chuyển !")
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
                hideSoftKeyboard(this@OutputWarehouseDeliveryActivity)
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