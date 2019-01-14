package dsc.vn.mybbdeliv.Utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import dsc.vn.mybbdeliv.Service.LocationTrackingService
import java.util.ArrayList


object PermissionUtils {
    fun checkPermission(activity: Activity) {
        TedPermission(activity.baseContext)
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {

                    }

                    override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                        activity.finish()
                    }
                })
                .setPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CALL_PHONE
                )
                .check()
    }
}