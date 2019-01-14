package dsc.vn.mybbdeliv.View.ReadyToTransit.CreateShipments

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.content_list_shipment_create.*
import dsc.vn.mybbdeliv.Base.BaseSubActivity
import dsc.vn.mybbdeliv.Camera.ZXingUtils
import dsc.vn.mybbdeliv.ListViewAdapter.CustomListCreateAdapter
import dsc.vn.mybbdeliv.Model.Shipment
import dsc.vn.mybbdeliv.Process.ShipmentProcess
import dsc.vn.mybbdeliv.R
import dsc.vn.mybbdeliv.Service.LocationTrackingService
import dsc.vn.mybbdeliv.Utils.ToastUtils
import dsc.vn.mybbdeliv.View.ReadyToTransit.ReadyToTransitActivity
import java.util.*

class ListShipmentCreateActivity : BaseSubActivity() , View.OnClickListener , AdapterView.OnItemClickListener , CustomListCreateAdapter.btListClickListener {

    private var allShipment: MutableList<Shipment> = mutableListOf()
    private var selectedItem: Shipment? = null
    private lateinit var listView: ListView
    private var requestScan = ""

    override val layoutResourceId: Int
        get() {
            return R.layout.activity_list_shipment_create
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_shipment_create)
        prepareUI()
        bindData()
    }

    private fun prepareUI() {
        setupUI(findViewById<View>(clLayout.id))
        listView = findViewById(listView_shipment_create.id)
        listView.onItemClickListener = this
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE
        btnUpdate.setOnClickListener(this)

        editTextSearch.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val searchShipments = allShipment.filter { x -> x.receiverPhone!!.contains(editTextSearch.text.toString()) }
                val adapter = CustomListCreateAdapter(applicationContext, ArrayList(searchShipments!!), false)
                adapter.setbtListner(this)
                listView.adapter = adapter
                editTextSearch.clearFocus()
            }
        }
    }

    private fun bindData() {
        val intent = intent
        val bundle = intent.extras

        val shipment = (bundle!!.getSerializable("Shipments") as Shipment)

        txtName.text = shipment?.senderName
        txtPhone.text = shipment?.senderPhone
        txtAddress.text = shipment?.pickingAddress

        allShipment.clear()

        ShipmentProcess().getByRequestShipmentId(this, shipment.id.toString(), {
            Log.v("Shipment", it)
            allShipment = Shipment.Deserializer().deserialize(it)!!
            val adapter = CustomListCreateAdapter(applicationContext, ArrayList(allShipment), false)
            adapter.setbtListner(this)
            listView.adapter = adapter
            val empty = findViewById<TextView>(empty.id)
            listView.emptyView = empty
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_camera, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_camera -> {
                requestScan = "ScanBarcode"
                getBarcodeScan()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

    }

    override fun onScanShipmentClickListner(position: Int, value: Shipment) {
        selectedItem = value
        requestScan = "ChangeShipmentNumber"
        getBarcodeScan()
    }

    override fun onEditShipmentClickListner(position: Int, value: Shipment) {
        val bundle = Bundle()
        bundle.putSerializable(
                "Shipments",
                value
        )
        val intent = Intent(this, CreateDetailActivity::class.java)
        intent.putExtras(bundle)
        startActivityForResult(intent, 2)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when {
                v.id == btnUpdate.id -> submit()
            }
        }
    }

    private fun submit() {

        val intent = intent
        val bundle = intent.extras

        val shipment = (bundle!!.getSerializable("Shipments") as Shipment)

        if (shipment.shipmentStatusId == 2) {
            val result = allShipment.filter { shipment -> shipment.shipmentStatusId == 2 }

            if (result.count() == 0) {
                shipment?.shipmentStatusId = 3
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
                    val addressFragments = (0..address!!.maxAddressLineIndex).map(address!!::getAddressLine)
                    shipment.location = TextUtils.join(System.getProperty("line.separator"),
                            addressFragments).replace("\n", ", ")
                }
                ShipmentProcess().updateRequestShipment(this, shipment, {
                    ToastUtils.success(this, "Yêu cầu đã tiếp nhận thành công . Số lượng vận đơn : ${allShipment.count()} .")
                    val intent = Intent(this, ReadyToTransitActivity::class.java)
                    startActivity(intent)
                })
            } else {
                val message = "Các vận đơn chưa lấy hết sẽ chuyển sang huỷ, bạn chấp nhận hoàn tất lấy hàng ?"
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setTitle("Xác nhận")
                dialogBuilder.setMessage(message)
                dialogBuilder.setPositiveButton("Quay lại", null)
                dialogBuilder.setNegativeButton("Xác nhận", { v, w ->
                    shipment.shipmentStatusId = 3
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
                        val addressFragments = (0..address!!.maxAddressLineIndex).map(address!!::getAddressLine)
                        shipment.location = TextUtils.join(System.getProperty("line.separator"),
                                addressFragments).replace("\n", ", ")
                    }
                    ShipmentProcess().updateRequestShipment(this, shipment, {
                        for (item in allShipment) {
                            if (item.shipmentStatusId == 2) {
                                item.shipmentStatusId = 4
                                item.reasonId = 3
                                item.note = "Không có hàng trong yêu cầu.Nhân viên xác nhận !"
                                item.currentLat = LocationTrackingService.lastLocation.latitude
                                item.currentLng = LocationTrackingService.lastLocation.longitude
                                if (address != null) {
                                    val addressFragments = (0..address!!.maxAddressLineIndex).map(address!!::getAddressLine)
                                    shipment.location = TextUtils.join(System.getProperty("line.separator"),
                                            addressFragments).replace("\n", ", ")
                                }
                                ShipmentProcess().updateShipment(this, item, {
                                    if (item.id == allShipment.last().id) {
                                        var totalSR = 0
                                        for (shipment in allShipment) {
                                            if (shipment.shipmentStatusId == 3) {
                                                totalSR += 1
                                            }
                                        }
                                        ToastUtils.success(this, "Yêu cầu đã tiếp nhận thành công . Số lượng vận đơn : $totalSR .")
                                        val intent = Intent(this, ReadyToTransitActivity::class.java)
                                        startActivity(intent)
                                    }
                                })
                            }
                        }
                        ToastUtils.success(this, "Yêu cầu đã tiếp nhận thành công . Số lượng vận đơn : ${allShipment.count()} .")
                        val intent = Intent(this, ReadyToTransitActivity::class.java)
                        startActivity(intent)
                    })
                })
                dialogBuilder.show()
            }
        }
    }

