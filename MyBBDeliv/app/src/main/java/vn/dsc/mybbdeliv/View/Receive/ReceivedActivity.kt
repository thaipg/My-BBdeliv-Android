package dsc.vn.mybbdeliv.View.Receive

import android.annotation.SuppressLint
import android.content.Intent
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
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.content_received.*
import dsc.vn.mybbdeliv.Base.BaseActivity
import dsc.vn.mybbdeliv.Camera.ZXingUtils
import dsc.vn.mybbdeliv.ListViewAdapter.CustomAdapter
import dsc.vn.mybbdeliv.Model.Shipment
import dsc.vn.mybbdeliv.Process.ShipmentProcess
import dsc.vn.mybbdeliv.R
import dsc.vn.mybbdeliv.Utils.ToastUtils
import java.util.*

class ReceivedActivity : BaseActivity() , AdapterView.OnItemClickListener , View.OnClickListener {

    private var allShipment: MutableList<Shipment> = mutableListOf()
    private var selectedItems: MutableList<Shipment> = mutableListOf()
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_received)
        super.onCreate(savedInstanceState)

        prepareUI()
        bindData()
    }

    private fun prepareUI() {
        listView = findViewById<ListView>(listView_received.id)
        listView.onItemClickListener = this
        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        listView.isTextFilterEnabled = true
        searchViewContentReceive.setIconifiedByDefault(false)
        searchViewContentReceive.isSubmitButtonEnabled = false
        searchViewContentReceive.queryHint = "Nhập mã vận đơn hoặc bảng kê phát"
        searchViewContentReceive.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
        searchViewContentReceive.post { searchViewContentReceive.clearFocus() }
    }

    @SuppressLint("ResourceAsColor", "WrongConstant")
    private fun bindData() {
        allShipment.clear()
        ShipmentProcess().getShipmentTransfer (this, {
            for (s in Shipment.Deserializer().deserialize(it)!!) {
                allShipment.add(s)
            }
            ShipmentProcess().getShipmentTransferReturn (this, {
                for (s in Shipment.Deserializer().deserialize(it)!!) {
                    allShipment.add(s)
                }
                val adapter = CustomAdapter(this, ArrayList(allShipment))
                adapter.showCheckBox = false
                listView.adapter = adapter
                val empty = findViewById<TextView>(empty.id)
                listView.emptyView = empty
                showSelectedItemText()
            })
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
                ZXingUtils.initiateScan(this)
                true
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

    @SuppressLint("SetTextI18n")
    private fun showSelectedItemText() {
        if (listView.checkedItemCount > 0) {
            tvFooter.text = "Số lượng vận đơn : " + listView.count + " - Vận đơn đã chọn : " + listView.checkedItemCount
        } else {
            tvFooter.text = "Số lượng vận đơn : " + listView.count + " - Vận đơn đã chọn : 0"
        }
    }

    private fun getSelectedItem()
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == ZXingUtils.REQUEST_CODE) {
                val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                Log.d("RequestCode", "ZXingUtils.REQUEST_CODE: " + ZXingUtils.REQUEST_CODE + " - " + result!!.contents)
                if (result.contents == null) {

                } else {
                    ToastUtils.success(this,result.contents)
                }
            }
        }
    }
}
