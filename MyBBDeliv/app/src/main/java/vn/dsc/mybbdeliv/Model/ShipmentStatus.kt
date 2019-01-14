package dsc.vn.mybbdeliv.Model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

/**
 * Created by NhatAnh on 11/16/2017.
 */
data class ShipmentStatus(   var id: Int,
                             var concurrencyStamp: String?,
                             var isEnabled: Boolean?,
                             var name: String?) {

    //User Deserializer
    class Deserializer : ResponseDeserializable<ShipmentStatus> {
        override fun deserialize(content: String) = Gson().fromJson(content, ShipmentStatus::class.java)
    }

}