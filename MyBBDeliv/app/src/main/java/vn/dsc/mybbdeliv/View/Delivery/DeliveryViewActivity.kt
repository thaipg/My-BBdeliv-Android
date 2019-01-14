package dsc.vn.mybbdeliv.View.Delivery

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.content_delivery_view.*
import dsc.vn.mybbdeliv.Base.BaseActivity
import dsc.vn.mybbdeliv.Camera.ZXingUtils
import dsc.vn.mybbdeliv.ListViewAdapter.CustomDeliveryAdapter
import dsc.vn.mybbdeliv.Model.MapModel
import dsc.vn.mybbdeliv.Model.Shipment
import dsc.vn.mybbdeliv.Process.ShipmentProcess
import dsc.vn.mybbdeliv.R
import dsc.vn.mybbdeliv.Utils.ToastUtils
import dsc.vn.mybbdeliv.View.Maps.MapsActivity
import java.util.*

class DeliveryViewActivity: BaseActivity() , AdapterView.OnItemClickListener , View.OnClickListener {

    private var allShipment: MutableList<Shipment> = mutableListOf()
    private var selectedItems: MutableList<Shipment> = mutableListOf()
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_delivery_view)
        super.onCreate(savedInstanceState)
        prepareUI()
        bindData()
    }

    private fun prepareUI() {
        listView = findViewById<ListView>(listView_delivery.id)
        listView.onItemClickListener = this
        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        btDeliverySuccess.setOnClickListener(this)
        btDeliveryFail.setOnClickListener(this)
        btDeliveryAccident.setOnClickListener(this)

        listView.isTextFilterEnabled = true
        searchViewContentDelivery.setIconifiedByDefault(false)
        searchViewContentDelivery.isSubmitButtonEnabled = false
        searchViewContentDelivery.queryHint = "Nhập mã vận đơn hoặc bảng kê phát"
        searchViewContentDelivery.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
        searchViewContentDelivery.post { searchViewContentDelivery.clearFocus() }
    }

    @SuppressLint("ResourceAsColor")
    private fun bindData() {
        allShipment.clear()
        ShipmentProcess().getShipmentDelivery(this, {
            for (s in Shipment.Deserializer().deserialize(it)!!) {
                allShipment.add(s)
            }
            ShipmentProcess().getShipmentReturn(this, {
                for (s in Shipment.Deserializer().deserialize(it)!!) {
                    allShipment.add(s)
                }
                listView.adapter = CustomDeliveryAdapter(
                        this,
                        ArrayList(allShipment)
                )
                val empty = findViewById<TextView>(empty.id)
                listView.emptyView = empty
                showSelectedItemText()
            })
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_delivery, menu)
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
                mapModel.lat = shipment.latTo!!
                mapModel.lng = shipment.lngTo!!
                mapModel.name = shipment.receiverName!!
                mapModel.phone = shipment.receiverPhone!!
                mapModel.title = shipment.shippingAddress
                mapModel.snippet = shipment.addressNoteTo
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
                getBarcodeScan()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedRow = CustomDeliveryAdapter.ViewHolder(view)
        selectedRow.cbSelected.isChecked = !selectedRow.cbSelected.isChecked
        (listView.adapter as CustomDeliveryAdapter).setSelected(id.toInt(), listView)
        showSelectedItemText()
    }

    @SuppressLint("SetTextI18n")
    private fun showSelectedItemText() {
        if (listView.checkedItemCount > 0) {
            tvFooter.text = "Số lượng vận đơn : " + listView.count + " - Vận đơn đã chọn : " + listView.checkedItemCount
        } else {
            tvFooter.text = "Số lượng vận đơn : " + listView.count + " - Vận đơn đã chọn : 0"
        }
    }

    private fun getBarcodeScan() {
        ZXingUtils.initiateScan(this)
    }

    private fun getSelectedItem() {
        selectedItems.clear()
        listView.checkItemIds
                .asSequence()
                .mapNotNull { allShipment.find { shipment -> shipment.id.toLong() == it } }
                .forEach({ selectedItems.add(it) })
    }

    override fun onClick(v: View?) {
        getSelectedItem()
        if (listView.checkedItemCount == 0) {
            ToastUtils.error(this, "Vui lòng chọn vận đơn !")
            return
        }

        if (v != null) {
            if (v.id == btDeliverySuccess.id) {
                val bundle = Bundle()
                bundle.putSerializable(
                        "Shipments",
                        ArrayList(selectedItems)
                )

                val intent = Intent(this, DeliverySuccessActivity::class.java)
                intent.putExtras(bundle)
                startActivityForResult(intent, 1)
            } else if (v.id == btDeliveryFail.id) {
                val bundle = Bundle()
                bundle.putSerializable(
                        "Shipments",
                        ArrayList(selectedItems)
                )

                val intent = Intent(this, DeliveryFailActivity::class.java)
                intent.putExtras(bundle)
                startActivityForResult(intent, 1)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == ZXingUtils.REQUEST_CODE) {
                val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                Log.d("RequestCode", "ZXingUtils.REQUEST_CODE: " + ZXingUtils.REQUEST_CODE + " - " + result!!.contents)
                if (result.contents != null) {
                    val filter = allShipment.filter { shipment -> shipment.shipmentNumber == result.contents }
                    if (filter.count() > 0) {
                        allShipment = filter as MutableList<Shipment>
                        listView.adapter = CustomDeliveryAdapter(
                                this,
                                ArrayList(allShipment)
                        )
                    }
                    else
                    {
                        ShipmentProcess().receiveDeliveryReturnShipment(this, result.contents) {
                            ToastUtils.success(this, "Nhận hàng đi giao thành công !")
                            this.bindData()
                        }
                    }
                }
            }
        }
        if (resultCode == RESULT_OK && data == null) {
            ToastUtils.success(applicationContext, "Cập nhật thành công!")
            listView.clearChoices()
            bindData()
        }
    }
}
