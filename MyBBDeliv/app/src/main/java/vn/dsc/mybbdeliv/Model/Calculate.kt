package dsc.vn.mybbdeliv.Model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

/**
 * Created by NhatAnh on 11/16/2017.
 */
class Calculate {
    var defaultPrice: Double = 0.0
    var totalDVGT: Double = 0.0
    var remoteAreasPrice: Double = 0.0
    var fuelPrice: Double = 0.0
    var otherPrice: Double = 0.0
    var vatPrice: Double = 0.0
    var totalPrice: Double = 0.0

    //User Deserializer
    class Deserializer : ResponseDeserializable<Calculate> {
        override fun deserialize(content: String) = Gson().fromJson(content, Calculate::class.java)
    }

}