package dsc.vn.mybbdeliv.ListViewAdapter

import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import dsc.vn.mybbdeliv.Extension.toDateTime
import dsc.vn.mybbdeliv.Model.Shipment
import dsc.vn.mybbdeliv.R
import dsc.vn.mybbdeliv.Service.DistanceService
import dsc.vn.mybbdeliv.Service.LocationTrackingService
import java.util.*

/**
 * Created by NhatAnh on 11/20/2017.
 */
class CustomDeliveryAdapter(var context: Context, private var shipments: ArrayList<Shipment>) : BaseAdapter(), Filterable {


    var showCheckBox: Boolean = true
    var shipmentTemp: ArrayList<Shipment> = ArrayList()

    class ViewHolder(row: View?) {
        var tvRequestCode: TextView = row?.findViewById(R.id.tvRequestCode)!!
        var tvDistance: TextView = row?.findViewById(R.id.tvDistance)!!
        var tvCustomerName: TextView = row?.findViewById(R.id.tvCustomerName)!!
        var tvSenderName: TextView = row?.findViewById(R.id.tvSenderName)!!
        var tvPhoneNumber: TextView = row?.findViewById(R.id.tvPhoneNumber)!!
        var tvNumber: TextView = row?.findViewById(R.id.tvNumber)!!
        var tvAddress: TextView = row?.findViewById(R.id.tvAddress)!!
        var tvStatus: TextView = row?.findViewById(R.id.tvStatus)!!
        var tvTimePickup: TextView = row?.findViewById(R.id.tvTimePickup)!!
        var cbSelected: CheckBox = row?.findViewById(R.id.cbSelected)!!
        var tvNote: TextView = row?.findViewById(R.id.tvNote)!!
        var tvMoney: TextView = row?.findViewById(R.id.tvMoney)!!
        var btNote: ImageButton = row?.findViewById(R.id.btNote)!!
    }

