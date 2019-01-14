package dsc.vn.mybbdeliv.View.Maps

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.beust.klaxon.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.android.synthetic.main.activity_maps.*
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.db.INTEGER
import org.jetbrains.anko.uiThread
import dsc.vn.mybbdeliv.Model.MapModel
import dsc.vn.mybbdeliv.R
import dsc.vn.mybbdeliv.Service.LocationTrackingService
import dsc.vn.mybbdeliv.Utils.ToastUtils
import java.net.URL
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback , View.OnClickListener  {

    private var mLocationPermissionGranted = false
    private var LOCATION = 1
    private lateinit var mapModel:MapModel
    private lateinit var mMap: GoogleMap
    private var totalDistanceInMeters: Int = 0
    private var totalDurationInSeconds: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_maps)
        prepareUI()
        bindData()
    }

    private fun prepareUI()
    {
        btCallCustomer.setOnClickListener(this)
    }

    private fun bindData()
    {
        val intent = intent
        val bundle = intent.extras
        mapModel = bundle!!.getSerializable("mapModel") as MapModel
        lbName.text = "Họ & Tên : ${mapModel.name}"

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            if (v.id == btCallCustomer.id) {
                Log.v("CALL", mapModel.phone)
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:" + mapModel.phone)
                if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.error(applicationContext, "Chưa cấp quyền để gọi điện!")

                    return
                }
                startActivity(callIntent)
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val LatLongB = LatLngBounds.Builder()

        mMap.isBuildingsEnabled = true
        mMap.isTrafficEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
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
        val destination = MarkerOptions().position(LatLng(mapModel.lat, mapModel.lng))
        destination.title(mapModel.title)
        destination.snippet(mapModel.snippet)
        destination.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_to_36))
        destination.flat(true)
        mMap.addMarker(destination)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(origin))

        // Declare polyline object and set up color and width
        val options = PolylineOptions()
        options.color(Color.RED)
        options.width(5f)

        // build URL to call API
        val url = getURL(origin, destination.position)

        async {
            // Connect to URL, download content and convert into string asynchronously
            val result = URL(url).readText()
            uiThread {
                // When API call is done, create parser and convert into JsonObject
                val parser = Parser()
                val stringBuilder = StringBuilder(result)
                val json: JsonObject = parser.parse(stringBuilder) as JsonObject
                // get to the correct element in JsonObject
                val routes = json.array<JsonObject>("routes")

                if (routes!!["legs"].toJsonString(true) == "[]") {
                    ToastUtils.warning(applicationContext, "Không tìm thấy địa điểm bạn mún đến !")
                } else {
                    val legs = routes!!["legs"]

                    for (leg in legs) {
                        val data = leg as JsonArray<JsonObject>
                        totalDistanceInMeters += Integer.valueOf(data["distance"]["value"][0].toString())
                        totalDurationInSeconds += Integer.valueOf(data["duration"]["value"][0].toString())
                    }
                    if (totalDistanceInMeters > 1000) {
                        val distanceInKilometers = totalDistanceInMeters / 1000
                        lbDistance.text = "$distanceInKilometers Km"
                    } else {
                        val distanceInMeters = totalDistanceInMeters
                        lbDistance.text = "$distanceInMeters m"
                    }

                    val mins = totalDurationInSeconds / 60
                    lbDuration.text = "$mins phút"

                    val points = routes!!["legs"]["steps"][0] as JsonArray<JsonObject>
                    // For every element in the JsonArray, decode the polyline string and pass all points to a List
                    val polypts = points.flatMap { decodePoly(it.obj("polyline")?.string("points")!!) }
                    // Add  points to polyline and bounds
                    options.add(origin)
                    LatLongB.include(origin)
                    for (point in polypts) {
                        options.add(point)
                        LatLongB.include(point)
                    }
                    options.add(destination.position)
                    LatLongB.include(destination.position)
                    // build bounds
                    val bounds = LatLongB.build()
                    // add polyline to the map
                    mMap.addPolyline(options)
                    // show map with route centered
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                }
            }
        }
    }

    private fun getURL(from : LatLng, to : LatLng) : String {
        val origin = "origin=" + from.latitude + "," + from.longitude
        val dest = "destination=" + to.latitude + "," + to.longitude
        val sensor = "sensor=false"
        val params = "$origin&$dest&$sensor"
        return "https://maps.googleapis.com/maps/api/directions/json?$params"
    }

    /**
     * Method to decode polyline points
     * Courtesy : https://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     */
    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5,
                    lng.toDouble() / 1E5)
            poly.add(p)
        }

        return poly
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
