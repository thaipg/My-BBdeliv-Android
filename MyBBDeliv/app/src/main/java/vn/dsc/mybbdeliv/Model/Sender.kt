package dsc.vn.mybbdeliv.Model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import java.io.Serializable

/**
 * Created by NhatAnh on 11/16/2017.
 */
class Sender : Serializable {
    var id: Int = 0
    var concurrencyStamp: String = ""
    var code: String = ""
    var name: String = ""
    var isEnabled: Boolean = false
    //User Deserializer
    class Deserializer : ResponseDeserializable<Sender> {
        override fun deserialize(content: String) = Gson().fromJson(content, Sender::class.java)
    }

}