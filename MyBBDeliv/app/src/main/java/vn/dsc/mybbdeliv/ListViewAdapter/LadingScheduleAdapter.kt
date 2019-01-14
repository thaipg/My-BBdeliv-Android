package dsc.vn.mybbdeliv.ListViewAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import dsc.vn.mybbdeliv.Model.LadingSchedule
import dsc.vn.mybbdeliv.R
import java.util.ArrayList

/**
 * Created by thaiphan on 12/15/17.
 */
class LadingScheduleAdapter(var context: Context, private var ladingSchedules: ArrayList<LadingSchedule>) : BaseAdapter() {

    class ViewHolder(row: View?) {

        var lsTime: TextView = row?.findViewById(R.id.lsTime)!!
        var lsLocation: TextView = row?.findViewById(R.id.lsLocation)!!
        var lsDate: TextView = row?.findViewById(R.id.lsDate)!!
        var lsNote: TextView = row?.findViewById(R.id.lsNote)!!
        var lsStatus: TextView = row?.findViewById(R.id.lsStatus)!!
        var lsUserUpdate: TextView = row?.findViewById(R.id.lsUserUpdate)!!
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View?
        val viewHolder: ViewHolder
        if (convertView == null) {
            val layout = LayoutInflater.from(context)
            view = layout.inflate(R.layout.listview_lading_schedule, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val ladingSchedule: LadingSchedule = getItem(position) as LadingSchedule

        viewHolder.lsTime.text = ladingSchedule.timeCreated
        viewHolder.lsLocation.text = ladingSchedule.location
        viewHolder.lsDate.text = ladingSchedule.dateCreated
        viewHolder.lsNote.text = ladingSchedule.note
        viewHolder.lsStatus.text = ladingSchedule.shipmentStatusName
        viewHolder.lsUserUpdate.text = ladingSchedule.userFullName
        return view!!
    }

    override fun getItem(position: Int): Any {
        return ladingSchedules[position]
    }

    override fun getItemId(position: Int): Long {
        return ladingSchedules[position].id.toLong()
    }

    override fun getCount(): Int {
        return ladingSchedules.count()
    }
}