package dsc.vn.mybbdeliv.View.Information

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import com.sandrios.sandriosCamera.internal.SandriosCamera
import com.sandrios.sandriosCamera.internal.configuration.CameraConfiguration
import kotlinx.android.synthetic.main.content_information.*
import kotlinx.android.synthetic.main.popup_change_password.*
import org.json.JSONObject
import dsc.vn.mybbdeliv.Base.BaseActivity
import dsc.vn.mybbdeliv.Camera.SandriosCameraUtils
import dsc.vn.mybbdeliv.Model.UserDetail
import dsc.vn.mybbdeliv.Process.UserProcess
import dsc.vn.mybbdeliv.R
import dsc.vn.mybbdeliv.SQLite.MyDatabaseOpenHelper
import dsc.vn.mybbdeliv.Utils.ToastUtils
import java.io.ByteArrayOutputStream
import java.io.File


class InformationActivity : BaseActivity() , View.OnClickListener {

    private var bitmapSign: Bitmap? = null
    var userDetail: UserDetail? = null
    private val Context.database: MyDatabaseOpenHelper
        get() = MyDatabaseOpenHelper.getInstance(applicationContext)

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_information)
        super.onCreate(savedInstanceState)

        prepareUI()
        bindData()
    }

    private fun prepareUI() {
        setupUI(findViewById<View>(clLayout.id))
        btChangePass.setOnClickListener(this)

    }

    private fun bindData() {
        UserProcess().getUserAccount(this, {
            userDetail = UserDetail.Deserializer().deserialize(it)
            lbUserName.text = userDetail?.userName
            txtFullName.setText(userDetail?.fullName)
            txtPhone.setText(userDetail?.phoneNumber)
            txtEmail.setText(userDetail?.email)
            if (userDetail!!.avatarPath != null)
            {
                // Prepare the popup assets
                UserProcess().downloadImage(this, userDetail!!.avatarPath!!, {
                    val jsonObj = JSONObject(it)
                    if (!jsonObj["fileBase64String"].toString().isEmpty()) {
                        val decodedString = Base64.decode(jsonObj["fileBase64String"].toString(), Base64.DEFAULT)
                        val mbitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                        loadImage(mbitmap)
                    }
                })
            }
        })
    }

    private fun loadImage(mbitmap: Bitmap) {

        val imageRounded = Bitmap.createBitmap(480, 480, mbitmap.config)
        val canvas = Canvas(imageRounded)
        val mpaint = Paint()
        mpaint.isAntiAlias = true
        mpaint.shader = BitmapShader(mbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        canvas.drawRoundRect(RectF(0f, 0f, 480F, 480F),  480F,  480F, mpaint)// Round Image Corner 100 100 100 100
        Log.v("Width", mbitmap.width.toFloat().toString())
        // Set up the input
        imagePicked.scaleType = ImageView.ScaleType.FIT_CENTER
        bitmapSign = imageRounded
        imagePicked.setImageBitmap(imageRounded)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_information, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_camera -> {
                SandriosCamera(this, SandriosCameraUtils().CAPTURE_MEDIA)
                        .setShowPicker(true)
                        .setMediaAction(CameraConfiguration.MEDIA_ACTION_PHOTO)
                        .launchCamera()
                return true
            }
            R.id.action_save -> {
                saveData()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveData()
    {
        userDetail?.fullName = txtFullName.text.toString()
        userDetail?.phoneNumber = txtPhone.text.toString()
        userDetail?.email = txtEmail.text.toString()

        val bitmap = Bitmap.createScaledBitmap(bitmapSign, 480, 480, true)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val imgString = Base64.encodeToString(byteArray, Base64.NO_WRAP)

        val uploadData = JSONObject()
        uploadData.put("Id", userDetail!!.id)
        uploadData.put("FileName", userDetail!!.userName + ".jpg")
        uploadData.put("FileExtension", "jpg")
        uploadData.put("FileBase64String", imgString)

        UserProcess().update(this , userDetail!!, {
            UserProcess().uploadAvatar(this, uploadData, {
                Log.v("Upload","Đã upload hình")
                bindData()
            })
            ToastUtils.success(this,"Cập nhật thành công!")
        })
    }


    override fun onClick(v: View?) {
        if (v != null) {
            if (v.id == btChangePass.id) {
                val dialog = BottomSheetDialog(this@InformationActivity)
                dialog.setContentView(R.layout.popup_change_password)
                dialog.btChangePassSubmit.setOnClickListener {
                    if (dialog.txtOldPassword.text.isEmpty())
                    {
                        ToastUtils.error(this, "Vui lòng nhập mật khẩu cũ !")
                        return@setOnClickListener
                    }
                    if (dialog.txtNewPassword.text.isEmpty())
                    {
                        ToastUtils.error(this, "Vui lòng nhập mật khẩu mới !")
                        return@setOnClickListener
                    }
                    if (dialog.txtNewPassword.text.toString() != dialog.txtConfirmNewPassword.text.toString())
                    {
                        ToastUtils.error(this, "Mật khẩu xác nhận không trùng với mật khẩu mới !")
                        return@setOnClickListener
                    }
                    val user = UserProcess().getUser(database)

                    val requestParam = JSONObject()
                    requestParam.put("UserId", user!!.userId)
                    requestParam.put("CurrentPassWord", dialog.txtOldPassword.text)
                    requestParam.put("NewPassWord", dialog.txtNewPassword.text)


                    UserProcess().changePassword(this, requestParam , {
                        ToastUtils.success(this, "Bạn đã thay đổi mật khẩu thành công")
                        bindData()
                    })
                    dialog.dismiss()
                }
                dialog.show()
            }
        }
    }

    private fun setupUI(view: View) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                view.clearFocus()
                hideSoftKeyboard(this@InformationActivity)
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
                if (requestCode == SandriosCameraUtils().CAPTURE_MEDIA) {
                    val filePath = data!!.getStringExtra(CameraConfiguration.Arguments.FILE_PATH)
                    val imgFile = File(filePath)
                    if (imgFile.exists()) {
                        bitmapSign = BitmapFactory.decodeFile(imgFile.absolutePath)
                        loadImage(bitmapSign!!)
                        imgFile.delete()
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
