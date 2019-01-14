package dsc.vn.mybbdeliv.Model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import java.io.Serializable

/**
 * Created by thaiphan on 1/22/18.
 */
class MenuBadge : Serializable {
    var assignEmployeePickup: Int = 0
    var picking: Int = 0
    var assignEmployeeTransfer: Int = 0
    var transferring: Int = 0
    var assignEmployeeTransferReturn: Int = 0
    var transferReturning: Int = 0
    var assignEmployeeDelivery: Int = 0
    var delivering: Int = 0
    var assignEmployeeReturn: Int = 0
    var returning: Int = 0
    var pickupComplete: Int = 0
    var deliveryComplete: Int = 0
    var returnComplete: Int = 0

    //User Deserializer
    class Deserializer : ResponseDeserializable<MenuBadge> {
        override fun deserialize(content: String) = Gson().fromJson(content, MenuBadge::class.java)!!
    }
}