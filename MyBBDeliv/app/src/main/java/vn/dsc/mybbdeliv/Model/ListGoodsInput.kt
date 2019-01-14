package vn.dsc.mybbdeliv.Model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

/**
 * Created by NhatAnh on 11/16/2017.
 */
class ListGoodsInput: Serializable {
    var id: Int = 0
    var code: String = ""
    var name: String = ""
    var listGoodsTypeId: Int? = null
    var fromHubId: Int? = null
    var toHubId: Int? = null
    var createdByHub: Int? = null
    var listGoodsStatusId: Int? = null
    var empId: Int? = null
    var transportTypeId: Int? = null
    var truckId: Int? = null
    var feeRent: Double = 0.0

    class Deserializer : ResponseDeserializable<MutableList<ListGoodsInput>> {
        fun deserializeSingle(content: String) = Gson().fromJson(content, ListGoodsInput::class.java)!!

        override fun deserialize(content: String): MutableList<ListGoodsInput>? {
            return Gson().fromJson(
                    content,
                    object : TypeToken<MutableList<ListGoodsInput>>() {}.type
            )
        }
    }
}