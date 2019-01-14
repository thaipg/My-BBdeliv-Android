package dsc.vn.mybbdeliv.Model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

/**
 * Created by NhatAnh on 11/16/2017.
 */
class PaymentType : Serializable {
    var id: Int = 0
    var concurrencyStamp: String? = null
    var isEnabled: Boolean = false
    var code: String = ""
    var name: String = ""

    //User Deserializer
    class Deserializer : ResponseDeserializable<MutableList<PaymentType>> {
        fun deserializeSingle(content: String) = Gson().fromJson(content, PaymentType::class.java)!!

        override fun deserialize(content: String): MutableList<PaymentType>? {
            return Gson().fromJson(
                    content,
                    object : TypeToken<MutableList<PaymentType>>() {}.type
            )
        }
    }
}