package dsc.vn.mybbdeliv.Extension

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import dsc.vn.mybbdeliv.Utils.ToastUtils
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by thaiphan on 12/2/17.
 */
fun EditText.isNotEmptyTextField(context: Context, errorMessage: CharSequence): Boolean
{
    return if (!this.text.isNullOrEmpty()) {
        this.error = null
        false
    } else {
        ToastUtils.warning(context, errorMessage.toString())
        this.error = errorMessage
        true
    }
}

fun TextView.isNotEmptyTextField(context: Context, errorMessage: CharSequence): Boolean
{
    return if (!this.text.isNullOrEmpty()) {
        this.error = null
        false
    } else {
        ToastUtils.warning(context,  errorMessage.toString())
        this.error = errorMessage
        true
    }
}

fun Button.isNotEmptySelector(context: Context, errorMessage: CharSequence, data: Any?): Boolean
{
    return if (data != null) {
        this.error = null
        false
    } else {
        ToastUtils.warning(context,  errorMessage.toString())
        this.error = errorMessage
        true
    }
}

fun String?.toDateTime(): String
{
    if (this == null)
        return ""
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val date = format.parse(this)
    val formatNew = SimpleDateFormat("dd/MM/yyyy HH:mm")
    return formatNew.format(date)
}