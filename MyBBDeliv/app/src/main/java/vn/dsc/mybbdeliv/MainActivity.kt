package dsc.vn.mybbdeliv

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.google.firebase.iid.FirebaseInstanceId
import com.google.zxing.integration.android.IntentIntegrator
import dsc.vn.mybbdeliv.Base.BaseActivity
import dsc.vn.mybbdeliv.Camera.ZXingUtils
import dsc.vn.mybbdeliv.Model.Shipment
import dsc.vn.mybbdeliv.Process.ShipmentProcess
import dsc.vn.mybbdeliv.Process.UserProcess
import dsc.vn.mybbdeliv.SQLite.MyDatabaseOpenHelper
import dsc.vn.mybbdeliv.Service.AutoUpdateService
import dsc.vn.mybbdeliv.Service.LocationTrackingService
import dsc.vn.mybbdeliv.Utils.PermissionUtils
import dsc.vn.mybbdeliv.View.Login.LoginActivity
import dsc.vn.mybbdeliv.View.Tracking.ShipmentDetailActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import vn.dsc.mybbdeliv.View.Vietstar.ShipmentDetailVietstarActivity

class MainActivity : BaseActivity(), SearchView.OnQueryTextListener {

    private val Context.database: MyDatabaseOpenHelper
        get() = MyDatabaseOpenHelper.getInstance(applicationContext)

    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)
        prepareUI()
        bindData()
    }

    private fun prepareUI() {
        setupUI(findViewById<View>(clLayout.id))
        fab.setOnClickListener { _ ->
            getBarcodeScan()
        }

        val user = UserProcess().getUser(database)

        if (user == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        else
        {
            //Start AutoUpdateService
            val intentAuto = Intent(this, AutoUpdateService::class.java)
            startService(intentAuto)

            val mServiceIntent = Intent(this, LocationTrackingService::class.java)
            startService(mServiceIntent)
        }
    }

    private fun bindData() {
        PermissionUtils.checkPermission(this)

        //Get the token
        //Use the token only for received a push notification this device
        val token = FirebaseInstanceId.getInstance().token
        if (token != null)
        {
            Log.v("Token ", token)
            UserProcess().updateInstanceIDToken(token)
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        val item = menu.findItem(R.id.action_search)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = MenuItemCompat.getActionView(item) as SearchView
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(componentName))

        searchView.setOnQueryTextListener(this)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        ShipmentProcess().trackingShipment(this, query!!) {
            val shipment = Shipment.Deserializer().deserializeSingle(it)

            if (shipment.fromWard == null)
            {
                        val bundle = Bundle()
        bundle.putSerializable(
                "bill",
                query
        )

        val intent = Intent(this, ShipmentDetailVietstarActivity::class.java)
        intent.putExtras(bundle)
        startActivityForResult(intent, 1)
            }
            else {
                val bundle = Bundle()
                bundle.putSerializable(
                        "Shipments", shipment
                )

                val intent = Intent(this, ShipmentDetailActivity::class.java)
                intent.putExtras(bundle)
                startActivityForResult(intent, 1)
            }
        }
        searchView.clearFocus()
        return false
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_search -> {
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getBarcodeScan() {
        ZXingUtils.initiateScan(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == ZXingUtils.REQUEST_CODE) {
                val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                Log.d("RequestCode", "ZXingUtils.REQUEST_CODE: " + ZXingUtils.REQUEST_CODE + " - " + result!!.contents)
                if (result.contents == null) {

                } else {
                    ShipmentProcess().trackingShipment(this, result.contents) {
                        val shipment = Shipment.Deserializer().deserializeSingle(it)

                        if (shipment.fromWard == null)
                        {
                            val bundle = Bundle()
                            bundle.putSerializable(
                                    "bill",
                                    result.contents
                            )

                            val intent = Intent(this, ShipmentDetailVietstarActivity::class.java)
                            intent.putExtras(bundle)
                            startActivityForResult(intent, 1)
                        }
                        else {
                            val bundle = Bundle()
                            bundle.putSerializable(
                                    "Shipments", shipment
                            )

                            val intent = Intent(this, ShipmentDetailActivity::class.java)
                            intent.putExtras(bundle)
                            startActivityForResult(intent, 1)
                        }
                    }
                }
            }
        }
    }

    private fun setupUI(view: View) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { _, _ ->
                view.clearFocus()
                hideSoftKeyboard(this@MainActivity)
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
}
