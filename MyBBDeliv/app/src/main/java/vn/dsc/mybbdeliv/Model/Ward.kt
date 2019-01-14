package dsc.vn.mybbdeliv.Model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

/**
 * Created by NhatAnh on 11/16/2017.
 */
class Ward: Serializable {
    var id: Int = 0
    var isEnabled: Boolean = false
    var code: String = ""
    var name: String = ""
    var districtId: Int = 0
    var district: District? = null

    class Deserializer : ResponseDeserializable<MutableList<Ward>> {
        fun deserializeSingle(content: String) = Gson().fromJson(content, Ward::class.java)!!

        override fun deserialize(content: String): MutableList<Ward>? {
            return Gson().fromJson(
                    content,
                    object : TypeToken<MutableList<Ward>>() {}.type
            )
        }
    }
}