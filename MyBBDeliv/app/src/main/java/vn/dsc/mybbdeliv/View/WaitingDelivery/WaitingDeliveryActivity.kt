package dsc.vn.mybbdeliv.View.WaitingDelivery

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import kotlinx.android.synthetic.main.content_waiting_delivery.*
import dsc.vn.mybbdeliv.Base.BaseActivity
import dsc.vn.mybbdeliv.ListViewAdapter.CustomAdapter
import dsc.vn.mybbdeliv.Model.Shipment
import dsc.vn.mybbdeliv.Process.ShipmentProcess
import dsc.vn.mybbdeliv.R
import dsc.vn.mybbdeliv.Service.LocationTrackingService
import dsc.vn.mybbdeliv.Utils.ToastUtils
import java.util.*


class WaitingDeliveryActivity  : BaseActivity() , AdapterView.OnItemClickListener , View.OnClickListener {

    private var allShipment: MutableList<Shipment> = mutableListOf()
    private var selectedItems: MutableList<Shipment> = mutableListOf()
    private lateinit var listView: ListView
    private var isCheckAll = true

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_waiting_delivery)
        super.onCreate(savedInstanceState)

        prepareUI()
        bindData()
    }

    private fun prepareUI() {
        listView = findViewById<ListView>(listView_waiting_delivery.id)
        listView.onItemClickListener = this
        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        btPickup.setOnClickListener(this)

        listView.isTextFilterEnabled = true
        searchViewContentWaitingDelivery.setIconifiedByDefault(false)
        searchViewContentWaitingDelivery.isSubmitButtonEnabled = false
        searchViewContentWaitingDelivery.queryHint = "Nhập mã vận đơn hoặc bảng kê phát"
        searchViewContentWaitingDelivery.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
        searchViewContentWaitingDelivery.post { searchViewContentWaitingDelivery.clearFocus() }
    }

    @SuppressLint("ResourceAsColor")
    private fun bindData() {
        allShipment.clear()
        ShipmentProcess().getShipmentWaitingDelivery (this, {
            for (s in Shipment.Deserializer().deserialize(it)!!) {
                allShipment.add(s)
            }
            ShipmentProcess().getShipmentWaitingReturn (this, {
                for (s in Shipment.Deserializer().deserialize(it)!!) {
                    allShipment.add(s)
                }
                listView.adapter = CustomAdapter(this, ArrayList(allShipment))
                val empty = findViewById<TextView>(empty.id)
                listView.emptyView = empty
                showSelectedItemText()
            })
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_checkbox_all, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_checkbox -> {
                if (isCheckAll)
                {
                    item.icon = ContextCompat.getDrawable(this, R.drawable.baseline_check_box_24)
                }
                else
                {
                    item.icon = ContextCompat.getDrawable(this, R.drawable.ic_check_box_outline_blank_white_24dp)
                }
                for (i in 0 until listView.adapter.count) {
                    listView.setItemChecked(i, isCheckAll)
                }
                (listView.adapter as CustomAdapter).checkAll(isCheckAll)
                showSelectedItemText()
                isCheckAll = !isCheckAll
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
    }

    private fun disableButton() {
        btPickup.setBackgroundColor(Color.parseColor("#AAAAAA"))
        btPickup.isEnabled = false
    }

    fun getSelectedItem()
    {
        selectedItems.clear()
        listView.checkItemIds
                .asSequence()
                .mapNotNull { allShipment.find { shipment -> shipment.id.toLong() == it } }
                .forEach { selectedItems.add(it) }
    }

    override fun onClick(v: View?) {
        getSelectedItem()
        if (listView.checkedItemCount == 0) {
            ToastUtils.error(this, "Vui lòng chọn vận đơn !")
            return
        }

        if (v != null) {
            if (v.id == btPickup.id) {
                for (item in selectedItems) {
                    // Giao hàng
                    if (item.shipmentStatusId == 48)
                    {
                        item.shipmentStatusId = 11
                    }
                    // Trả hàng
                    else if (item.shipmentStatusId == 51)
                    {
                        item.shipmentStatusId = 31
                    }

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

                    ShipmentProcess().updateShipment(this,item, {
                        if (item.id == selectedItems.last().id)
                        {
                            ToastUtils.success(this, "Đã xác nhận giao hàng!")
                            listView.clearChoices()
                            this.bindData()
                            this.disableButton()
                        }
                    })
                }
            }
        }
    }
}
