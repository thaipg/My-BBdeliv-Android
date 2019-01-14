package dsc.vn.mybbdeliv.View.Delivery.Vietstar

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.google.zxing.integration.android.IntentIntegrator
import com.yanzhenjie.album.Album
import dsc.vn.mybbdeliv.Base.BaseActivity
import dsc.vn.mybbdeliv.Camera.ZXingUtils
import dsc.vn.mybbdeliv.Process.ShipmentProcess
import dsc.vn.mybbdeliv.Process.UserProcess
import dsc.vn.mybbdeliv.R
import dsc.vn.mybbdeliv.Utils.ToastUtils
import kotlinx.android.synthetic.main.content_delivery_vietstar.*
import vn.dsc.mybbdeliv.ListViewAdapter.ImageGridAdapter
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class DeliveryVietstarActivity : BaseActivity() , View.OnClickListener {


    private var bitmapSign: Bitmap? = null
    private var adapter: ImageGridAdapter? = null
    private lateinit var mAlbumFiles: ArrayList<Bitmap>

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_delivery_vietstar)
        super.onCreate(savedInstanceState)
        prepareUI()
        bindData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_camera_vietstar, menu)
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
            R.id.action_mode_close_button -> {
                resetForm()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun prepareUI() {
        setupUI(findViewById<View>(clLayout.id))
        imageView_camera.setOnClickListener(this)
        btnUpdate.setOnClickListener(this)
        mAlbumFiles = ArrayList<Bitmap>()
    }

    private fun bindData() {

    }

    override fun onClick(v: View?) {
        if (v != null) {
            when {
                v.id == btnUpdate.id -> updateVietstar()
                v.id == imageView_camera.id -> getBarcodeScan()
            }
        }
    }

    private fun getBarcodeScan() {
        ZXingUtils.initiateScan(this)
    }

    private fun updateVietstar()
    {
        if (txtShipmentCode.text.isEmpty())
        {
            ToastUtils.error(this,"Vui lòng nhập mã vận đơn!")
            return
        }
        if (tfActualRecipient.text.isEmpty())
        {
            ToastUtils.error(this,"Vui lòng nhập thông tin người nhận thực tế!")
            return
        }
        if (mAlbumFiles.count() > 0) {
            mAlbumFiles.forEachIndexed { index, fileAlbum ->
                var userName = UserProcess.token!!.userName
                val bitmap = Bitmap.createScaledBitmap(fileAlbum, fileAlbum.width * 2, fileAlbum.height * 2, true)
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                val byteArray = stream.toByteArray()
                val imgString = Base64.encodeToString(byteArray, Base64.NO_WRAP)
                val c = Calendar.getInstance()
                val dateString = DateFormat.format("yyyyMMddhhmmss", c.time)
                var uploadData = ArrayList<Pair<String, Any>>(9)
                uploadData.add(Pair<String, Any>("VD_ID", 0))
                uploadData.add(Pair<String, Any>("SVD", txtShipmentCode.text))
                uploadData.add(Pair<String, Any>("SMS_ID", 0))
                uploadData.add(Pair<String, Any>("MANV", userName))
                uploadData.add(Pair<String, Any>("KY_NHAN", tfActualRecipient.text))
                uploadData.add(Pair<String, Any>("LY_DO", ""))
                uploadData.add(Pair<String, Any>("CREATED", ""))
                uploadData.add(Pair<String, Any>("FILENAME", userName + "_" + dateString + "_" + index + ".jpg"))
                uploadData.add(Pair<String, Any>("IMAGE", imgString))

                ShipmentProcess().deliveryVietstar(this, uploadData) {
                    if (fileAlbum == mAlbumFiles.last()) {
                        ToastUtils.success(this, "Cập nhật thành công!")
                        resetForm()
                    }
                }
            }
        } else {
            ToastUtils.error(this, "Vui lòng chụp hình ảnh trước khi upload!")
        }
    }

    private fun resetForm() {
        txtShipmentCode.setText("")
        tfActualRecipient.setText("")
        mAlbumFiles = ArrayList<Bitmap>()
        adapter = ImageGridAdapter(this, mAlbumFiles)
        grid.adapter = adapter
    }

    private fun setupUI(view: View) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                view.clearFocus()
                hideSoftKeyboard(this@DeliveryVietstarActivity)
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
                if (requestCode == ZXingUtils.REQUEST_CODE) {
                    val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                    Log.d("RequestCode", "ZXingUtils.REQUEST_CODE: " + ZXingUtils.REQUEST_CODE + " - " + result!!.contents)
                    if (result.contents != null) {
                        txtShipmentCode.setText(result.contents)
                    }
                }
            }
        } catch (e: OutOfMemoryError) {
            ToastUtils.error(this, "Không đủ dung lượng để lưu ảnh!")
        } catch (e: Exception) {
            ToastUtils.error(this, e.localizedMessage)
        }
    }
}
