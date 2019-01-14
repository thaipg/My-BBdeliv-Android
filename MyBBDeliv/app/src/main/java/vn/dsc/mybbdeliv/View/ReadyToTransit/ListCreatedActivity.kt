package dsc.vn.mybbdeliv.View.ReadyToTransit

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.TextView
import dsc.vn.mybbdeliv.Base.BaseActivity
import dsc.vn.mybbdeliv.ListViewAdapter.CustomAdapter
import dsc.vn.mybbdeliv.Model.Shipment
import dsc.vn.mybbdeliv.Process.ShipmentProcess
import dsc.vn.mybbdeliv.R
import dsc.vn.mybbdeliv.Utils.ToastUtils
import dsc.vn.mybbdeliv.View.ReadyToTransit.CreateShipmentFast.CreateShipmentFastActivity
import kotlinx.android.synthetic.main.content_list_created.*
import java.util.*

class ListCreatedActivity : BaseActivity() {

    private var allShipment: MutableList<Shipment> = mutableListOf()
    private var selectedItems: MutableList<Shipment> = mutableListOf()
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_list_created)
        super.onCreate(savedInstanceState)

        prepareUI()
        bindData()

    }

    private fun prepareUI() {
        listView = findViewById(listView_created.id)
        listView.choiceMode = ListView.CHOICE_MODE_NONE
        listView.isTextFilterEnabled = true

    }

    @SuppressLint("ResourceAsColor")
    private fun bindData() {
        allShipment.clear()
        ShipmentProcess().getShipmentCreated(this) {
            allShipment = Shipment.Deserializer().deserialize(it)!!
            listView.adapter = CustomAdapter(this, ArrayList(allShipment))
            val empty = findViewById<TextView>(empty.id)
            listView.emptyView = empty
            textView_shipment_count.text =  listView.count.toString()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.list_created, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_add_request -> {
                val bundle = Bundle()
                bundle.putSerializable(
                        "isUpdate",
                        false
                )
                val intent = Intent(this, CreateShipmentFastActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                ToastUtils.success(applicationContext, "Cập nhật thành công!")
                listView.clearChoices()
                bindData()
            }
        }
    }
}
