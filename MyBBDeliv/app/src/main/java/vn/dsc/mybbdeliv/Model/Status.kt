package dsc.vn.mybbdeliv.Model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import java.io.Serializable

/**
 * Created by NhatAnh on 11/16/2017.
 */
class Status: Serializable {

    var id: Int = 0
    var concurrencyStamp: String? = null
    var isEnabled: Boolean = false
    var districtId: Int = 0
    var district: District? = null
    var code: String = ""
    var name: String = ""

    //User Deserializer
    class Deserializer : ResponseDeserializable<Status> {
        override fun deserialize(content: String) = Gson().fromJson(content, Status::class.java)
    }

}