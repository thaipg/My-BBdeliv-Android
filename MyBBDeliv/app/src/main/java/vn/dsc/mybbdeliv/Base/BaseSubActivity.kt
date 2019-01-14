package dsc.vn.mybbdeliv.Base

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import dsc.vn.mybbdeliv.Service.NetworkChangeReceiver

/**
 * Created by thaiphan on 11/28/17.
 */

abstract class BaseSubActivity : AppCompatActivity() {

    //Network check
    private lateinit var receiver: NetworkChangeReceiver
    private lateinit var intentNetwork: IntentFilter
    protected abstract val layoutResourceId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)

        intentNetwork = IntentFilter()
        intentNetwork.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        receiver = NetworkChangeReceiver()
    }

    public override fun onResume() {
        super.onResume()
        registerReceiver(receiver, intentNetwork)
    }

    public override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }

    override fun onBackPressed() {
        finish()
    }
}