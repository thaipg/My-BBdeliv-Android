package dsc.vn.mybbdeliv.Model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

/**
 * Created by NhatAnh on 11/16/2017.
 */
class Structure : Serializable {
    var id: Int = 0
    var concurrencyStamp: String? = null
    var isEnabled: Boolean = false
    var code: String = ""
    var name: String = ""

    //User Deserializer
    class Deserializer : ResponseDeserializable<MutableList<Structure>> {
        fun deserializeSingle(content: String) = Gson().fromJson(content, Structure::class.java)!!

        override fun deserialize(content: String): MutableList<Structure>? {
            return Gson().fromJson(
                    content,
                    object : TypeToken<MutableList<Structure>>() {}.type
            )
        }
    }

}