package dsc.vn.mybbdeliv.Service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import dsc.vn.mybbdeliv.Process.UserProcess
import java.util.*

/**
 * Created by thaiphan on 2/7/18.
 */

class AutoUpdateService : Service() {

    // run on another Thread to avoid crash
    private val mHandler = Handler()
    // timer handling
    private var mTimer: Timer? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        // cancel if already existed
        if (mTimer != null) {
            mTimer!!.cancel()
        } else {
            // recreate new
            mTimer = Timer()
        }
        // schedule task
        mTimer!!.scheduleAtFixedRate(TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL)
    }

    internal inner class TimeDisplayTimerTask : TimerTask() {

        override fun run() {
            // run on another thread
            mHandler.post {
                    UserProcess().updateLocation()
            }
        }
    }

    companion object {
        val NOTIFY_INTERVAL = (10 * 1000).toLong() // 10 seconds
    }
}
