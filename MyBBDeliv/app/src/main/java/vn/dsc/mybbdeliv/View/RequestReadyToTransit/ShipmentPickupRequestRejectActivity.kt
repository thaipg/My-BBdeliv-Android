package dsc.vn.mybbdeliv.View.RequestReadyToTransit

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import kotlinx.android.synthetic.main.content_shipment_pickup_request_reject.*
import dsc.vn.mybbdeliv.Base.BaseSubActivity
import dsc.vn.mybbdeliv.Model.Reason
import dsc.vn.mybbdeliv.Model.Shipment
import dsc.vn.mybbdeliv.Process.GeneralProcess
import dsc.vn.mybbdeliv.Process.ShipmentProcess
import dsc.vn.mybbdeliv.R.layout.activity_shipment_pickup_request_reject
import dsc.vn.mybbdeliv.Service.LocationTrackingService
import dsc.vn.mybbdeliv.Utils.ToastUtils
import java.util.*


class ShipmentPickupRequestRejectActivity : BaseSubActivity() , View.OnClickListener {

    var selectedItems: MutableList<Shipment> = mutableListOf()
    private var selectedReason: Reason? = null

    override val layoutResourceId: Int
        get() {
            return activity_shipment_pickup_request_reject
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_shipment_pickup_request_reject)
        prepareUI()
        bindData()
    }

    private fun prepareUI()
    {
        setupUI(findViewById<View>(clLayout.id))
        btnUpdate.setOnClickListener(this)
        btSelectReason.setOnClickListener(this)
    }

    private fun bindData()
    {
        val intent = intent
        val bundle = intent.extras
        selectedItems = (bundle!!.getSerializable("Shipments") as ArrayList<Shipment>).toMutableList()
    }

    override fun onClick(v: View?) {
        if (selectedItems.count() == 0) {
            ToastUtils.error(applicationContext, "Vui lòng chọn vận đơn !")
            return
        }

        if (v != null) {
            if (v.id == btnUpdate.id)
            {
                if(selectedReason == null)
                {
                    ToastUtils.error(this,"Vui lòng chọn lý do !")
                    return
                }

                for (item in selectedItems) {
                    item.shipmentStatusId = 42

                    item.currentLat = LocationTrackingService.lastLocation.latitude
                    item.currentLng = LocationTrackingService.lastLocation.longitude
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(
                            LocationTrackingService.lastLocation.latitude,
                            LocationTrackingService.lastLocation.longitude,
                            1)

                    var address: Address? = null
                    if (addresses.count() > 0) {
                        address = addresses[0]
                    }

                    if (address != null) {
                        val addressFragments = (0..address.maxAddressLineIndex).map(address::getAddressLine)
                        item.location = TextUtils.join(System.getProperty("line.separator"),
                                addressFragments).replace("\n", ", ")
                    }

                    item.reasonId = selectedReason!!.id
                    var note = ""
                    if (selectedReason != null) {
                        note = selectedReason!!.name + "\n"
                    }
                    if (!editTextNote.text.isEmpty()) {
                        note += "|Ghi chú : " + editTextNote.text
                    }
                    item.note = note

                    ShipmentProcess().updateRequestShipment(this, item, {
                        if (item.id == selectedItems.last().id) {
                            val intent = Intent()
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                    })
                }
            }
            else if  (v.id == btSelectReason.id)
            {
                GeneralProcess().getReason(this, {
                    val allReason = Reason.Deserializer().deserialize(it)!!
                    val pickRejectReason: ArrayList<Reason> = ArrayList()
                    val pickRejectReasonName: ArrayList<String> = ArrayList()
                    allReason.forEach { r ->
                        if(r.pickReject) {
                            pickRejectReason.add(r)
                            pickRejectReasonName.add(r.name)
                        }
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
                        btSelectReason.text = selectedReason!!.name
                        v.dismiss()
                        // Display the selected item's text on snack bar
                    }

                    builder.show()
                })
            }
        }
    }


    private fun setupUI(view: View) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                view.clearFocus()
                hideSoftKeyboard(this@ShipmentPickupRequestRejectActivity)
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
