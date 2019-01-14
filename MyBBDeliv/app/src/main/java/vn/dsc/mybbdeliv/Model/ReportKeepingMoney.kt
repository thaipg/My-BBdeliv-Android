package dsc.vn.mybbdeliv.Model


import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

class ReportKeepingMoney : Serializable {
    var id: Int = 0
    var userId: String = ""
    var totalShipmentKeeping: Int = 0
    var pickupComplete: Int = 0
    var transferring: Int = 0
    var delivering: Int = 0
    var deliveryFail: Int = 0
    var returnFail: Int = 0
    var returning: Int = 0
    var transferReturning: Int = 0
    var totalCODKeeping: Int = 0
    var totalPriceKeeping: Int = 0
    //User Deserializer
    class Deserializer : ResponseDeserializable<MutableList<ReportKeepingMoney>> {
        fun deserializeSingle(content: String) = Gson().fromJson(content, ReportKeepingMoney::class.java)!!

        override fun deserialize(content: String): MutableList<ReportKeepingMoney>? {
            return Gson().fromJson(
                    content,
                    object : TypeToken<MutableList<ReportKeepingMoney>>() {}.type
            )
        }
    }
}