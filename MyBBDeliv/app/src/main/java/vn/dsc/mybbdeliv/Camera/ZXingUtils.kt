package dsc.vn.mybbdeliv.Camera

import android.app.Activity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity

import com.google.zxing.integration.android.IntentIntegrator

/**
 * Created by admin on 2/8/17.
 */

object ZXingUtils {
    val REQUEST_CODE = 0x0000c0de // Only use bottom 16 bits

    fun initiateScan(activity: Activity): IntentIntegrator {
        val intentIntegrator = IntentIntegrator(activity)
        intentIntegrator.setPrompt("Scan a barcode")
        intentIntegrator.setBeepEnabled(true)
        intentIntegrator.setOrientationLocked(false)
        intentIntegrator.initiateScan()
        return intentIntegrator
    }
}
