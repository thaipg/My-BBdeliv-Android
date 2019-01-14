package dsc.vn.mybbdeliv.Service

import android.content.Context
import android.content.Intent
import android.support.v4.content.WakefulBroadcastReceiver
import android.util.Log
import me.leolin.shortcutbadger.ShortcutBadger
import dsc.vn.mybbdeliv.Helper.NotificationHelper
import dsc.vn.mybbdeliv.Utils.*

/**
 * This is called whenever app receives notification
 * in background/foreground state so you can
 * apply logic for background task, but still Firebase notification
 * will be shown in notification tray
 */
class FirebaseDataReceiver : WakefulBroadcastReceiver() {

    private val TAG = "FirebaseDataReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.extras != null) {
            for (key in intent.extras!!.keySet()) {
                val value = intent.extras!!.get(key)

                if(key.equals(NotificationHelper.key_badge)) {
                    //Store badge
                    BadgeUtils.addBadge(context, BadgeInput(context, Integer.valueOf(value.toString()), BadgeType.ALL))
                }

                Log.e("FirebaseDataReceiver", "Key: $key Value: $value")
            }
        }
    }
}