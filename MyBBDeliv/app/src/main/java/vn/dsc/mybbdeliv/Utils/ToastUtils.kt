package dsc.vn.mybbdeliv.Utils

import android.content.Context
import android.widget.Toast
import es.dmoral.toasty.Toasty


/**
 * Created by thaiphan on 11/27/17.
 */

object ToastUtils {
    fun success(context: Context, message: String) {
        Toasty.success(context, message, Toast.LENGTH_SHORT, true).show()
    }

    fun error(context: Context, errorMessage: String) {
        Toasty.error(context, errorMessage, Toast.LENGTH_SHORT, true).show()
    }

    fun info(context: Context, message: String) {
        Toasty.info(context, message, Toast.LENGTH_SHORT, true).show()
    }

    fun warning(context: Context, message: String) {
        Toasty.warning(context, message, Toast.LENGTH_SHORT, true).show()
    }
}
