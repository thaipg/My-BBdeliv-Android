package dsc.vn.mybbdeliv.Base

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.view.MenuItemCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.github.javiersantos.appupdater.AppUpdater
import com.github.javiersantos.appupdater.enums.UpdateFrom
import dsc.vn.mybbdeliv.MainActivity
import dsc.vn.mybbdeliv.Model.MenuBadge
import dsc.vn.mybbdeliv.Model.UserDetail
import dsc.vn.mybbdeliv.Process.GeneralProcess
import dsc.vn.mybbdeliv.Process.UserProcess
import dsc.vn.mybbdeliv.R
import dsc.vn.mybbdeliv.SQLite.MyDatabaseOpenHelper
import dsc.vn.mybbdeliv.Service.JsonWebservice
import dsc.vn.mybbdeliv.Service.NetworkChangeReceiver
import dsc.vn.mybbdeliv.Utils.BadgeUtils
import dsc.vn.mybbdeliv.Utils.CMSUtils
import dsc.vn.mybbdeliv.View.Delivery.DeliveryViewActivity
import dsc.vn.mybbdeliv.View.Delivery.Vietstar.DeliveryVietstarActivity
import dsc.vn.mybbdeliv.View.Information.InformationActivity
import dsc.vn.mybbdeliv.View.Login.LoginActivity
import dsc.vn.mybbdeliv.View.ReadyToTransit.ListCreatedActivity
import dsc.vn.mybbdeliv.View.ReadyToTransit.ReadyToTransitActivity
import dsc.vn.mybbdeliv.View.Receive.ReceivedActivity
import dsc.vn.mybbdeliv.View.Report.ReportActivity
import dsc.vn.mybbdeliv.View.Report.ReportKeepingMoneyActivity
import dsc.vn.mybbdeliv.View.RequestReadyToTransit.RequestReadyToTransitActivity
import dsc.vn.mybbdeliv.View.WaitingDelivery.WaitingDeliveryActivity
import dsc.vn.mybbdeliv.View.WaitingReceive.WaitingReceiveActivity
import kotlinx.android.synthetic.main.activity_main.*
import vn.dsc.mybbdeliv.View.Problem.AddDelayActivity
import vn.dsc.mybbdeliv.View.Problem.AddIncidentsActivity
import vn.dsc.mybbdeliv.View.Vietstar.CheckExtraPriceActivity
import vn.dsc.mybbdeliv.View.Warehouse.InputWarehouseActivity
import vn.dsc.mybbdeliv.View.Warehouse.OutputWarehouseDeliveryActivity
import vn.dsc.mybbdeliv.View.Warehouse.OutputWarehouseTransferActivity