    fun setSelected(id: Int, listView: ListView) {
        if (listView.choiceMode == ListView.CHOICE_MODE_SINGLE) {
            shipments.forEach { x -> x.checkBoxState = false }
        }
        shipments.firstOrNull { x -> x.id == id }?.checkBoxState = !shipments.firstOrNull { x -> x.id == id }?.checkBoxState!!
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults? {
                val filterResult = FilterResults()
                val results = ArrayList<Shipment>()
                var tempDataFilter: ArrayList<Shipment> = ArrayList()
                if (shipmentTemp.size == 0) {
                    shipmentTemp.addAll(shipments)
                }
                tempDataFilter.addAll(shipmentTemp)
                if (charSequence != null && charSequence.length > 0) {
                    if (tempDataFilter != null && tempDataFilter.size > 0) {
                        for (itemShip in tempDataFilter) {
                            if (itemShip.shipmentNumber.toLowerCase()
                                            .contains(charSequence.toString()))
                                results.add(itemShip)
                        }
                    }
                    filterResult.values = results
                } else {
                    filterResult.values = shipmentTemp
                }
                return filterResult
            }

            @SuppressWarnings("unchecked")
            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                shipments = filterResults.values as ArrayList<Shipment>
                notifyDataSetChanged()
            }
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View?
        val viewHolder: ViewHolder
        if (convertView == null) {
            val layout = LayoutInflater.from(context)
            view = layout.inflate(R.layout.listview_shipment_delivery, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val shipment: Shipment = getItem(position) as Shipment

        viewHolder.tvRequestCode.text = shipment.shipmentNumber
        viewHolder.tvSenderName.text = shipment.senderName
        viewHolder.tvDistance.text = DistanceService.getDistanceDefaultString(LocationTrackingService.lastLocation.latitude, LocationTrackingService.lastLocation.longitude, shipment.latTo.toString(), shipment.lngTo.toString())
        viewHolder.tvCustomerName.text = shipment.receiverName
        viewHolder.tvPhoneNumber.text = shipment.receiverPhone
        viewHolder.tvAddress.text = shipment.shippingAddress + shipment.addressNoteTo
        viewHolder.tvNumber.text = shipment.totalBox.toString()
        if (shipment.paymentTypeId != null) {
            if (shipment.paymentTypeId!! == 3) {
                viewHolder.tvMoney.text = "Cước : 0.0 đ + COD : ${shipment.cod} đ \nTổng : ${shipment.cod} đ"
            }
            if (shipment.paymentTypeId!! == 2) {
                viewHolder.tvMoney.text = "Cước : 0.0 đ + COD : ${shipment.cod} đ \nTổng : ${shipment.cod} đ"
            }
            if (shipment.paymentTypeId!! == 1) {
                if (shipment.totalPrice == null) {
                    shipment.totalPrice = 0.0
                }
                if (shipment.cod == null) {
                    shipment.cod = 0.0
                }
                viewHolder.tvMoney.text = "Cước :  ${shipment.totalPrice} đ + COD :  ${shipment.cod}  đ \nTổng : ${shipment.totalPrice!! + shipment.cod!!} đ"
            }
        }
        if (shipment.note == null)
        {
            viewHolder.tvNote.text = "Không có ghi chú"
        }
        else {
            viewHolder.tvNote.text = shipment.note
        }
        viewHolder.btNote.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setTitle("Nội dung")
            dialogBuilder.setNegativeButton("Đóng", null)
            if (shipment.note == null)
            {
                dialogBuilder.setMessage("Không có ghi chú")
            }
            else {
                dialogBuilder.setMessage(shipment.note)
            }
            // Set up the input
            dialogBuilder.show()
        }
        // Chờ lấy hàng
        if (shipment.shipmentStatusId == 41) {
            viewHolder.tvTimePickup.text = shipment.orderDate.toDateTime()
            viewHolder.tvStatus.text = "Chờ lấy hàng"
        }
        // Đang lấy hàng
        if (shipment.shipmentStatusId == 2) {
            viewHolder.tvTimePickup.text = shipment.orderDate.toDateTime()
            viewHolder.tvStatus.text = "Đang lấy hàng"
        }
        // Trung chuyển
        if (shipment.shipmentStatusId == 49) {
            viewHolder.tvAddress.text = shipment.toHub?.name
            viewHolder.tvTimePickup.text = shipment.firstTransferTime.toDateTime()
            viewHolder.tvStatus.text = "Chờ trung chuyển"
        }
        // Trung chuyển trả hàng
        if (shipment.shipmentStatusId == 50) {
            viewHolder.tvAddress.text = shipment.toHub?.name
            viewHolder.tvTimePickup.text = shipment.firstReturnTransferTime.toDateTime()
            viewHolder.tvStatus.text = "Chờ trung chuyển trả hàng"
        }
        // Đang trung chuyển
        if (shipment.shipmentStatusId == 8) {
            viewHolder.tvAddress.text = shipment.toHub?.name
            viewHolder.tvTimePickup.text = shipment.firstTransferTime.toDateTime()
            viewHolder.tvStatus.text = "Đang trung chuyển"
        }
        // Đang trung chuyển trả hàng
        if (shipment.shipmentStatusId == 37) {
            viewHolder.tvAddress.text = shipment.toHub?.name
            viewHolder.tvTimePickup.text = shipment.firstReturnTransferTime.toDateTime()
            viewHolder.tvStatus.text = "Đang trung chuyển trả hàng"
        }
        // Chờ giao hàng
        if (shipment.shipmentStatusId == 48) {
            viewHolder.tvTimePickup.text = shipment.firstDeliveredTime.toDateTime()
            viewHolder.tvStatus.text = "Chờ giao hàng"
        }
        // Chờ trả hàng
        if (shipment.shipmentStatusId == 51) {
            viewHolder.tvTimePickup.text = shipment.firstReturnTime.toDateTime()
            viewHolder.tvStatus.text = "Chờ trả hàng"
        }
        // Giao hàng
        if (shipment?.shipmentStatusId == 11) {
            viewHolder.tvTimePickup.text = shipment.expectedDeliveryTime.toDateTime()
            viewHolder.tvStatus.text = "Giao hàng"
        }
        // Trả hàng
        if (shipment.shipmentStatusId == 31) {
            viewHolder.tvTimePickup.text = shipment.startReturnTime.toDateTime()
            viewHolder.tvStatus.text = "Trả hàng"
        }
        viewHolder.cbSelected.isChecked = shipment.checkBoxState
        if (!showCheckBox) {
            viewHolder.cbSelected.visibility = View.GONE
        }
        return view!!
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