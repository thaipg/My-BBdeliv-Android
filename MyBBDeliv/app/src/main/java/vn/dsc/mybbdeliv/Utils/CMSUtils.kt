package dsc.vn.mybbdeliv.Utils

import android.app.Activity
import android.content.pm.PackageInfo
import android.support.design.widget.NavigationView
import android.view.View
import android.widget.TextView
import dsc.vn.mybbdeliv.R
import java.text.DecimalFormat

/**
 * Created by thaiphan on 11/27/17.
 */
class CMSUtils {

    fun decimalFormat(value: Double, format: String): String {
        val formatter = DecimalFormat(format)
        return formatter.format(value)
    }
    /**
     * This method returns true if the input object is integer.
     *
     * @param value
     * @return true | false
     */
    fun isInteger(value: Any): Boolean {
        try {
            Integer.parseInt(value.toString())
            return true
        } catch (e: Exception) {
        }

        return false
    }

    /**
     * This method returns true if the input object is float.
     *
     * @param value
     * @return true | false
     */
    fun isFloat(value: Any): Boolean {
        try {
            java.lang.Float.parseFloat(value.toString())
            return true
        } catch (e: Exception) {
        }

        return false
    }

    /**
     * This method returns true if the input object is double.
     *
     * @param value
     * @return true | false
     */
    fun isDouble(value: Any): Boolean {
        try {
            java.lang.Double.parseDouble(value.toString())
            return true
        } catch (e: Exception) {
        }

        return false
    }

    /**
     * This method returns float value if the input object is not null and return zero when the input object is null.
     *
     * @param value
     * @return true: float value | false: 0
     */
    fun getSafeFloat(value: Any): Float {
        return if (isFloat(value)) {
            java.lang.Float.parseFloat(value.toString())
        } else 0f
    }

    /**
     * This method returns double value if the input object is not null and return zero when the input object is null.
     *
     * @param value
     * @return true: double value | false: 0
     */
    fun getSafeDouble(value: Any): Double {
        return if (isDouble(value)) {
            java.lang.Double.parseDouble(value.toString())
        } else 0.0
    }

    fun showAppVersion(activity: Activity, navigationView: NavigationView) = try {
        val manager = activity.packageManager
        val info: PackageInfo
        info = manager.getPackageInfo(
                activity.packageName, 0)
        val version = info.versionName

        val headerLayout = navigationView.getHeaderView(0)
        val txtVersion = headerLayout.findViewById<TextView>(R.id.textView_version)
        txtVersion.visibility = View.VISIBLE
        txtVersion.text = "Phiên bản hiện tại: " + version
    } catch (e: Exception) {
        e.printStackTrace()
    }
}