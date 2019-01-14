package dsc.vn.mybbdeliv.Model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

class Report : Serializable {
    var totalDeliveyCompleteOfCurrentDay: Int = 0
    var totalDeliveyCompleteOfCurrentMonth: Int = 0
    var totalReturnCompleteOfCurrentDay: Int = 0
    var totalReturnCompleteOfCurrentMonth: Int = 0
    var totalPickupCompleteOfCurrentDay: Int = 0
    var totalPickupCompleteOfCurrentMonth: Int = 0

    //User Deserializer
    class Deserializer : ResponseDeserializable<MutableList<Report>> {
        fun deserializeSingle(content: String) = Gson().fromJson(content, Report::class.java)!!

        override fun deserialize(content: String): MutableList<Report>? {
            return Gson().fromJson(
                    content,
                    object : TypeToken<MutableList<Report>>() {}.type
            )
        }
    }
}