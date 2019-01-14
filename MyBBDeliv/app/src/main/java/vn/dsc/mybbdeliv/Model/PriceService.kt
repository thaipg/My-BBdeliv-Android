package dsc.vn.mybbdeliv.Model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by thaiphan on 12/3/17.
 */
class PriceService {
    var price: Double? = null
    var id: Int = 0
    var expectedDeliveryTime: String? = null
    var isEnabled: Boolean? = null
    var code: String? = null
    var name: String = ""

    class Deserializer : ResponseDeserializable<MutableList<PriceService>> {
        fun deserializeSingle(content: String) = Gson().fromJson(content, PriceService::class.java)!!

        override fun deserialize(content: String): MutableList<PriceService>? {
            return Gson().fromJson(
                    content,
                    object : TypeToken<MutableList<PriceService>>() {}.type
            )
        }
    }
}