/**
* Created by ThaiPhan on 11/22/2017.
*/
open class BaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    protected val badgeContextList: MutableList<Context> = ArrayList()

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private val Context.database: MyDatabaseOpenHelper
        get() = MyDatabaseOpenHelper.getInstance(applicationContext)

    //Network check
    private lateinit var receiver:NetworkChangeReceiver
    private lateinit var intentNetwork:IntentFilter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)

        navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        CMSUtils().showAppVersion(this, navigationView)

        val menuNav = navigationView.menu
        val itemUserName = menuNav.findItem(R.id.nav_information)
        itemUserName.title = UserProcess().getUser(database)?.userFullName

        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu)
        initializeCountDrawer()

        //Init list context (application context and children context)
        badgeContextList.add(this.applicationContext)

        intentNetwork = IntentFilter()
        intentNetwork.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        receiver = NetworkChangeReceiver()
    }

    override fun onResume() {
        super.onResume()

        val appUpdater = AppUpdater(this)
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON("http://dsc.vn/vietstar_app/systems/update-changelog.json")
                .setTitleOnUpdateAvailable("Đã có phiên bản mới")
                .setContentOnUpdateAvailable("Vui lòng cài đặt trước khi tiếp tục làm việc !")
                .setTitleOnUpdateNotAvailable("Chưa có phiên bản mới")
                .setContentOnUpdateNotAvailable("Hiện tại chưa có phiên bản mới. Vui lòng cập nhật!")
                .setButtonUpdate("Cập nhật")
                .setButtonDismiss("Bỏ qua")
                .setButtonDoNotShowAgain("")
                .setIcon(R.drawable.ic_logo) // Notification icon
                .setCancelable(false) // Dialog could not be
        appUpdater.start()

        //Clear all badge
        BadgeUtils.clearBadgeContext(badgeContextList)
        BadgeUtils.resetBadge()

        val menuNav = navigationView.menu
        loadPermission(menuNav)

        registerReceiver(receiver, intentNetwork)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                initializeCountDrawer()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun loadPermission(menuNav: Menu) {
        val nav_danggiaohang_vietstar = menuNav.findItem(R.id.nav_danggiaohang_vietstar)
        val nav_kiemtraphuphi = menuNav.findItem(R.id.nav_kiemtraphuphi)
        val nav_list_created = menuNav.findItem(R.id.nav_list_created)
        val nav_search = menuNav.findItem(R.id.nav_search)
        val nav_requestreadytotransit = menuNav.findItem(R.id.nav_requestreadytotransit)
        val nav_readytotransit = menuNav.findItem(R.id.nav_readytotransit)
        val nav_hangchochungchuyen = menuNav.findItem(R.id.nav_hangchochungchuyen)
        val nav_dangtrungchuyen = menuNav.findItem(R.id.nav_dangtrungchuyen)
        val nav_chogiaohang = menuNav.findItem(R.id.nav_chogiaohang)
        val nav_danggiaohang = menuNav.findItem(R.id.nav_danggiaohang)
        val nav_input_warehouse = menuNav.findItem(R.id.nav_input_warehouse)
        val nav_output_warehouse_transfer = menuNav.findItem(R.id.nav_output_warehouse_transfer)
        val nav_output_warehouse_delivery = menuNav.findItem(R.id.nav_output_warehouse_delivery)
        val nav_baocao_giutien = menuNav.findItem(R.id.nav_baocao_giutien)
        val nav_baocao = menuNav.findItem(R.id.nav_baocao)

        nav_search.isVisible = false
        nav_list_created.isVisible = false
        nav_requestreadytotransit.isVisible = false
        nav_readytotransit.isVisible = false

        nav_hangchochungchuyen.isVisible = false
        nav_dangtrungchuyen.isVisible = false
        nav_chogiaohang.isVisible = false
        nav_danggiaohang.isVisible = false
        nav_baocao_giutien.isVisible = false
        nav_baocao.isVisible = false

        nav_input_warehouse.isVisible = false
        nav_output_warehouse_transfer.isVisible = false
        nav_output_warehouse_delivery.isVisible = false

        nav_danggiaohang_vietstar.isVisible = false
        nav_kiemtraphuphi.isVisible = false

        val user = UserProcess().getUser(database)
        if (user != null) {
            UserProcess().getUserAccount(this) {
                val userDetail = UserDetail.Deserializer().deserialize(it)
                if (userDetail.roleIds != null) {
                    for (r in userDetail.roles!!) {
                        if (r.code == "PICKUP") {
                            nav_search.isVisible = true
                            nav_list_created.isVisible = true
                            nav_requestreadytotransit.isVisible = true
                            nav_readytotransit.isVisible = true
                            nav_hangchochungchuyen.isVisible = true
                            nav_dangtrungchuyen.isVisible = true
                            if (JsonWebservice().post_api_endPoint == "http://postapi.flashship.vn/api" || JsonWebservice().post_api_endPoint == "http://postapi.bbdeliv.vn/api")
                            {
                                nav_kiemtraphuphi.isVisible = true
                            }
                        }

                        if (r.code == "DELIVERY") {
                            nav_hangchochungchuyen.isVisible = true
                            nav_dangtrungchuyen.isVisible = true
                            nav_chogiaohang.isVisible = true
                            nav_danggiaohang.isVisible = true
                            nav_baocao_giutien.isVisible = true
                            nav_baocao.isVisible = true
                            if (JsonWebservice().post_api_endPoint == "http://postapi.flashship.vn/api" || JsonWebservice().post_api_endPoint == "http://postapi.bbdeliv.vn/api")
                            {
                                nav_danggiaohang_vietstar.isVisible = true
                                nav_kiemtraphuphi.isVisible = true
                            }
                        }

                        if (r.code == "WAREHOUSE") {
                            nav_input_warehouse.isVisible = true
                            nav_output_warehouse_transfer.isVisible = true
                            nav_output_warehouse_delivery.isVisible = true
                        }
                    }
                }
            }
        }
    }

    private fun initializeCountDrawer() {
        GeneralProcess().getShipmentReportCurrentEmp {
            val menu = MenuBadge.Deserializer().deserialize(it)
            setMenuBadgeItem(R.id.nav_requestreadytotransit,menu.assignEmployeePickup)
            setMenuBadgeItem(R.id.nav_readytotransit,menu.picking)
            setMenuBadgeItem(R.id.nav_hangchochungchuyen,(menu.assignEmployeeTransfer + menu.assignEmployeeTransferReturn))
            setMenuBadgeItem(R.id.nav_dangtrungchuyen,(menu.transferring + menu.transferReturning))
            setMenuBadgeItem(R.id.nav_chogiaohang,(menu.assignEmployeeDelivery + menu.assignEmployeeReturn))
            setMenuBadgeItem(R.id.nav_danggiaohang,(menu.delivering + menu.returning))
        }
    }

    private fun setMenuBadgeItem(id_nav:Int,number_menu: Int) {
        val nav_menu = MenuItemCompat.getActionView(navigationView.menu.findItem(id_nav)) as TextView
        //Gravity property aligns the text
        nav_menu.gravity = Gravity.CENTER_VERTICAL
        nav_menu.setTypeface(null, Typeface.BOLD)
        nav_menu.setTextColor(resources.getColor(R.color.colorWhite))
        nav_menu.text = number_menu.toString() + "    "
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_search -> {
                // Handle the camera action
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_list_created -> {
                val intent = Intent(this, ListCreatedActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_requestreadytotransit -> {
                val intent = Intent(this, RequestReadyToTransitActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_readytotransit -> {
                val intent = Intent(this, ReadyToTransitActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_hangchochungchuyen -> {
                val intent = Intent(this, WaitingReceiveActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_dangtrungchuyen -> {
                val intent = Intent(this, ReceivedActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_chogiaohang -> {
                val intent = Intent(this, WaitingDeliveryActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_danggiaohang -> {
                val intent = Intent(this, DeliveryViewActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_danggiaohang_vietstar -> {
                val intent = Intent(this, DeliveryVietstarActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_baocao_giutien -> {
                val intent = Intent(this, ReportKeepingMoneyActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_add_delay -> {
                val intent = Intent(this, AddDelayActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_add_incidents -> {
                val intent = Intent(this, AddIncidentsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_baocao -> {
                val intent = Intent(this, ReportActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_kiemtraphuphi -> {
                val intent = Intent(this, CheckExtraPriceActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_input_warehouse -> {
                val intent = Intent(this, InputWarehouseActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_output_warehouse_transfer -> {
                val intent = Intent(this, OutputWarehouseTransferActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_output_warehouse_delivery -> {
                val intent = Intent(this, OutputWarehouseDeliveryActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_information ->
            {
                val intent = Intent(this, InformationActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_signout -> {
                UserProcess().removeAll(database)
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}