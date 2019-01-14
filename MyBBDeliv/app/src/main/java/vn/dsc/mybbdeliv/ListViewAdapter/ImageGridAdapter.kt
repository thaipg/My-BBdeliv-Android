package vn.dsc.mybbdeliv.ListViewAdapter

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import dsc.vn.mybbdeliv.R

class ImageGridAdapter (private var activity: Activity, private var items: ArrayList<Bitmap>) :  BaseAdapter(){
    private class ViewHolder(row: View?) {
        var imgCapture: ImageView? = null

        init {
            this.imgCapture = row?.findViewById<ImageView>(R.id.img_company)
        }
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.grid_single_item_image, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        var image = items[position]
        viewHolder.imgCapture?.scaleType = ImageView.ScaleType.FIT_CENTER
        viewHolder.imgCapture?.setImageBitmap(image)

        return view
    }
    override fun getItem(i: Int): Bitmap {
        return items[i]
    }
    override fun getItemId(i: Int): Long {
        return i.toLong()
    }
    override fun getCount(): Int {
        return items.size
    }
}