//    private fun getSelectedItem() {
//        selectedItems.clear()
//        listView.checkItemIds
//                .asSequence()
//                .mapNotNull { allShipment.find { shipment -> shipment.id.toLong() == it } }
//                .forEach { selectedItems.add(it) }
//    }

    private fun getBarcodeScan() {
        ZXingUtils.initiateScan(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.v("ReloadData", ""+ resultCode + "|" + requestCode)
        val intent = intent
        val bundle = intent.extras
        val shipment = (bundle!!.getSerializable("Shipments") as Shipment)
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == ZXingUtils.REQUEST_CODE) {
                val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                Log.d("RequestCode", "ZXingUtils.REQUEST_CODE: " + ZXingUtils.REQUEST_CODE + " - " + result!!.contents)
                if (result.contents != null) {
                    if (requestScan == "ScanBarcode") {
                        ShipmentProcess().getByRequestShipmentId(this, shipment.id.toString(), {
                            var shipments = Shipment.Deserializer().deserialize(it)!!
                            for (s in shipments) {
                                if (s.shipmentNumber == result.contents) {
                                    if (s.shipmentStatusId == 2) {
                                        s.shipmentStatusId = 3
                                        s.currentLat = LocationTrackingService.lastLocation.latitude
                                        s.currentLng = LocationTrackingService.lastLocation.longitude
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
                                            val addressFragments = (0..address!!.maxAddressLineIndex).map(address!!::getAddressLine)
                                            s.location = TextUtils.join(System.getProperty("line.separator"),
                                                    addressFragments).replace("\n", ", ")
                                        }
                                        ShipmentProcess().updateShipment(this, s, {
                                            ToastUtils.warning(this, "Mã (${result.contents}) đã lấy thành công !")
                                            bindData()
                                        })
                                    } else {
                                        ToastUtils.warning(this, "Mã (${result.contents}) không đúng trạng thái !")
                                        bindData()
                                    }
                                } else {
                                    ToastUtils.warning(this, "Mã (${result.contents}) không nằm trong yêu cầu !")
                                }
                            }
                        })
                    } else if (requestScan == "ChangeShipmentNumber") {
                        if (selectedItem != null) {
                            Log.v("Change shipment", selectedItem!!.shipmentNumber)
                            var item = selectedItem!!
                            if (item.shipmentStatusId == 2) {
                                item.shipmentNumber = result.contents
                                item.shipmentStatusId = 3
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
                                    val addressFragments = (0..address!!.maxAddressLineIndex).map(address!!::getAddressLine)
                                    item.location = TextUtils.join(System.getProperty("line.separator"),
                                            addressFragments).replace("\n", ", ")
                                }
                                ShipmentProcess().updateShipment(this, item, {
                                    ToastUtils.warning(this, "Mã (${result.contents}) đã lấy thành công !")
                                    bindData()
                                })
                            } else {
                                ToastUtils.warning(this, "Mã (${result.contents}) không đúng trạng thái !")
                                bindData()
                            }
                        } else {
                            ToastUtils.warning(this, "Mã (${result.contents}) không nằm trong yêu cầu !")
                        }
                    }
                }
            }
            else if (requestCode == 2) {
                Log.v("ReloadData","true")
                bindData()
            }
        }
    }

    private fun setupUI(view: View) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                view.clearFocus()
                hideSoftKeyboard(this@ListShipmentCreateActivity)
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