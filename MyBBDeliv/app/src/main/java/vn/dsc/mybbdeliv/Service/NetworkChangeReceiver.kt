package dsc.vn.mybbdeliv.Service

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import vn.dsc.mybbdeliv.Utils.NetworkUtil


class NetworkChangeReceiver : BroadcastReceiver() {
    private var alertDialog:AlertDialog? = null
    override fun onReceive(context: Context, intent: Intent) {
        val status = NetworkUtil.getConnectivityStatusString(context)
        Log.d("Network", "Receiver network")
        if ("android.net.conn.CONNECTIVITY_CHANGE" == intent.action) {
            if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                if (alertDialog == null) {
                    alertDialog = AlertDialog.Builder(context).create()
                    alertDialog!!.setTitle("Cảnh báo")
                    alertDialog!!.setMessage("Thiết bị không có kết nối mạng, vui lòng kiểm tra mạng và thử lại")
                    alertDialog!!.setIcon(android.R.drawable.ic_dialog_alert)
                    alertDialog!!.setCancelable(false)
                    alertDialog!!.show()
                }
                Log.d("Network", "no network")
                //ForceExitPause(context).execute()
            }
            else if (status == NetworkUtil.NETWORK_STAUS_WIFI)
            {

            }
            else {
                if (alertDialog != null)
                {
                    alertDialog!!.setCancelable(true)
                    alertDialog!!.dismiss()
                    alertDialog = null
                }
                Log.d("Network", "connect network")
                //ResumeForceExitPause(context).execute()
            }
        }
    }
}