package dsc.vn.mybbdeliv.View.ReadyToTransit.CreateShipment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import com.google.zxing.integration.android.IntentIntegrator
import dsc.vn.mybbdeliv.Base.BaseSubActivity
import dsc.vn.mybbdeliv.Camera.ZXingUtils
import dsc.vn.mybbdeliv.Extension.isNotEmptySelector
import dsc.vn.mybbdeliv.Extension.isNotEmptyTextField
import dsc.vn.mybbdeliv.Extension.toDateTime
import dsc.vn.mybbdeliv.Model.*
import dsc.vn.mybbdeliv.Process.GeneralProcess
import dsc.vn.mybbdeliv.R
import dsc.vn.mybbdeliv.Utils.ToastUtils
import kotlinx.android.synthetic.main.content_step3.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class Step3Activity : BaseSubActivity(), View.OnClickListener {

    private var shipment: Shipment = Shipment()
    private var structure: Structure? = null
    private var service: Service? = null
    private var typePay: PaymentType? = null
    private var serviceDVGT: MutableList<ServiceDVGT> = mutableListOf()
    override val layoutResourceId: Int
        get() {
            return R.layout.activity_shipment_pickup_request_reject
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step3)
        prepareUI()
        bindData()
    }

    private fun prepareUI() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setupUI(findViewById<View>(clLayout.id))
        txtWeight.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                calculated()
            }
        }
        txtCOD.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                calculated()
            }
        }
        txtInsured.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                calculated()
            }
        }
        imageView_camera.setOnClickListener(this)
        btService.setOnClickListener(this)
        btStructure.setOnClickListener(this)
        btTypePay.setOnClickListener(this)
        btServiceDVGT.setOnClickListener(this)
        btnNext.setOnClickListener(this)
    }

    private fun bindData() {
        val intent = intent
        val bundle = intent.extras
        shipment = (bundle!!.getSerializable("Shipments") as Shipment)
        if (shipment.structure != null) {
            structure = shipment.structure
            btStructure.text = structure!!.name
        }

        if (shipment.service != null) {
            service = shipment.service
            btService.text = service!!.name
        }

        if (shipment.weight != null)
            txtWeight.setText(shipment.weight.toString())

        if (!txtWeight.text.isNullOrEmpty() && service != null) {
            calculated()
        }

        if (shipment.cod != null && shipment.cod != 0.0) {
            txtCOD.setText(shipment.cod.toString())
        }
        if (shipment.insured != null && shipment.insured != 0.0) {
            txtInsured.setText(shipment.insured.toString())
        }
        if (shipment.expectedDeliveryTime != null)
        {
            editText_shipment_time_expect.setText("Dự kiến giao: " + (shipment.expectedDeliveryTime))
        }
    }

    var data: Calculate? = null
    private fun calculated() {
        if (service == null)
        {
            return
        }
        if (txtWeight.text.isNullOrEmpty()) {
            return
        }

        var cod = 0.0
        var insured = 0.0

        if (!txtCOD.text.isNullOrEmpty())
            cod = txtCOD.text.toString().toDouble()
        if (!txtInsured.text.isNullOrEmpty())
            insured = txtInsured.text.toString().toDouble()

        val dvgt = JSONArray(serviceDVGT.map { it.id })
        val json = JSONObject()

        json.put("SenderId", shipment.senderId)
        json.put("FromWardId", shipment.fromWardId)
        json.put("ToDistrictId", shipment.toDistrictId)
        json.put("ServiceId", service!!.id)
        json.put("Weight", txtWeight.text)
        json.put("COD", cod)
        json.put("Insured", insured)
        json.put("OtherPrice", "0")
        json.put("ServiceDVGTIds", dvgt)

        Log.v("JSON", json.toString())
        GeneralProcess().getCalculate(this, json, {
            data = Calculate.Deserializer().deserialize(it)
            print(it)
            lblPhiVanChuyen.text = data!!.defaultPrice.toString()
            //lblPhiCOD.text = result["codPrice"]!.description.twoFractionDigits + " VNĐ"
            //lblPhiKhaiGia.text = result["insuredPrice"]!.description.twoFractionDigits + " VNĐ"
            lblPPXD.text = data!!.fuelPrice.toString()
            lblPhiDVGT.text = data!!.totalDVGT.toString()
            //lblPhiDongGoi.text = result["packPrice"]!.description.twoFractionDigits + " VNĐ"
            lblVat.text = data!!.vatPrice.toString()
            lblTongCong.text = data!!.totalPrice.toString()
        })
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when {
                v.id == btnNext.id -> nextStep()
                v.id == btStructure.id -> getStructure()
                v.id == btService.id -> getService()
                v.id == btTypePay.id -> getTypePay()
                v.id == btServiceDVGT.id -> getServiceDVGT()
                v.id == imageView_camera.id -> getBarcodeScan()
            }
        }
    }

    private fun getBarcodeScan() {
        ZXingUtils.initiateScan(this)
    }

    private fun getServiceDVGT() {
        GeneralProcess().getServiceDVGT(this, {
            val allServiceDVGT = ServiceDVGT.Deserializer().deserialize(it)!!
            val pickServiceDVGT: ArrayList<ServiceDVGT> = ArrayList()
            val pickServiceDVGTName: ArrayList<CharSequence> = ArrayList()
            val checkedServiceDVGT: ArrayList<Boolean> = ArrayList()

            allServiceDVGT.forEach { r ->
                pickServiceDVGT.add(r)
                pickServiceDVGTName.add(r.name)
                if (serviceDVGT.contains(r)) {
                    checkedServiceDVGT.add(true)
                } else {
                    checkedServiceDVGT.add(false)
                }
            }

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Chọn dịch vụ gia tăng")
            val entries: Array<CharSequence> = pickServiceDVGTName.toTypedArray()
            val checkedEntries: BooleanArray = checkedServiceDVGT.toBooleanArray()
            val selectedValues: ArrayList<Int> = arrayListOf()
            builder.setMultiChoiceItems(entries, checkedEntries, { di, i, b ->
                if (b) {
                    serviceDVGT.add(pickServiceDVGT[i])
                    selectedValues.add(i)
                } else {
                    serviceDVGT.remove(pickServiceDVGT[i])
                    selectedValues.remove(i)
                }
                val subtitle = selectedValues.joinToString { entries[it] }
                btServiceDVGT.text = subtitle
                calculated()
            })
            builder.show()
        })
    }

    private fun getTypePay() {
        GeneralProcess().getPaymentType(this, {
            val allPaymentType = PaymentType.Deserializer().deserialize(it)!!
            val pickPaymentType: ArrayList<PaymentType> = ArrayList()
            val pickPaymentTypeName: ArrayList<String> = ArrayList()
            allPaymentType.forEach { r ->
                pickPaymentType.add(r)
                pickPaymentTypeName.add(r.name)
            }

            val arrayAdapter = ArrayAdapter<String>(
                    this, // Context
                    android.R.layout.simple_list_item_single_choice, // Layout
                    pickPaymentTypeName // List
            )

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Chọn hình thức thanh toán")
            builder.setSingleChoiceItems(
                    arrayAdapter, // Items list
                    -1 // Index of checked item (-1 = no selection)
            ) { v, i ->
                // Item click listener
                // Get the alert dialog selected item's text
                typePay = pickPaymentType[i]
                btTypePay.text = pickPaymentType[i].name
                v.dismiss()
                // Display the selected item's text on snack bar
            }
            builder.show()
        })
    }

    private fun getService() {
        if (txtWeight.isNotEmptyTextField(this, "Vui lòng chọn trọng lượng trước !")) {
            return
        }
        val json = JSONObject()
        json.put("SenderId", shipment.senderId)
        json.put("FromWardId", shipment.fromWardId)
        json.put("ToDistrictId", shipment.toDistrictId)
        json.put("Weight", txtWeight.text)
        GeneralProcess().getPriceListService(this, json, {
            val allPriceService = PriceService.Deserializer().deserialize(it)!!
            val pickPriceService: ArrayList<PriceService> = ArrayList()
            val pickPriceServiceName: ArrayList<String> = ArrayList()
            allPriceService.forEach { r ->
                pickPriceService.add(r)
                pickPriceServiceName.add(r.name + " - " + r.price)
            }

            val arrayAdapter = ArrayAdapter<String>(
                    this, // Context
                    android.R.layout.simple_list_item_single_choice, // Layout
                    pickPriceServiceName // List
            )

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Chọn dịch vụ")
            builder.setSingleChoiceItems(
                    arrayAdapter, // Items list
                    -1 // Index of checked item (-1 = no selection)
            ) { v, i ->
                // Item click listener
                // Get the alert dialog selected item's text
                service = Service()
                service!!.id = pickPriceService[i].id
                service!!.name = pickPriceService[i].name
                if (pickPriceService[i].expectedDeliveryTime != null)
                {
                    if (!pickPriceService[i].expectedDeliveryTime.isNullOrBlank())
                    {
                        shipment.expectedDeliveryTime = pickPriceService[i].expectedDeliveryTime
                        editText_shipment_time_expect.setText("Dự kiến giao : " + shipment.expectedDeliveryTime.toDateTime())
                    }
                }

                btService.text = pickPriceService[i].name + " - " + pickPriceService[i].price
                calculated()
                v.dismiss()
                // Display the selected item's text on snack bar
            }
            builder.show()
        })
    }

    private fun getStructure() {
        GeneralProcess().getStructure(this, {
            val allStructure = Structure.Deserializer().deserialize(it)!!
            val pickStructure: ArrayList<Structure> = ArrayList()
            val pickStructureName: ArrayList<String> = ArrayList()
            allStructure.forEach { r ->
                pickStructure.add(r)
                pickStructureName.add(r.name)
            }

            val arrayAdapter = ArrayAdapter<String>(
                    this, // Context
                    android.R.layout.simple_list_item_single_choice, // Layout
                    pickStructureName // List
            )

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Chọn nhóm hàng hoá")
            builder.setSingleChoiceItems(
                    arrayAdapter, // Items list
                    -1 // Index of checked item (-1 = no selection)
            ) { v, i ->
                // Item click listener
                // Get the alert dialog selected item's text
                structure = pickStructure[i]
                btStructure.text = structure!!.name
                v.dismiss()
                // Display the selected item's text on snack bar
            }
            builder.show()
        })
    }

    private fun nextStep() {
        if (txtShipmentCode.isNotEmptyTextField(this, "Vui lòng nhập mã vận đơn")) {
            return
        }
        if (btStructure.isNotEmptySelector(this, "Vui lòng chọn nhóm hàng ", structure)) {
            return
        }
        if (btService.isNotEmptySelector(this, "Vui lòng chọn nhóm hàng ", service)) {
            return
        }
        if (btTypePay.isNotEmptySelector(this, "Vui lòng chọn nhóm hàng ", typePay)) {
            return
        }
        if (lblPhiVanChuyen.text == "0") {
            ToastUtils.error(this, "Khu vực này chưa có mức giá dịch vụ !")
            return
        }

        val dvgt = serviceDVGT.map { it.id }

        shipment.shipmentNumber = txtShipmentCode.text.toString()
        shipment.serviceId = service!!.id
        shipment.service = service
        shipment.weight = txtWeight.text.toString().toDouble()
        shipment.structureId = structure!!.id
        shipment.paymentTypeId = typePay!!.id
        if (txtPackNumber.text.isNullOrEmpty())
        {
            shipment.totalBox = 1
        }
        else
        {
            shipment.totalBox = txtPackNumber.text.toString().toInt()
        }

        var cod = 0.0
        var insured = 0.0

        if (!txtCOD.text.isNullOrEmpty())
            cod = txtCOD.text.toString().toDouble()
        if (!txtInsured.text.isNullOrEmpty())
            insured = txtInsured.text.toString().toDouble()

        shipment.cod = cod
        shipment.insured = insured
        shipment.defaultPrice = data!!.defaultPrice
        shipment.totalDVGT = data!!.totalDVGT
        shipment.vatPrice = data!!.vatPrice
        shipment.fuelPrice = data!!.fuelPrice
        shipment.totalPrice = data!!.totalPrice
        shipment.serviceDVGTIds = dvgt
        val bundle = Bundle()
        bundle.putSerializable(
                "Shipments",
                shipment
        )

        val intent = Intent(this, Step4Activity::class.java)
        intent.putExtras(bundle)
        startActivityForResult(intent, 1)
    }

    private fun setupUI(view: View) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                view.clearFocus()
                hideSoftKeyboard(this@Step3Activity)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
