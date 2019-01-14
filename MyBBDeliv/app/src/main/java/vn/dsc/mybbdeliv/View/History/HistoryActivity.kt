package dsc.vn.mybbdeliv.View.History

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import kotlinx.android.synthetic.main.content_history.*
import dsc.vn.mybbdeliv.Base.BaseActivity
import dsc.vn.mybbdeliv.ListViewAdapter.HistoryAdapter
import dsc.vn.mybbdeliv.Model.History
import dsc.vn.mybbdeliv.Process.ShipmentProcess
import dsc.vn.mybbdeliv.R
import java.util.*


class HistoryActivity : BaseActivity() {

    private var allShipment: MutableList<History> = mutableListOf()
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_history)
        super.onCreate(savedInstanceState)

        prepareUI()
        bindData()
    }

    private fun prepareUI() {
        listView = findViewById<ListView>(listView_history.id)
        listView.choiceMode = ListView.CHOICE_MODE_NONE
    }

    @SuppressLint("ResourceAsColor")
    private fun bindData() {
        allShipment.clear()
        ShipmentProcess().getCurrentEmpHistory(this, {
            allShipment = History.Deserializer().deserialize(it)!!
            listView.adapter = HistoryAdapter(this, ArrayList(allShipment))
            val empty = findViewById<TextView>(empty.id)
            listView.emptyView = empty
            showSelectedItemText()
        })
    }


    @SuppressLint("SetTextI18n")
    private fun showSelectedItemText() {
        if (listView.checkedItemCount > 0) {
            tvFooter.text = "Số lượng vận đơn : " + listView.count
        } else {
            tvFooter.text = "Số lượng vận đơn : " + listView.count
        }
    }


}
