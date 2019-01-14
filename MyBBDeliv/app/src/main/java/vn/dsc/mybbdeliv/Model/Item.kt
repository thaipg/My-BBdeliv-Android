package dsc.vn.mybbdeliv.Model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

/**
 * Created by NhatAnh on 11/16/2017.
 */
class Item: Serializable {
    var id: Int = 0
    var code: String = ""
    var name: String = ""

    class Deserializer : ResponseDeserializable<MutableList<Item>> {
        fun deserializeSingle(content: String) = Gson().fromJson(content, Item::class.java)!!

        override fun deserialize(content: String): MutableList<Item>? {
            return Gson().fromJson(
                    content,
                    object : TypeToken<MutableList<Item>>() {}.type
            )
        }
    }
}