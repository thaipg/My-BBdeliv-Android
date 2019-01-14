package vn.dsc.mybbdeliv.Model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

/**
 * Created by NhatAnh on 11/16/2017.
 */
class Truck: Serializable {
    var id: Int = 0
    var code: String = ""
    var name: String = ""
    var truckNumber: String = ""
    var truckOwnershipName: String = ""
    var truckOwnershipId: Int = 0
    var truckTypeName: String = ""

    class Deserializer : ResponseDeserializable<MutableList<Truck>> {
        fun deserializeSingle(content: String) = Gson().fromJson(content, Truck::class.java)!!

        override fun deserialize(content: String): MutableList<Truck>? {
            return Gson().fromJson(
                    content,
                    object : TypeToken<MutableList<Truck>>() {}.type
            )
        }
    }
}