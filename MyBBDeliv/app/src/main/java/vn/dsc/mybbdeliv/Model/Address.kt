package dsc.vn.mybbdeliv.Model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

/**
 * Created by NhatAnh on 11/16/2017.
 */
class Address {

    var street_number: String? = null
    var route: String? = null
    var sublocality_level_1: String? = null
    var locality: String? = null
    var administrative_area_level_2: String? = null
    var administrative_area_level_1: String? = null
    var country: String? = null
    var lat: String? = null
    var lng: String? = null

    //User Deserializer
    class Deserializer : ResponseDeserializable<Address> {
        override fun deserialize(content: String) = Gson().fromJson(content, Address::class.java)
    }

}