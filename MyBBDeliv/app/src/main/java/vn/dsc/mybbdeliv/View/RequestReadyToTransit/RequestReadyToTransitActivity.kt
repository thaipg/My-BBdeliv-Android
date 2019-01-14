package dsc.vn.mybbdeliv.View.RequestReadyToTransit

import android.annotation.SuppressLint
import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import kotlinx.android.synthetic.main.content_request_ready_to_transit.*
import dsc.vn.mybbdeliv.Base.BaseActivity
import dsc.vn.mybbdeliv.Model.Shipment
import dsc.vn.mybbdeliv.ListViewAdapter.CustomAdapter
import dsc.vn.mybbdeliv.Model.MapModel
import dsc.vn.mybbdeliv.Process.ShipmentProcess
import dsc.vn.mybbdeliv.R
import dsc.vn.mybbdeliv.Service.LocationTrackingService
import dsc.vn.mybbdeliv.Service.LocationTrackingService.Companion.lastLocation
import dsc.vn.mybbdeliv.Utils.ToastUtils
import dsc.vn.mybbdeliv.View.Maps.MapsActivity
import java.util.*

class RequestReadyToTransitActivity : BaseActivity(), AdapterView.OnItemClickListener, View.OnClickListener {

    private var allShipment: MutableList<Shipment> = mutableListOf()
    private var selectedItems: MutableList<Shipment> = mutableListOf()
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_request_ready_to_transit)
        super.onCreate(savedInstanceState)
        prepareUI()
        bindData()
    }

    private fun prepareUI() {
        listView = findViewById<ListView>(listView_request_shipment_ready_to_transit.id)
        listView.onItemClickListener = this
        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        btPickup.setOnClickListener(this)
        btReject.setOnClickListener(this)

        listView.isTextFilterEnabled = true
        searchViewRequestReady.setIconifiedByDefault(false)
        searchViewRequestReady.isSubmitButtonEnabled = false
        searchViewRequestReady.queryHint = "Nhập mã vận đơn hoặc bảng kê phát"
        searchViewRequestReady.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (TextUtils.isEmpty(newText)) {
                    listView.clearTextFilter()
                } else {
                    listView.setFilterText(newText)
                }
                return false
            }
        })
        searchViewRequestReady.post { searchViewRequestReady.clearFocus() }
    }

    @SuppressLint("ResourceAsColor")
    private fun bindData() {
        allShipment.clear()
        ShipmentProcess().getShipmentRequestPickup(this, {
            allShipment = Shipment.Deserializer().deserialize(it)!!
            listView.adapter = CustomAdapter(this, ArrayList(allShipment))
            val empty = findViewById<TextView>(empty.id)
            listView.emptyView = empty
            showSelectedItemText()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_request_shipment, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_add_request -> {
                val intent = Intent(this, CreateRequestShipmentActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedRow = CustomAdapter.ViewHolder(view)
        selectedRow.cbSelected.isChecked = !selectedRow.cbSelected.isChecked
        (listView.adapter as CustomAdapter).setSelected(id.toInt(), listView)
        showSelectedItemText()
    }

    private fun showSelectedItemText() {
        if (listView.checkedItemCount > 0) {
            enableButton()
            tvFooter.text = "Số lượng vận đơn : " + listView.count + " - Vận đơn đã chọn : " + listView.checkedItemCount
        } else {
            disableButton()
            tvFooter.text = "Số lượng vận đơn : " + listView.count + " - Vận đơn đã chọn : 0"
        }
    }

    private fun enableButton() {
        btPickup.setBackgroundColor(Color.parseColor("#5CB85C"))
        btPickup.isEnabled = true

        btReject.setBackgroundColor(Color.parseColor("#FF3B30"))
        btReject.isEnabled = true
    }

    private fun disableButton() {
        btPickup.setBackgroundColor(Color.parseColor("#AAAAAA"))
        btPickup.isEnabled = false

        btReject.setBackgroundColor(Color.parseColor("#AAAAAA"))
        btReject.isEnabled = false
    }

    private fun getSelectedItem() {
        selectedItems.clear()
        listView.checkItemIds
                .asSequence()
                .mapNotNull { allShipment.find { shipment -> shipment.id.toLong() == it } }
                .forEach { selectedItems.add(it) }
    }

    override fun onClick(v: View?) {
        getSelectedItem()
        if (listView.checkedItemCount == 0) {
            ToastUtils.error(applicationContext, "Vui lòng chọn vận đơn !")
            return
        }

        if (v != null) {
            if (v.id == btPickup.id) {
                for (item in selectedItems) {

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

                    item.shipmentStatusId = 2
                    ShipmentProcess().updateRequestShipment(this, item, {

                        Log.v("Van Don SL", item.totalShipment.toString())
                        if (item.totalShipment!! > 0) {
                            ShipmentProcess().getByRequestShipmentId(this, item.id.toString(), {
                                Log.v("Van Don", it.toString())
                                val shipments = Shipment.Deserializer().deserialize(it)!!
                                for (itemS in shipments) {
                                    if (itemS.shipmentStatusId == 1) {
                                        itemS.shipmentStatusId = 2
                                        Log.v("Van Don", itemS.toString())
                                        ShipmentProcess().updateShipment(this, itemS, {
                                            if (item.id == selectedItems.last().id) {
                                                ToastUtils.success(applicationContext, "Xác nhận lấy hàng thành công!")
                                                listView.clearChoices()
                                                this.bindData()
                                                this.disableButton()
                                            }
                                        })
                                    }
                                }
                            })
                        } else {
                            if (item.id == selectedItems.last().id) {
                                ToastUtils.success(applicationContext, "Xác nhận lấy hàng thành công!")
                                listView.clearChoices()
                                this.bindData()
                                this.disableButton()
                            }
                        }
                    })
                }
            } else if (v.id == btReject.id) {
                val bundle = Bundle()
                bundle.putSerializable(
                        "Shipments",
                        ArrayList(selectedItems)
                )

                val intent = Intent(this, ShipmentPickupRequestRejectActivity::class.java)
                intent.putExtras(bundle)
                startActivityForResult(intent, 1)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                ToastUtils.success(applicationContext, "Cập nhật thành công!")
                listView.clearChoices()
                bindData()
                disableButton()
            }
        }
    }
}