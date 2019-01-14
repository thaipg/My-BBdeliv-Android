package dsc.vn.mybbdeliv.Model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import java.io.Serializable

/**
 * Created by NhatAnh on 11/16/2017.
 */
class MapModel : Serializable {
    var lat:Double = 0.0
    var lng:Double = 0.0
    var name: String = ""
    var title:String? = ""
    var snippet:String? = ""
    var phone:String = ""

    //User Deserializer
    class Deserializer : ResponseDeserializable<MapModel> {
        override fun deserialize(content: String) = Gson().fromJson(content, MapModel::class.java)!!
    }

}