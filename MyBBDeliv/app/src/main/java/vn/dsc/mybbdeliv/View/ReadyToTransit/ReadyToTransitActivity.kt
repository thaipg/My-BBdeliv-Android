package dsc.vn.mybbdeliv.View.ReadyToTransit

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import dsc.vn.mybbdeliv.Base.BaseActivity
import dsc.vn.mybbdeliv.ListViewAdapter.CustomAdapter
import dsc.vn.mybbdeliv.Model.MapModel
import dsc.vn.mybbdeliv.Model.Shipment
import dsc.vn.mybbdeliv.Process.ShipmentProcess
import dsc.vn.mybbdeliv.R
import dsc.vn.mybbdeliv.Utils.ToastUtils
import dsc.vn.mybbdeliv.View.Maps.MapsActivity
import dsc.vn.mybbdeliv.View.ReadyToTransit.CreateShipment.Step1Activity
import dsc.vn.mybbdeliv.View.ReadyToTransit.CreateShipmentFast.CreateShipmentFastActivity
import dsc.vn.mybbdeliv.View.ReadyToTransit.CreateShipmentRequest.CreateShipmentRequestActivity
import dsc.vn.mybbdeliv.View.ReadyToTransit.CreateShipments.ListShipmentCreateActivity
import kotlinx.android.synthetic.main.content_ready_to_transit.*
import java.util.*


class ReadyToTransitActivity : BaseActivity() , AdapterView.OnItemClickListener , View.OnClickListener {

    private var allShipment: MutableList<Shipment> = mutableListOf()
    private var selectedItems: MutableList<Shipment> = mutableListOf()
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_ready_to_transit)
        super.onCreate(savedInstanceState)

        prepareUI()
        bindData()

    }

    private fun prepareUI() {
        listView = findViewById(listView_shipment_ready_to_transit.id)
        listView.onItemClickListener = this
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE
        btPickup.setOnClickListener(this)
        btPickupBills.setOnClickListener(this)
        btReject.setOnClickListener(this)

        listView.isTextFilterEnabled = true
        searchViewRequest.setIconifiedByDefault(false)
        searchViewRequest.isSubmitButtonEnabled = false
        searchViewRequest.queryHint = "Nhập mã vận đơn hoặc bảng kê phát"
        searchViewRequest.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
        searchViewRequest.post { searchViewRequest.clearFocus() }

    }

    @SuppressLint("ResourceAsColor")
    private fun bindData() {
        allShipment.clear()
        ShipmentProcess().getShipmentPickup(this, {
            allShipment = Shipment.Deserializer().deserialize(it)!!
            listView.adapter = CustomAdapter(this, ArrayList(allShipment))
            val empty = findViewById<TextView>(empty.id)
            listView.emptyView = empty
            showSelectedItemText()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_map_camera, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_view_map -> {
                getSelectedItem()
                if (selectedItems.count() == 0) {
                    ToastUtils.warning(this, "Vui lòng chọn bill trước khi xem bản đồ")
                    return false
                }
                val shipment = selectedItems[0]
                val mapModel = MapModel()
                mapModel.lat = shipment.latFrom!!
                mapModel.lng = shipment.lngFrom!!
                mapModel.name = shipment.senderName.toString()
                mapModel.phone = shipment.senderPhone.toString()
                mapModel.title = shipment.pickingAddress
                mapModel.snippet = shipment.addressNoteFrom
                val bundle = Bundle()
                bundle.putSerializable(
                        "mapModel",
                        mapModel
                )
                val intent = Intent(this, MapsActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
                return true
            }
            R.id.action_camera -> {
                getSelectedItem()
                if (selectedItems.count() == 0) {
                    ToastUtils.warning(this, "Vui lòng chọn bill trước khi xem bản đồ")
                    return false
                }
                val shipment = selectedItems[0]
                val bundle = Bundle()
                bundle.putSerializable(
                        "shipment",
                        shipment
                )
                val intent = Intent(this, CreateShipmentFastActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private var lastSelectRow: CustomAdapter.ViewHolder? = null
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedRow = CustomAdapter.ViewHolder(view)
        if (lastSelectRow != null && lastSelectRow != selectedRow) {
            lastSelectRow!!.cbSelected.isChecked = false
            lastSelectRow = CustomAdapter.ViewHolder(view)
        } else {
            lastSelectRow = CustomAdapter.ViewHolder(view)
        }
        selectedRow.cbSelected.isChecked = !selectedRow.cbSelected.isChecked
        (listView.adapter as CustomAdapter).setSelected(id.toInt(), listView)
        getSelectedItem()
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


    override fun onClick(v: View?) {
        getSelectedItem()
        if (listView.checkedItemCount == 0) {
            ToastUtils.error(applicationContext, "Vui lòng chọn vận đơn !")
            return
        }

        if (v != null) {
            when {
                v.id == btPickup.id -> {
                    val bundle = Bundle()
                    bundle.putSerializable(
                            "Shipments",
                            selectedItems[0]
                    )
                    if (selectedItems[0].totalShipment!! > 0)
                    {
                        val intent = Intent(this, ListShipmentCreateActivity::class.java)
                        intent.putExtras(bundle)
                        startActivityForResult(intent, 1)
                    }
                    else
                    {
                        val intent = Intent(this, Step1Activity::class.java)
                        intent.putExtras(bundle)
                        startActivityForResult(intent, 1)
                    }
                }
                v.id == btPickupBills.id -> {
                    val bundle = Bundle()
                    bundle.putSerializable(
                            "Shipments",
                            selectedItems[0]
                    )

                    val intent = Intent(this, CreateShipmentRequestActivity::class.java)
                    intent.putExtras(bundle)
                    startActivityForResult(intent, 1)
                }
                v.id == btReject.id -> {
                    val bundle = Bundle()
                    bundle.putSerializable(
                            "Shipments",
                            ArrayList(selectedItems)
                    )

                    val intent = Intent(this, ShipmentPickupFailActivity::class.java)
                    intent.putExtras(bundle)
                    startActivityForResult(intent, 1)
                }
            }
        }
    }

    private fun enableButton() {
        btPickup.setBackgroundColor(Color.parseColor("#5CB85C"))
        btPickup.isEnabled = true
        Log.v("Shipment",selectedItems[0].toString())
        if (selectedItems[0].totalShipment == 0)
        {
            btPickupBills.setBackgroundColor(Color.parseColor("#2669FF"))
            btPickupBills.isEnabled = true

            btReject.setBackgroundColor(Color.parseColor("#FF3B30"))
            btReject.isEnabled = true
        }
        else
        {
            btReject.setBackgroundColor(Color.parseColor("#FF3B30"))
            btReject.isEnabled = true
        }

    }

    private fun disableButton() {
        btPickup.setBackgroundColor(Color.parseColor("#AAAAAA"))
        btPickup.isEnabled = false

        btPickupBills.setBackgroundColor(Color.parseColor("#AAAAAA"))
        btPickupBills.isEnabled = false

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
