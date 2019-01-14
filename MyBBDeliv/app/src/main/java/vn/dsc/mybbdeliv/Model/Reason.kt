package dsc.vn.mybbdeliv.Model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

/**
 * Created by NhatAnh on 11/16/2017.
 */
class Reason: Serializable {
    val pickFail: Boolean = false
    val pickCancel: Boolean  = false
    val pickReject: Boolean  = false
    val deliverFail: Boolean = false
    val deliverCancel: Boolean = false
    val returnFail: Boolean = false
    val returnCancel: Boolean  = false
    val itemOrder: Int = 0
    val id: Int  = 0
    val code: String = ""
    val name: String = ""
    val isEnabled: Boolean = false

    //User Deserializer
    class Deserializer : ResponseDeserializable<MutableList<Reason>> {
        override fun deserialize(content: String): MutableList<Reason>? {
            return Gson().fromJson(
                    content,
                    object : TypeToken<MutableList<Reason>>() {}.type
            )
        }
    }
}