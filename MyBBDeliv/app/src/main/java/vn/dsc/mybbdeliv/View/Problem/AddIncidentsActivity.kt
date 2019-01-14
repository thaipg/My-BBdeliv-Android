package vn.dsc.mybbdeliv.View.Problem

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import com.google.zxing.integration.android.IntentIntegrator
import com.yanzhenjie.album.Album
import dsc.vn.mybbdeliv.Base.BaseActivity
import dsc.vn.mybbdeliv.Camera.ZXingUtils
import dsc.vn.mybbdeliv.Model.Item
import dsc.vn.mybbdeliv.Model.Reason
import dsc.vn.mybbdeliv.Model.Shipment
import dsc.vn.mybbdeliv.Process.GeneralProcess
import dsc.vn.mybbdeliv.Process.ShipmentProcess
import dsc.vn.mybbdeliv.R
import dsc.vn.mybbdeliv.Utils.ToastUtils
import kotlinx.android.synthetic.main.content_add_incidents.*
import org.json.JSONObject
import vn.dsc.mybbdeliv.ListViewAdapter.ImageGridAdapter
import vn.dsc.mybbdeliv.Model.Incident
import java.io.ByteArrayOutputStream
import java.io.File

class AddIncidentsActivity : BaseActivity() , View.OnClickListener{

    var selectedItems: MutableList<Shipment> = mutableListOf()
    private var selectedReason: Reason? = null

    private var bitmapSign: Bitmap? = null
    private var adapter: ImageGridAdapter? = null
    private lateinit var mAlbumFiles: ArrayList<Bitmap>

    private var resultListEmp:MutableList<Item> = mutableListOf()
    private var adapterEmp: ArrayAdapter<String>? = null
    private var selectEmp: Item? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_add_incidents)
        super.onCreate(savedInstanceState)
        prepareUI()
        bindData()
    }
    private fun prepareUI()
    {
        setupUI(findViewById<View>(clLayout.id))
        btnUpdate.setOnClickListener(this)
        btSelectReason.setOnClickListener(this)
        imageView_scan_camera.setOnClickListener(this)
        imageView_camera.setOnClickListener(this)
        mAlbumFiles = ArrayList<Bitmap>()


        txtEmployee.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if (s.length >= 2) {
                        GeneralProcess().getEmployeeSearchByValue(s.toString()) {
                            Log.v("Emp :", it)
                            // If the response is JSONObject instead of expected JSONArray
                            resultListEmp = Item.Deserializer().deserialize(it)!!
                            setupAutocompleteViewEmployee()
                        }
                    }
                }
            }
        })

        txtEmployee.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            selectEmp = resultListEmp[position]
        }

    }

    private fun bindData()
    {
    }

    private fun setupAutocompleteViewEmployee()
    {
        // Get the string array
        val pickEmp = java.util.ArrayList<Item>()
        val pickEmpName = java.util.ArrayList<String>()
        // Create the adapter and set it to the AutoCompleteTextView
        adapterEmp = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_dropdown_item, pickEmpName)
        for (r in resultListEmp) {
            pickEmp.add(r)
            pickEmpName.add(r.code + " - " + r.name)
        }
        txtEmployee.setAdapter(adapterEmp)
        txtEmployee.setTextColor(Color.BLACK)
        txtEmployee.setHintTextColor(Color.LTGRAY)
        txtEmployee.highlightColor = Color.WHITE
        txtEmployee.setLinkTextColor(Color.WHITE)
        txtEmployee.showDropDown()
    }

    override fun onClick(v: View?) {

        if (v != null) {
            when {
                v.id == imageView_scan_camera.id -> {
                    getBarcodeScan()
                }
                v.id == btnUpdate.id -> {
                    if (txtShipmentCode.text.isEmpty())
                    {
                        ToastUtils.error(this,"Chưa nhập mã !")
                        return
                    }
                    if(selectedReason == null)
                    {
                        ToastUtils.error(this,"Vui lòng chọn lý do !")
                        return
                    }



                    if (mAlbumFiles.count() > 0) {

                        val json = JSONObject()
                        json.put("ShipmentNumber", txtShipmentCode.text)
                        json.put("IncidentsReasonId", selectedReason!!.id)
                        json.put("IncidentsContent", editTextNote.text)
                        json.put("IncidentsByEmpId", selectEmp!!.id)

                        ShipmentProcess().addIncidents(this, json)
                        {
                            val incident = Incident.Deserializer().deserializeSingle(it)!!
                            mAlbumFiles.forEachIndexed { index, fileAlbum ->
                                val bitmap = Bitmap.createScaledBitmap(fileAlbum, fileAlbum.width * 2, fileAlbum.height * 2, true)
                                val stream = ByteArrayOutputStream()
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                                val byteArray = stream.toByteArray()
                                val imgString = Base64.encodeToString(byteArray, Base64.NO_WRAP)
                                val uploadData = JSONObject()
                                uploadData.put("ShipmentId", incident.shipmentId)
                                uploadData.put("IncidentsId", incident.id)
                                uploadData.put("FileName", incident.shipmentId + "_" + index + ".jpg")
                                uploadData.put("FileExtension", "jpg")
                                uploadData.put("FileBase64String", imgString)

                                ShipmentProcess().uploadImageIncidents(this, uploadData) {
                                    Log.v("Upload", "Đã upload hình")
                                }
                            }
                            Log.v("result", it)
                            ToastUtils.error(this, "Cập nhật thành công")
                            resetForm()
                        }
                    }
                    else
                    {
                        ToastUtils.error(this,"Vui lòng chụp hình sự cố!")
                        return
                    }

                }
                v.id == btSelectReason.id -> GeneralProcess().getReasonIncidents(this) {
                    val allReason = Reason.Deserializer().deserialize(it)!!
                    val pickRejectReason: ArrayList<Reason> = ArrayList()
                    val pickRejectReasonName: ArrayList<String> = ArrayList()
                    allReason.forEach { r ->
                        pickRejectReason.add(r)
                        pickRejectReasonName.add(r.name)
                    }

                    val arrayAdapter = ArrayAdapter<String>(
                            this, // Context
                            android.R.layout.simple_list_item_single_choice, // Layout
                            pickRejectReasonName // List
                    )

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Chọn lý do")
                    builder.setSingleChoiceItems(
                            arrayAdapter, // Items list
                            -1 // Index of checked item (-1 = no selection)
                    ) { v , i ->
                        // Item click listener
                        // Get the alert dialog selected item's text
                        selectedReason = pickRejectReason[i]
                        btSelectReason.setText(selectedReason!!.name)
                        v.dismiss()
                        // Display the selected item's text on snack bar
                    }

                    builder.show()
                }
                v.id == imageView_camera.id -> {
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
                }
            }
        }
    }

    fun resetForm()
    {
        txtShipmentCode.setText("")
        editTextNote.setText("")
    }

    private fun setupUI(view: View) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                view.clearFocus()
                hideSoftKeyboard(this@AddIncidentsActivity)
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
                    txtShipmentCode.setText(result.contents)
                }
            } catch (e: OutOfMemoryError) {
                ToastUtils.error(this, "Không đủ dung lượng để lưu ảnh!")
            } catch (e: Exception) {
                ToastUtils.error(this, e.localizedMessage)
            }
        }
    }
}
