package dsc.vn.mybbdeliv.View.ReadyToTransit.CreateShipment

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.google.gson.Gson
import dsc.vn.mybbdeliv.Base.BaseSubActivity
import dsc.vn.mybbdeliv.Extension.toDateTime
import dsc.vn.mybbdeliv.Model.Shipment
import dsc.vn.mybbdeliv.Process.ShipmentProcess
import dsc.vn.mybbdeliv.Process.UserProcess
import dsc.vn.mybbdeliv.R
import dsc.vn.mybbdeliv.Service.LocationTrackingService
import dsc.vn.mybbdeliv.Utils.ToastUtils
import dsc.vn.mybbdeliv.View.ReadyToTransit.ReadyToTransitActivity
import kotlinx.android.synthetic.main.content_step4.*
import java.util.*

class Step4Activity : BaseSubActivity() , View.OnClickListener {

    private var shipment: Shipment = Shipment()

    override val layoutResourceId: Int
        get() {
            return R.layout.activity_shipment_pickup_request_reject
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step4)
        prepareUI()
        bindData()
    }

    private fun prepareUI()
    {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setupUI(findViewById<View>(clLayout.id))
        btnNext.setOnClickListener(this)
    }

    private fun bindData()
    {
        val intent = intent
        val bundle = intent.extras
        shipment = (bundle!!.getSerializable("Shipments") as Shipment)

        lblAddressTo.text = shipment.shippingAddress
        lblService.text = shipment.service!!.name
        lblWeight.text = shipment.weight.toString()

        lblPhiVanChuyen.text = shipment.defaultPrice.toString()
        if (shipment.fuelPrice != null)
        {
            lblPPXD.text = shipment.fuelPrice.toString()
        }
        if (shipment.totalDVGT != null)
        {
            lblPhiDVGT.text = shipment.totalDVGT.toString()
        }
        if (shipment.expectedDeliveryTime != null)
        {
            if (!shipment.expectedDeliveryTime.isNullOrBlank())
            {
                editText_shipment_time_expect.setText("Dự kiến giao : " + shipment.expectedDeliveryTime.toDateTime())
            }
        }
        lblVat.text = shipment.vatPrice.toString()
        lblTongCong.text = shipment.totalPrice.toString()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            if (v.id == btnNext.id) {
                nextStep()
            }
        }
    }

    private fun nextStep() {
        shipment.note = txtNote.text.toString()
        shipment.fromWard = null
        shipment.toWard = null
        shipment.pickUserId = UserProcess.token!!.userId
        shipment.currentLat = LocationTrackingService.lastLocation.latitude
        shipment.currentLng = LocationTrackingService.lastLocation.longitude
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
            shipment.location = TextUtils.join(System.getProperty("line.separator"),
                    addressFragments).replace("\n", ", ")
        }

        Log.v("Data", Gson().toJson(shipment))
        ShipmentProcess().updatePickupCompleteByCurrentEmp(this, shipment, {
            val intent = Intent(this, ReadyToTransitActivity::class.java)
            startActivity(intent)
            ToastUtils.success(this,"Yêu cầu đã tiếp nhận thành công . Số mã vận đơn " + shipment.shipmentNumber + ".")
            Log.v("Success", it)
        })
    }

    private fun setupUI(view: View) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                view.clearFocus()
                hideSoftKeyboard(this@Step4Activity)
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
