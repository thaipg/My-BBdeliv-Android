package dsc.vn.mybbdeliv.View.Delivery

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.yanzhenjie.album.Album
import dsc.vn.mybbdeliv.Base.BaseSubActivity
import dsc.vn.mybbdeliv.Model.Shipment
import dsc.vn.mybbdeliv.Process.ShipmentProcess
import dsc.vn.mybbdeliv.R
import dsc.vn.mybbdeliv.R.layout.activity_delivery_success
import dsc.vn.mybbdeliv.Service.LocationTrackingService
import dsc.vn.mybbdeliv.Utils.ToastUtils
import kotlinx.android.synthetic.main.content_delivery_success.*
import org.json.JSONObject
import vn.dsc.mybbdeliv.ListViewAdapter.ImageGridAdapter
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class DeliverySuccessActivity : BaseSubActivity() , View.OnClickListener {

    private var bitmapSign: Bitmap? = null
    private var adapter: ImageGridAdapter? = null
    private lateinit var mAlbumFiles: ArrayList<Bitmap>
    var selectedItems: MutableList<Shipment> = mutableListOf()

    override val layoutResourceId: Int
        get() {
            return activity_delivery_success
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_delivery_success)

        prepareUI()
        bindData()
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
                Album.camera(this) // Camera function.
                        .image() // Take Picture.
                        .onResult{
                            val filePath = it
                            val imgFile = File(filePath)
                            if (imgFile.exists()) {
                                val options = BitmapFactory.Options()
                                options.inSampleSize = 8
                                bitmapSign = BitmapFactory.decodeFile(imgFile.absolutePath,options)
                                mAlbumFiles.add(bitmapSign!!)
                                adapter = ImageGridAdapter(this, mAlbumFiles)
                                grid.adapter = adapter
                                imgFile.delete()
                            }
                        }
                        .onCancel{

                        }
                        .start()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun prepareUI()
    {
        setupUI(findViewById<View>(clLayout.id))
        btnUpdate.setOnClickListener(this)
        mAlbumFiles = ArrayList<Bitmap>()
    }

    private fun bindData()
    {
        val intent = intent
        val bundle = intent.extras
        selectedItems = (bundle!!.getSerializable("Shipments") as ArrayList<Shipment>).toMutableList()
    }

    override fun onClick(v: View?) {
        if (selectedItems.count() == 0) {
            ToastUtils.error(applicationContext, "Vui lòng chọn vận đơn !")
            return
        }

        if (v != null) {
            if (v.id == btnUpdate.id) {
                if (tfActualRecipient.text.isEmpty())
                {
                    ToastUtils.error(this,"Vui lòng nhập thông tin người nhận thực tế!")
                    return
                }

                for (item in selectedItems)
                {
                    //Lấy hàng thành công
                    if (item.shipmentStatusId == 11)
                    {
                        item.shipmentStatusId = 12
                    }
                    //Trả hàng thành công
                    else if (item.shipmentStatusId == 31)
                    {
                        item.shipmentStatusId = 26
                    }

                    item.currentLat = LocationTrackingService.lastLocation.latitude
                    item.currentLng = LocationTrackingService.lastLocation.longitude
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(
                            LocationTrackingService.lastLocation.latitude,
                            LocationTrackingService.lastLocation.longitude,
                            1)

                    var address: Address? = null
                    if (addresses.count() > 0) {
                        address = addresses[0]
                    }

                    if (address != null) {
                        val addressFragments = (0..address.maxAddressLineIndex).map(address::getAddressLine)
                        item.location = TextUtils.join(System.getProperty("line.separator"),
                                addressFragments).replace("\n", ", ")
                    }
                    item.realRecipientName = tfActualRecipient.text.toString()
                    item.note = editTextNote.text.toString()

                    if (mAlbumFiles.count() > 0) {

                        ShipmentProcess().updateShipment(this, item) {
                            mAlbumFiles.forEachIndexed { index, fileAlbum ->
                                val bitmap = Bitmap.createScaledBitmap(fileAlbum, fileAlbum.width * 2, fileAlbum.height * 2, true)
                                val stream = ByteArrayOutputStream()
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                                val byteArray = stream.toByteArray()
                                val imgString = Base64.encodeToString(byteArray, Base64.NO_WRAP)
                                val uploadData = JSONObject()
                                uploadData.put("ShipmentId", item.id)
                                uploadData.put("FileName", item.shipmentNumber + "_" + index + ".jpg")
                                uploadData.put("FileExtension", "jpg")
                                uploadData.put("FileBase64String", imgString)

                                ShipmentProcess().uploadDeliveryComplete(this, uploadData) {
                                    Log.v("Upload", "Đã upload hình")
                                }

                                if (item.id == selectedItems.last().id) {
                                    if (fileAlbum == mAlbumFiles.last()) {
                                        ToastUtils.success(this, "Cập nhật thành công!")
                                        val intent = Intent(this, DeliveryViewActivity::class.java)
                                        startActivity(intent)
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        ShipmentProcess().updateShipment(this, item) {
                            if (item.id == selectedItems.last().id) {
                                ToastUtils.success(this,"Cập nhật thành công!")
                                val intent = Intent(this, DeliveryViewActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupUI(view: View) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                view.clearFocus()
                hideSoftKeyboard(this@DeliverySuccessActivity)
                false
            }
        }

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            (0 until view.childCount)
                    .map { view.getChildAt(it) }
                    .forEach(this::setupUI)
        }
    }

    private fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus?.windowToken, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            if (resultCode == Activity.RESULT_OK) {

            }
        } catch (e: OutOfMemoryError) {
            ToastUtils.error(this, "Không đủ dung lượng để lưu ảnh!")
        } catch (e: Exception) {
            ToastUtils.error(this, e.localizedMessage)
        }
    }
}
