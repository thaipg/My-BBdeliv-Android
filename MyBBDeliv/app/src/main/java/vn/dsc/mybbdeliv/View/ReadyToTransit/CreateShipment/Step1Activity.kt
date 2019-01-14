package dsc.vn.mybbdeliv.View.ReadyToTransit.CreateShipment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import kotlinx.android.synthetic.main.content_step1.*
import dsc.vn.mybbdeliv.Base.BaseSubActivity
import dsc.vn.mybbdeliv.Extension.isNotEmptySelector
import dsc.vn.mybbdeliv.Extension.isNotEmptyTextField
import dsc.vn.mybbdeliv.Model.*
import dsc.vn.mybbdeliv.Process.GeneralProcess
import dsc.vn.mybbdeliv.R
import dsc.vn.mybbdeliv.Utils.LocationUtils
import dsc.vn.mybbdeliv.Utils.ToastUtils
import java.util.*

class Step1Activity : BaseSubActivity() , View.OnClickListener {

    private var shipment: Shipment = Shipment()
    private var province: Province? = null
    private var district: District? = null
    private var ward: Ward? = null
    private var hub: Hub? = null

    override val layoutResourceId: Int
        get() {
            return R.layout.activity_shipment_pickup_request_reject
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step1)
        prepareUI()
        bindData()
    }

    private fun prepareUI()
    {
        setupUI(findViewById<View>(clLayout.id))
        btProvince.setOnClickListener(this)
        btDistrict.setOnClickListener(this)
        btWard.setOnClickListener(this)
        btnNext.setOnClickListener(this)
        txtAddress.setOnClickListener(this)
    }

    private fun bindData()
    {
        val intent = intent
        val bundle = intent.extras
        shipment = (bundle!!.getSerializable("Shipments") as Shipment)

        if (shipment == null)
        {
            ToastUtils.warning(this,"Lỗi dữ liệu , vui lòng chọn lại vận đơn !")
        }
        else
        {
            txtName.setText(shipment.senderName)
            txtPhone.setText(shipment.senderPhone)
            txtAddress.text = shipment.pickingAddress
            txtAddressNote.setText(shipment.addressNoteFrom)
            txtCompany.setText(shipment.companyFrom)

            val newHub = Hub()
            newHub.id = shipment.fromHubId!!
            hub = newHub

            if (shipment.fromWard != null) {
                ward = shipment.fromWard
                btWard.text = ward?.name
                btWard.setTextColor(Color.parseColor("#000000"))

                if (shipment.fromWard!!.district != null) {
                    district = shipment.fromWard!!.district
                    btDistrict.text = district?.name
                    btDistrict.setTextColor(Color.parseColor("#000000"))

                    if (shipment.fromWard!!.district!!.province != null) {
                        province = shipment.fromWard!!.district!!.province
                        btProvince.text = province?.name
                        btProvince.setTextColor(Color.parseColor("#000000"))
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            if (v.id == btnNext.id)
            {
               nextStep()
            }
            else if (v.id == btProvince.id)
            {
                getProvince()
            }
            else if (v.id == btDistrict.id)
            {
                getDistrict()
            }
            else if (v.id == btWard.id)
            {
                getWard()
            }
            else if (v.id == txtAddress.id)
            {
                loadActivityAddress()
            }
        }
    }



    private fun getWard() {
        if (district == null)
        {
            ToastUtils.warning(this,"Vui lòng chọn quận huyện !")
            return
        }
        GeneralProcess().getWardByDistrictID(this, district!!.id.toString(), {
            val allWard = Ward.Deserializer().deserialize(it)!!
            val pickWard: ArrayList<Ward> = ArrayList()
            val pickWardName: ArrayList<String> = ArrayList()
            allWard.forEach { r ->
                pickWard.add(r)
                pickWardName.add(r.name)
            }

            val arrayAdapter = ArrayAdapter<String>(
                    this, // Context
                    android.R.layout.simple_list_item_single_choice, // Layout
                    pickWardName // List
            )

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Chọn phường xã")
            builder.setSingleChoiceItems(
                    arrayAdapter, // Items list
                    -1 // Index of checked item (-1 = no selection)
            ) { v , i ->
                // Item click listener
                // Get the alert dialog selected item's text
                ward = pickWard[i]
                btWard.text = ward!!.name
                hub = null
                GeneralProcess().getHubByWardId(this, ward!!.id.toString(), {
                    if (!it.isNullOrEmpty()) {
                        val dataHub = Hub.Deserializer().deserializeSingle(it)
                        hub = dataHub
                    } else {
                        ToastUtils.error(this, "Không hỗ trợ lấy hàng tại địa điểm này !")
                    }
                })
                v.dismiss()
                // Display the selected item's text on snack bar
            }
            builder.show()
        })
    }

    private fun getDistrict() {
        if (province == null)
        {
            ToastUtils.warning(this,"Vui lòng chọn tỉnh thành  !")
            return
        }
        GeneralProcess().getDistrictByProvinceID(this, province!!.id.toString(), {
            val allDistrict = District.Deserializer().deserialize(it)!!
            val pickDistrict: ArrayList<District> = ArrayList()
            val pickDistrictName: ArrayList<String> = ArrayList()
            allDistrict.forEach { r ->
                pickDistrict.add(r)
                pickDistrictName.add(r.name)
            }

            val arrayAdapter = ArrayAdapter<String>(
                    this, // Context
                    android.R.layout.simple_list_item_single_choice, // Layout
                    pickDistrictName // List
            )

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Chọn quận huyện")
            builder.setSingleChoiceItems(
                    arrayAdapter, // Items list
                    -1 // Index of checked item (-1 = no selection)
            ) { v , i ->
                // Item click listener
                // Get the alert dialog selected item's text
                district = pickDistrict[i]
                btDistrict.text = district!!.name
                ward = null
                btWard.text = "Chọn phường xã ▾"
                v.dismiss()
                // Display the selected item's text on snack bar
            }
            builder.show()
        })
    }

    private fun getProvince() {
        GeneralProcess().getProvinceVN(this, {
            val allProvince = Province.Deserializer().deserialize(it)!!
            val pickProvince: ArrayList<Province> = ArrayList()
            val pickProvinceName: ArrayList<String> = ArrayList()
            allProvince.forEach { r ->
                    pickProvince.add(r)
                    pickProvinceName.add(r.name)
            }

            val arrayAdapter = ArrayAdapter<String>(
                    this, // Context
                    android.R.layout.simple_list_item_single_choice, // Layout
                    pickProvinceName // List
            )

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Chọn tỉnh thành")
            builder.setSingleChoiceItems(
                    arrayAdapter, // Items list
                    -1 // Index of checked item (-1 = no selection)
            ) { v , i ->
                // Item click listener
                // Get the alert dialog selected item's text
                province = pickProvince[i]
                btProvince.text = province!!.name
                district = null
                btDistrict.text = "Chọn quận huyện ▾"
                ward = null
                btWard.text = "Chọn phường xã ▾"
                v.dismiss()
                // Display the selected item's text on snack bar
            }
            builder.show()
        })
    }

    private fun loadActivityAddress() {
        try {
            val typeFilter = AutocompleteFilter.Builder().setCountry("VN").build()
            val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setFilter(typeFilter)
                    .build(this)
            startActivityForResult(intent, 9999)
        } catch (e: GooglePlayServicesRepairableException) {
            // TODO: Handle the error.
            ToastUtils.error(this, e.localizedMessage)
        } catch (e: GooglePlayServicesNotAvailableException) {
            // TODO: Handle the error.
            ToastUtils.error(this, e.localizedMessage)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 9999 || requestCode == 9998) {
            if (resultCode == RESULT_OK) {
                val place = PlaceAutocomplete.getPlace(this, data)

                if (requestCode == 9999) {

                    val address = LocationUtils.getAddressFromPlace(this, place)
                    if (address != null) {
                        shipment.latFrom = address.latitude
                        shipment.lngFrom = address.longitude
                        txtAddress.text = place.address
                        txtAddress.isFocusable = true
                        province = null
                        btProvince.text = null
                        btProvince.error = null
                        btProvince.isFocusable = true
                        btProvince.hint = resources.getString(R.string.hint_state)
                        district = null
                        btDistrict.text = null
                        btDistrict.isFocusable = true
                        btDistrict.hint = resources.getString(R.string.hint_state)
                        btDistrict.error = null
                        ward = null
                        btWard.text = null
                        btWard.isFocusable = true
                        btWard.hint = resources.getString(R.string.hint_state)
                        btWard.error = null

                        province = null
                        district = null
                        ward = null
                        hub = null
                        GeneralProcess().getProvinceByName(this, address.adminArea, {
                            val newProvince = Province.Deserializer().deserializeSingle(it)
                            province = newProvince
                            btProvince.text = province!!.name
                            GeneralProcess().getDistrictByName(this, address.subAdminArea, province!!.id.toString(), {
                                Log.v("district", it)
                                if (!it.isNullOrEmpty()) {
                                    val newDistrict = District.Deserializer().deserializeSingle(it)
                                    district = newDistrict
                                    btDistrict.text = district!!.name
                                    var locality: String
                                    locality = address.locality
                                    if (!address.subLocality.isNullOrEmpty())
                                    {
                                        locality = address.subLocality
                                    }
                                    GeneralProcess().getWardByName(this,locality, district!!.id.toString(), {
                                        Log.v("ward", it)
                                        if (!it.isNullOrEmpty()) {
                                            val newWard = Ward.Deserializer().deserializeSingle(it)
                                            ward = newWard
                                            btWard.text = ward!!.name
                                            GeneralProcess().getHubByWardId(this, ward!!.id.toString(), {
                                                Log.v("hub", it)
                                                if (!it.isNullOrEmpty()) {
                                                    if (it != "null") {
                                                        val newHub = Hub.Deserializer().deserializeSingle(it)
                                                        hub = newHub
                                                    }
                                                    else
                                                    {
                                                        ToastUtils.warning(this, "Khu vực này chưa có trạm lấy hàng !")
                                                    }
                                                }
                                                else
                                                {
                                                    ToastUtils.warning(this, "Không hỗ trợ lấy hàng tại địa điểm này !")
                                                }
                                            })
                                        }
                                    })
                                }
                            })
                        })
                    }
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                val status = PlaceAutocomplete.getStatus(this, data)
                // TODO: Handle the error.
                ToastUtils.error(this, status.statusMessage!!)
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private fun nextStep() {
        if (txtName.isNotEmptyTextField(this, "Họ & tên không được bỏ trống !"))
        {
            return
        }
        if (txtAddress.isNotEmptyTextField(this,"Địa chỉ không được bỏ trống !"))
        {
            return
        }
        if (btProvince.isNotEmptySelector(this,"Tỉnh thành không được bỏ trống !",province))
        {
            return
        }
        if (btDistrict.isNotEmptySelector(this,"Quận huyện không được bỏ trống !",district))
        {
            return
        }
        if (btWard.isNotEmptySelector(this,"Phường xã không được bỏ trống !",ward))
        {
            return
        }
        if (btWard.isNotEmptySelector(this,"Không hỗ trợ lấy hàng tại địa điểm này !",hub))
        {
            return
        }
        shipment.senderName = txtName.text.toString()
        shipment.pickingAddress = txtAddress.text.toString()
        shipment.addressNoteFrom = txtAddressNote.text.toString()
        shipment.companyFrom = txtCompany.text.toString()

        shipment.fromProvinceId = province!!.id
        shipment.fromDistrictId = district!!.id
        shipment.fromWardId = ward!!.id
        shipment.fromHubId = hub!!.id

        val bundle = Bundle()
        bundle.putSerializable(
                "Shipments",
                shipment
        )

        val intent = Intent(this, Step2Activity::class.java)
        intent.putExtras(bundle)
        startActivityForResult(intent, 1)

    }

    private fun setupUI(view: View) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                view.clearFocus()
                hideSoftKeyboard(this@Step1Activity)
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
