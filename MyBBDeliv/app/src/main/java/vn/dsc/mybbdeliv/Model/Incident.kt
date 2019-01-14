package vn.dsc.mybbdeliv.Model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable


class Incident : Serializable {
    var id: Int = 0
    var shipmentId: String = ""

    //User Deserializer
    class Deserializer : ResponseDeserializable<MutableList<Incident>> {
        fun deserializeSingle(content: String) = Gson().fromJson(content, Incident::class.java)!!

        override fun deserialize(content: String): MutableList<Incident>? {
            return Gson().fromJson(
                    content,
                    object : TypeToken<MutableList<Incident>>() {}.type
            )
        }
    }

}