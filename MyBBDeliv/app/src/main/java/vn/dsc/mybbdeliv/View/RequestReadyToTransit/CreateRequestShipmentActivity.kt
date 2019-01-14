package dsc.vn.mybbdeliv.View.RequestReadyToTransit

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.beust.klaxon.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_create_request_shipment.*
import kotlinx.android.synthetic.main.activity_maps.*
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.uiThread
import dsc.vn.mybbdeliv.Model.MapModel
import dsc.vn.mybbdeliv.R
import dsc.vn.mybbdeliv.Service.LocationTrackingService
import dsc.vn.mybbdeliv.Utils.ToastUtils
import java.net.URL
import java.util.*

class CreateRequestShipmentActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {

    private var mLocationPermissionGranted = false
    private var LOCATION = 1
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_request_shipment)
        prepareUI()
        bindData()
    }

    private fun prepareUI()
    {

    }

    private fun bindData()
    {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val LatLongB = LatLngBounds.Builder()

        mMap.isBuildingsEnabled = true
        mMap.isTrafficEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.uiSettings.isScrollGesturesEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.uiSettings.isTiltGesturesEnabled = true
        mMap.uiSettings.isRotateGesturesEnabled = true
        getLocationPermission()
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        // Add a marker in Sydney and move the camera
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(
                LocationTrackingService.lastLocation.latitude,
                LocationTrackingService.lastLocation.longitude,
                1)

        if (addresses.count() == 0) {
            ToastUtils.error(this,"Định vị của bạn không hoạt động, không thể cập nhật thông tin")
            return
        }

        val origin = LatLng(LocationTrackingService.lastLocation.latitude, LocationTrackingService.lastLocation.longitude)
        val originMaker = MarkerOptions().position(LatLng(origin.latitude, origin.longitude))
        originMaker.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_from_36))
        originMaker.flat(true)
        mMap.addMarker(originMaker)
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(origin,15.0F)))

        // Declare polyline object and set up color and width
        val options = PolylineOptions()
        options.color(Color.RED)
        options.width(5f)

    }


    override fun onClick(v: View?) {
        if (v != null) {

        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        mLocationPermissionGranted = false
        when (requestCode) {
            LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }

    private fun getLocationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),1)
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ToastUtils.error(applicationContext, "Chưa cấp quyền để sử dụng vị trí!")
            return
        }
        else
        {
            mLocationPermissionGranted = true
        }
    }

    private fun updateLocationUI() {
        try {
            if (mLocationPermissionGranted) {
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = true
            } else {
                mMap.isMyLocationEnabled = false
                mMap.uiSettings.isMyLocationButtonEnabled = false
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }

    }
}
