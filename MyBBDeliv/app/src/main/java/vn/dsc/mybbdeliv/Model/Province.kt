package dsc.vn.mybbdeliv.Model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

/**
 * Created by NhatAnh on 11/16/2017.
 */
class Province : Serializable {
    var id: Int = 0
    var concurrencyStamp: String? = null
    var isEnabled: Boolean = false
    var countryId: Int = 0
    var country: Country? = null
    var code: String = ""
    var name: String = ""

    class Deserializer : ResponseDeserializable<MutableList<Province>> {
        override fun deserialize(content: String): MutableList<Province>? {
            return Gson().fromJson(
                    content,
                    object : TypeToken<MutableList<Province>>() {}.type
            )
        }
        fun deserializeSingle(content: String) = Gson().fromJson(content, Province::class.java)!!

    }
}