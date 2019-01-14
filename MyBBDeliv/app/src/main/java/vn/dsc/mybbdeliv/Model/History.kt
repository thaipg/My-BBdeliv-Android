package dsc.vn.mybbdeliv.Model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

/**
 * Created by NhatAnh on 11/16/2017.
 */
class History : Serializable {
    var shipmentId: Int? = null
    var shipmentStatusId: Int? = null
    var location: String? = null
    var note: String? = null
    var shipment: Shipment? = null
    var shipmentStatus: ShipmentStatus? = null
    var createdWhen: String? = null

    //User Deserializer
    class Deserializer : ResponseDeserializable<MutableList<History>> {
        fun deserializeSingle(content: String) = Gson().fromJson(content, History::class.java)!!

        override fun deserialize(content: String): MutableList<History>? {
            return Gson().fromJson(
                    content,
                    object : TypeToken<MutableList<History>>() {}.type
            )
        }
    }
}