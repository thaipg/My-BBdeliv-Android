package dsc.vn.mybbdeliv.View.Tracking

import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.ListView
import dsc.vn.mybbdeliv.Base.BaseSubActivity
import dsc.vn.mybbdeliv.ListViewAdapter.LadingScheduleAdapter
import dsc.vn.mybbdeliv.Model.Shipment
import dsc.vn.mybbdeliv.Process.ShipmentProcess
import dsc.vn.mybbdeliv.R
import dsc.vn.mybbdeliv.R.layout.activity_shipment_detail
import kotlinx.android.synthetic.main.content_shipment_detail.*
import org.json.JSONObject


class ShipmentDetailActivity : BaseSubActivity() {

    var shipment: Shipment = Shipment()
    private lateinit var listView: ListView
    override val layoutResourceId: Int
        get() {
            return activity_shipment_detail
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipment_detail)

        //process inside
        prepareUI()
        bindData()
    }

    fun prepareUI()
    {
        listView = findViewById(listView_shipment_LadingHistory.id)
    }

    fun bindData() {
        val intent = intent
        val bundle = intent.extras
        shipment = bundle!!.getSerializable("Shipments") as Shipment

        title = shipment.shipmentNumber
        txtWeight.text = shipment.weight.toString() + " gram"
        txtCusName.text = shipment.senderName
        txtCusPhone.text = shipment.senderPhone
        txtCusAddress.text = shipment.pickingAddress

        txtShipmentCenterFromName.text = shipment.fromWard!!.district!!.province!!.name
        if (shipment.toWard != null) {
            txtShipmentCenterToName.text = shipment.toWard!!.district!!.province!!.name
        }
        txtCusNameTo.text = shipment.receiverName
        txtCusPhoneTo.text = shipment.receiverPhone
        txtAddressTo.text = shipment.shippingAddress

        txtCustomerReceivedReal.text = shipment.realRecipientName
        txtNote.text = shipment.note
        txtPrice.text = shipment.cod.toString()
        if (shipment.service != null) {
            txtServiceName.text = shipment.service!!.name
        }
        if (shipment.structure != null) {
            txtStructureName.text = shipment.structure!!.name
        }
        txtStatus.text = shipment.shipmentStatus!!.name

        listView.adapter = LadingScheduleAdapter(applicationContext, ArrayList(shipment.ladingSchedules))
        listView.choiceMode = ListView.CHOICE_MODE_NONE
        listView.isClickable = false

        //Set height for listview
        var totalHeight = 0
        for (i in 0 until listView.adapter.count) {
            val listItem = listView.adapter.getView(i, null, listView)
            listItem.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }

        val params = listView.layoutParams
        params.height = totalHeight + listView.dividerHeight * (listView.adapter.count - 1)
        listView.layoutParams = params
        listView.requestLayout()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_view_signature, menu)
        val item = menu.findItem(R.id.action_view_signature)
        item.isEnabled = shipment.deliveryImagePath != null
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_view_signature -> {
                ShipmentProcess().downloadImage(this, shipment.deliveryImagePath!! ,{
                    val jsonObj = JSONObject(it)
                    if (!jsonObj["fileBase64String"].toString().isNullOrEmpty())
                        loadImage("Chữ ký", jsonObj["fileBase64String"].toString())
                })
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadImage(title: String, encodedImage: String) {
        val decodedString = Base64.decode(encodedImage, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle(title)
        dialogBuilder.setNegativeButton("Đóng", null)

        // Set up the input
        val imageView = ImageView(this)
        imageView.setImageBitmap(decodedByte)
        dialogBuilder.setView(imageView)

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        val negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        // override the text color of negative button
        negativeButton.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
        // provides custom implementation to negative button click
        negativeButton.setOnClickListener { alertDialog.dismiss() }
    }
}
