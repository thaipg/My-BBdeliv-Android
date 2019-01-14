package dsc.vn.mybbdeliv.Utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.location.places.Place
import java.io.IOException
import java.util.*

/**
 * Created by thaiphan on 12/1/17.
 */

object LocationUtils {
    private val TAG = "LocationUtils"
    private var _address: Address? = null

    fun getAddressFromPlace(context: Context, place: Place): Address? {
        val coordinates = place.latLng // Get the coordinates from your place

        val locale = Locale("vi_VN")
        val geocoder = Geocoder(context, locale)

        Log.v(TAG, place.address.toString())

        var addresses: List<Address>? // Only retrieve 1 address
        try {
            addresses = geocoder.getFromLocation(
                    coordinates.latitude,
                    coordinates.longitude,
                    5)

            for (address in addresses!!) {
                for (i in 0 until address.maxAddressLineIndex) {
                    Log.d(TAG, "getAddressLine(" + i + ") - " + address.getAddressLine(i))
                }
                Log.d(TAG, "getAddress - " + address.featureName)
                Log.d(TAG, "getAdminArea - " + address.adminArea)
                Log.d(TAG, "getSubAdminArea - " + address.subAdminArea)
                Log.d(TAG, "getSubLocality - " + address.subLocality)
                Log.d(TAG, "getLocality - " + address.locality)
                Log.d(TAG, "getFeatureName - " + address.featureName)
                Log.d(TAG, "==========================")

                if (!address.adminArea.isNullOrEmpty() && !address.subAdminArea.isNullOrEmpty() &&
                        !address.subLocality.isNullOrEmpty() && !address.locality.isNullOrEmpty() &&
                        !address.featureName.isNullOrEmpty()) {
                    return address
                }
            }

            return addresses[0]
        } catch (e: IOException) {
            ToastUtils.error(context, e.localizedMessage)
            Log.e(TAG, "IOException", e)
        }
        return null
    }
}