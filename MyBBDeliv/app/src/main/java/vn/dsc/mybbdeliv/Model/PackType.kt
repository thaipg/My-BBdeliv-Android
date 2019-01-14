package dsc.vn.mybbdeliv.Model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

/**
 * Created by NhatAnh on 11/16/2017.
 */
data class PackType(var id: Int,
                    var concurrencyStamp: String?,
                    var isEnabled: Boolean,
                    var code: String,
                    var name: String) {

    //User Deserializer
    class Deserializer : ResponseDeserializable<PackType> {
        override fun deserialize(content: String) = Gson().fromJson(content, PackType::class.java)
    }

}