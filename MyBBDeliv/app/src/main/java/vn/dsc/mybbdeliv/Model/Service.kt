package dsc.vn.mybbdeliv.Model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import java.io.Serializable

/**
 * Created by NhatAnh on 11/16/2017.
 */
class Service : Serializable {

    var isPublish: Boolean? = null
    var id: Int = 0
    var concurrencyStamp: String = ""
    var isEnabled: Boolean? = null
    var code: String? = null
    var name: String = ""

    //User Deserializer
    class Deserializer : ResponseDeserializable<Service> {
        override fun deserialize(content: String) = Gson().fromJson(content, Service::class.java)
    }

}