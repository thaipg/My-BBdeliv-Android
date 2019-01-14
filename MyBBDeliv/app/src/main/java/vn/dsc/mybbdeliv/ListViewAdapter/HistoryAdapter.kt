package dsc.vn.mybbdeliv.ListViewAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import dsc.vn.mybbdeliv.Model.History
import dsc.vn.mybbdeliv.Model.Shipment
import dsc.vn.mybbdeliv.R

/**
 * Created by thaiphan on 1/4/18.
 */
class HistoryAdapter(var context: Context, private var shipments: ArrayList<History>) : BaseAdapter() {
   class ViewHolder(row: View?) {
        var tvRequestCode: TextView = row?.findViewById(R.id.tvRequestCode)!!
        var tvStatus:TextView = row?.findViewById(R.id.tvStatus)!!
        var tvTimePickup: TextView = row?.findViewById(R.id.tvTimePickup)!!
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View?
        val viewHolder: ViewHolder
        if (convertView == null) {
            val layout = LayoutInflater.from(context)
            view = layout.inflate(R.layout.listview_history, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val shipment = getItem(position) as History
        viewHolder.tvRequestCode.text = shipment.shipment!!.shipmentNumber
        viewHolder.tvStatus.text = shipment.shipmentStatus!!.name
        viewHolder.tvTimePickup.text = shipment.shipment!!.createdWhen
        return view!!
    }

    override fun getItem(position: Int): Any {
        return shipments[position]
    }

    override fun getItemId(position: Int): Long {
        return shipments[position].shipmentId!!.toLong()
    }

    override fun getCount(): Int {
        return shipments.count()
    }
}