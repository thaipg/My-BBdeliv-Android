package dsc.vn.mybbdeliv.Service

/**
 * Created by admin on 2/9/17.
 */

import dsc.vn.mybbdeliv.Utils.CMSUtils
import java.lang.*

object DistanceService {
    val UNIT_MILES = "M"
    val UNIT_KILOMETERS = "K"
    val UNIT_NAUTICAL_MILES = "N"
    //    public static void main (String[] args) throws java.lang.Exception
    //    {
    //        System.out.println(distance(32.9697, -96.80322, 29.46786, -98.53506, "M") + " Miles\n");
    //        System.out.println(distance(32.9697, -96.80322, 29.46786, -98.53506, "K") + " Kilometers\n");
    //        System.out.println(distance(32.9697, -96.80322, 29.46786, -98.53506, "N") + " Nautical Miles\n");
    //    }

    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double, unit: String): Double {
        val theta = lon1 - lon2
        var dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist *= 60.0 * 1.1515
        if (unit === UNIT_KILOMETERS) {
            dist *= 1.609344
        } else if (unit === UNIT_NAUTICAL_MILES) {
            dist *= 0.8684
        }

        return dist
    }

    fun distanceString(lat1: Double, lon1: Double, lat2: Double, lon2: Double, unit: String): String {
        val theta = lon1 - lon2
        var dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist *= 60.0 * 1.1515
        if (unit === UNIT_KILOMETERS) {
            dist *= 1.609344
        } else if (unit === UNIT_NAUTICAL_MILES) {
            dist *= 0.8684
        }
        return CMSUtils().decimalFormat(dist, "#,###,##0.##")
    }

    fun getDistanceDefaultString(lat1: Double, lon1: Double, lat2: String?, lon2: String?): String {
        if (lat1 == 0.0 && lon1 == 0.0) {
            return "chưa bật định vị"
        }
        return if (lat2 == null || lon2 == null) {
            "0 km"
        } else {
            String.format("%s km",
                    distanceString(
                            lat1,
                            lon1,
                            CMSUtils().getSafeDouble(lat2),
                            CMSUtils().getSafeDouble(lon2),
                            UNIT_KILOMETERS
                    )
            )
        }
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts decimal degrees to radians						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts radians to decimal degrees						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private fun rad2deg(rad: Double): Double {
        return rad * 180 / Math.PI
    }
}