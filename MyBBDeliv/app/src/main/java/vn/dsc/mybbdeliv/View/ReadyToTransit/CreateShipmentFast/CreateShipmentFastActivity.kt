package dsc.vn.mybbdeliv.View.ReadyToTransit.CreateShipmentFast

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.google.zxing.integration.android.IntentIntegrator
import com.yanzhenjie.album.Album
import dsc.vn.mybbdeliv.Base.BaseSubActivity
import dsc.vn.mybbdeliv.Camera.ZXingUtils
import dsc.vn.mybbdeliv.Extension.isNotEmptyTextField
import dsc.vn.mybbdeliv.Model.Shipment
import dsc.vn.mybbdeliv.Process.ShipmentProcess
import dsc.vn.mybbdeliv.Process.UserProcess
import dsc.vn.mybbdeliv.R
import dsc.vn.mybbdeliv.Service.LocationTrackingService
import dsc.vn.mybbdeliv.Utils.ToastUtils
import dsc.vn.mybbdeliv.View.ReadyToTransit.ListCreatedActivity
import dsc.vn.mybbdeliv.View.ReadyToTransit.ReadyToTransitActivity
import kotlinx.android.synthetic.main.content_create_shipment_fast.*
import org.json.JSONObject
import vn.dsc.mybbdeliv.ListViewAdapter.ImageGridAdapter
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CreateShipmentFastActivity : BaseSubActivity() , View.OnClickListener {

    private var bitmapSign: Bitmap? = null
    private var shipment: Shipment = Shipment()

    var isUpdate:Boolean = true

    private var adapter: ImageGridAdapter? = null
    private lateinit var mAlbumFiles: ArrayList<Bitmap>

    override val layoutResourceId: Int
        get() {
            return R.layout.activity_create_shipment_fast
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_shipment_fast)
        prepareUI()
        bindData()
    }

    private fun prepareUI() {
        setupUI(findViewById<View>(clLayout.id))
        mAlbumFiles = ArrayList<Bitmap>()
    }

    private fun bindData() {
        val intent = intent
        val bundle = intent.extras
        if (bundle!!.getSerializable("shipment") != null) {
            shipment = (bundle.getSerializable("shipment") as Shipment)
        }
        if (bundle.getSerializable("isUpdate") != null) {
            isUpdate = (bundle.getSerializable("isUpdate") as Boolean)
        }
        imageView_camera.setOnClickListener(this)
        imageView_scan_camera.setOnClickListener(this)
        btSubmit.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when {
                v.id == btSubmit.id -> submit()
                v.id == imageView_scan_camera.id -> getBarcodeScan()
                v.id == imageView_camera.id -> getPicture()
            }
        }
    }

    private fun getPicture() {
        Album.camera(this) // Camera function.
                .image() // Take Picture
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
    }

    private fun submit() {
        if (txtShipmentCode.isNotEmptyTextField(this, "Vui lòng nhập mã vận đơn")) {
            return
        }

        shipment.shipmentStatusId = 3
        shipment.shipmentNumber = txtShipmentCode.text.toString()
        shipment.fromWard = null
        shipment.toWard = null
        shipment.service = null
        shipment.sender = null
        shipment.ladingSchedules = null
        shipment.requestShipmentId = shipment.id

        shipment.currentLat = LocationTrackingService.lastLocation.latitude
        shipment.currentLng = LocationTrackingService.lastLocation.longitude
        shipment.latFrom = LocationTrackingService.lastLocation.latitude
        shipment.lngFrom = LocationTrackingService.lastLocation.longitude

        shipment.pickUserId = UserProcess.token!!.userId
        shipment.currentEmpId = UserProcess.token!!.userId

        if (mAlbumFiles.count() > 0) {
            shipment.toProvinceId = shipment.fromProvinceId
            shipment.toDistrictId = shipment.fromDistrictId
            shipment.toWardId = shipment.fromWardId
            shipment.toHubId = shipment.fromHubId
            shipment.requestShipmentId = shipment.id
            if (isUpdate) {
                ShipmentProcess().updateRequestShipment(this, shipment) {
                    shipment.id = 0
                    print(shipment)

                    ShipmentProcess().createShipment(this, shipment) {
                        val shipmentDetail = Shipment.Deserializer().deserializeSingle(it)

                        val uploadDataJson = JSONObject()
                        mAlbumFiles.forEachIndexed { index, fileAlbum ->
                            val bitmap = Bitmap.createScaledBitmap(fileAlbum, 1344, 1008, true)
                            val stream = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                            val byteArray = stream.toByteArray()
                            val imgString = Base64.encodeToString(byteArray, Base64.NO_WRAP)
                            uploadDataJson.put("ShipmentId", shipmentDetail.id)
                            uploadDataJson.put("FileName", shipmentDetail.shipmentNumber + "_" + index + ".jpg")
                            uploadDataJson.put("FileExtension", "jpg")
                            uploadDataJson.put("FileBase64String", imgString)

                            ShipmentProcess().uploadImagePickupComplete(this, uploadDataJson) {
                                Log.v("Upload", "Đã upload hình")
                                stream.flush()
                                stream.close()
                            }

//                            if (JsonWebservice().post_api_endPoint == "http://postapi.bbdeliv.vn/api") {
//                                var userName = ""
//                                if (JsonWebservice().post_api_endPoint == "http://postapi.bbdeliv.vn/api") {
//                                    userName = UserProcess.token!!.userName
//                                } else {
//                                    userName = "50611"
//                                }
//
//                                val c = Calendar.getInstance()
//                                val dateString = DateFormat.format("yyyyMMddhhmmss", c.time)
//                                var uploadData = ArrayList<Pair<String, Any>>(9)
//                                uploadData.add(Pair<String, Any>("VD_ID", 0))
//                                uploadData.add(Pair<String, Any>("SVD", txtShipmentCode.text))
//                                uploadData.add(Pair<String, Any>("SMS_ID", 0))
//                                uploadData.add(Pair<String, Any>("MANV", userName))
//                                uploadData.add(Pair<String, Any>("KY_NHAN", ""))
//                                uploadData.add(Pair<String, Any>("LY_DO", ""))
//                                uploadData.add(Pair<String, Any>("CREATED", ""))
//                                uploadData.add(Pair<String, Any>("FILENAME", userName + "_" + dateString + "_" + index + ".jpg"))
//                                uploadData.add(Pair<String, Any>("IMAGE", imgString))
//
//                                ShipmentProcess().pickupVietstar(this, uploadData) {
//                                    if (fileAlbum == mAlbumFiles.last()) {
//                                        ToastUtils.success(this, "Cập nhật thành công!")
//                                        val intent = Intent(this, ReadyToTransitActivity::class.java)
//                                        startActivity(intent)
//                                    }
//                                }
//                            } else {
                                ToastUtils.success(this, "Cập nhật thành công!")
                                val intent = Intent(this, ReadyToTransitActivity::class.java)
                                startActivity(intent)
//                            }
                        }
                    }
                }
            } else {
                shipment.senderId = 6
                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                shipment.orderDate = format.format(Calendar.getInstance().time)
                ShipmentProcess().createShipment(this, shipment) {
                    val shipmentDetail = Shipment.Deserializer().deserializeSingle(it)
                    mAlbumFiles.forEachIndexed { index, fileAlbum ->
                        val bitmap = Bitmap.createScaledBitmap(fileAlbum, 1344, 1008, true)
                        val stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                        val byteArray = stream.toByteArray()
                        val imgString = Base64.encodeToString(byteArray, Base64.NO_WRAP)
                        val uploadDataJson = JSONObject()
                        uploadDataJson.put("ShipmentId", shipmentDetail.id)
                        uploadDataJson.put("FileName", shipmentDetail.shipmentNumber + "_" + index + ".jpg")
                        uploadDataJson.put("FileExtension", "jpg")
                        uploadDataJson.put("FileBase64String", imgString)

                        ShipmentProcess().uploadImagePickupComplete(this, uploadDataJson) {
                            Log.v("Upload", "Đã upload hình")
                            stream.flush()
                            stream.close()
                        }

//                        if (JsonWebservice().post_api_endPoint == "http://postapi.bbdeliv.vn/api") {
//                            var userName = ""
//                            if (JsonWebservice().post_api_endPoint == "http://postapi.bbdeliv.vn/api") {
//                                userName = UserProcess.token!!.userName
//                            } else {
//                                userName = "50611"
//                            }
//
//                            val c = Calendar.getInstance()
//                            val dateString = DateFormat.format("yyyyMMddhhmmss", c.time)
//                            var uploadData = ArrayList<Pair<String, Any>>(9)
//                            uploadData.add(Pair<String, Any>("VD_ID", 0))
//                            uploadData.add(Pair<String, Any>("SVD", txtShipmentCode.text))
//                            uploadData.add(Pair<String, Any>("SMS_ID", 0))
//                            uploadData.add(Pair<String, Any>("MANV", userName))
//                            uploadData.add(Pair<String, Any>("KY_NHAN", ""))
//                            uploadData.add(Pair<String, Any>("LY_DO", ""))
//                            uploadData.add(Pair<String, Any>("CREATED", ""))
//                            uploadData.add(Pair<String, Any>("FILENAME", userName + "_" + dateString + "_" + index + ".jpg"))
//                            uploadData.add(Pair<String, Any>("IMAGE", imgString))
//
//                            ShipmentProcess().pickupVietstar(this, uploadData) {
//                                if (fileAlbum == mAlbumFiles.last()) {
//                                    ToastUtils.success(this, "Thêm mới thành công!")
//                                    val intent = Intent(this, ListCreatedActivity::class.java)
//                                    startActivity(intent)
//                                }
//                            }
//
//                        } else {
                            ToastUtils.success(this, "Thêm mới thành công!")
                            val intent = Intent(this, ListCreatedActivity::class.java)
                            startActivity(intent)
//                        }
                    }
                }
            }

        } else {
            ToastUtils.error(this, "Vui lòng chụp hình ảnh trước khi upload!")
        }
    }


    private fun getBarcodeScan() {
        ZXingUtils.initiateScan(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            try {
                if (requestCode == ZXingUtils.REQUEST_CODE) {
                    val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                    Log.d("RequestCode", "ZXingUtils.REQUEST_CODE: " + ZXingUtils.REQUEST_CODE + " - " + result!!.contents)
                    if (result.contents != null) {
                        txtShipmentCode.setText(result.contents)
                    }
                }
            } catch (e: OutOfMemoryError) {
                ToastUtils.error(this, "Không đủ dung lượng để lưu ảnh!")
            } catch (e: Exception) {
                ToastUtils.error(this, e.localizedMessage)
            }
        }
    }

    private fun setupUI(view: View) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                view.clearFocus()
                hideSoftKeyboard(this@CreateShipmentFastActivity)
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
}