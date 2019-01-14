package dsc.vn.mybbdeliv.Service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dsc.vn.mybbdeliv.Process.UserProcess
import dsc.vn.mybbdeliv.Utils.ToastUtils

class LocationTrackingService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    companion object {
         var lastLocation: Location = Location(LocationManager.GPS_PROVIDER)
    }
    override fun onBind(intent: Intent?) = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
                UserProcess().updateLocation()
                Log.v("Location Update", lastLocation!!.latitude.toString() + "&" + lastLocation!!.longitude.toString())
            }
        }
    }
}