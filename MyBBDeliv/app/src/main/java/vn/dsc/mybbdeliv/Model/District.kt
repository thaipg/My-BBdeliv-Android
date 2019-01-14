package dsc.vn.mybbdeliv.Model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

/**
 * Created by NhatAnh on 11/16/2017.
 */
class District: Serializable {
    var id: Int = 0
    var concurrencyStamp: String? = null
    var isEnabled: Boolean = false
    var provinceId: Int = 0
    var province: Province? = null
    var code: String = ""
    var name: String = ""


    class Deserializer : ResponseDeserializable<MutableList<District>> {
        fun deserializeSingle(content: String) = Gson().fromJson(content, District::class.java)!!

        override fun deserialize(content: String): MutableList<District>? {
            return Gson().fromJson(
                    content,
                    object : TypeToken<MutableList<District>>() {}.type
            )
        }
    }
}