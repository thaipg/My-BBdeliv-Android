package dsc.vn.mybbdeliv.Model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

/**
 * Created by NhatAnh on 11/16/2017.
 */
class LadingSchedule : Serializable {

    var id: Int = 0
    var shipmentId: Int = 0
    var location: String = ""
    var dateCreated: String = ""
    var timeCreated: String = ""
    var hubId: Int = 0
    var hubName: String = ""
    var userId: Int = 0
    var userFullName: String = ""
    var shipmentStatusId: Int = 0
    var shipmentStatusName: String = ""
    var note: String = ""

    //User Deserializer
    class Deserializer : ResponseDeserializable<MutableList<LadingSchedule>> {
        fun deserializeSingle(content: String) = Gson().fromJson(content, LadingSchedule::class.java)!!

        override fun deserialize(content: String): MutableList<LadingSchedule>? {
            return Gson().fromJson(
                    content,
                    object : TypeToken<MutableList<LadingSchedule>>() {}.type
            )
        }
    }

}