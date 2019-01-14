package dsc.vn.mybbdeliv.ListViewAdapter

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import dsc.vn.mybbdeliv.Model.Shipment
import dsc.vn.mybbdeliv.R
import dsc.vn.mybbdeliv.Service.DistanceService
import dsc.vn.mybbdeliv.Service.LocationTrackingService
import java.util.*

class CustomListCreateAdapter(var context: Context, private var shipments: ArrayList<Shipment>, var isSender: Boolean = true) : BaseAdapter() {

    var showCheckBox: Boolean = true
    class ViewHolder(row: View?) {

        var tvRequestCode: TextView = row?.findViewById(R.id.tvRequestCode)!!
        var lblCustomerCode: TextView = row?.findViewById(R.id.lblCustomerCode)!!
        var tvCustomerCode: TextView = row?.findViewById(R.id.tvCustomerCode)!!
        var tvDistance: TextView = row?.findViewById(R.id.tvDistance)!!
        var tvCustomerName: TextView = row?.findViewById(R.id.tvCustomerName)!!
        var tvPhoneNumber: TextView = row?.findViewById(R.id.tvPhoneNumber)!!
        var tvAddress: TextView = row?.findViewById(R.id.tvAddress)!!
        var listViewLayout: LinearLayout = row?.findViewById(R.id.linearLayout_item_shipment)!!
        var btEditShipment: Button = row?.findViewById(R.id.btEditShipment)!!
        var btScanShipment: Button = row?.findViewById(R.id.btScanShipment)!!
    }

    fun setSelected(id: Int, listView: ListView)  {
        if (listView.choiceMode == ListView.CHOICE_MODE_SINGLE)
        {
            shipments.forEach { x -> x.checkBoxState = false }
        }
        (shipments.firstOrNull { x -> x.id == id })?.checkBoxState = !(shipments.firstOrNull { x -> x.id == id })?.checkBoxState!!
    }

    fun checkAll(isCheckAll: Boolean)  {
        for (s in shipments)
        {
            s.checkBoxState = isCheckAll
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View?
        val viewHolder: ViewHolder
        if (convertView == null) {
            val layout = LayoutInflater.from(context)
            view = layout.inflate(R.layout.listview_list_shipment_create, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val shipment: Shipment = getItem(position) as Shipment

        viewHolder.tvRequestCode.text = shipment.shipmentNumber
        viewHolder.tvCustomerCode.text = shipment.totalPrice.toString() + " đ"
        viewHolder.tvDistance.text = DistanceService.getDistanceDefaultString(LocationTrackingService.lastLocation.latitude, LocationTrackingService.lastLocation.longitude, shipment.latFrom.toString(), shipment.lngFrom.toString())
        viewHolder.tvCustomerName.text =  shipment.receiverName
        viewHolder.tvPhoneNumber.text =  shipment.receiverPhone
        viewHolder.tvAddress.text =  shipment.shippingAddress
        viewHolder.btScanShipment.setOnClickListener {
            if (btListner != null) {
                btListner!!.onScanShipmentClickListner(position, shipment)
            }
        }
        viewHolder.btEditShipment.setOnClickListener {
            if (btListner != null) {
                btListner!!.onEditShipmentClickListner(position, shipment)
            }
        }
        if (shipment.shipmentStatusId == 2)
        {
            viewHolder.btScanShipment.visibility = View.VISIBLE
            viewHolder.btEditShipment.visibility = View.VISIBLE
            viewHolder.listViewLayout.background = ContextCompat.getDrawable(context, R.drawable.border_left_red)
        }
        else
        {
            viewHolder.btScanShipment.visibility = View.GONE
            viewHolder.btEditShipment.visibility = View.GONE
            viewHolder.listViewLayout.background = ContextCompat.getDrawable(context, R.drawable.border_left_blue)
        }

        return view!!
    }

    var btListner: btListClickListener? = null

    interface btListClickListener {
        fun onScanShipmentClickListner(position: Int, value: Shipment)
        fun onEditShipmentClickListner(position: Int, value: Shipment)
    }

    fun setbtListner(listener: btListClickListener) {
        this.btListner = listener
    }

    override fun getItem(position: Int): Any {
        return shipments[position]
    }

    override fun getItemId(position: Int): Long {
        return shipments[position].id.toLong()
    }

    override fun getCount(): Int {
        return shipments.count()
    }
}