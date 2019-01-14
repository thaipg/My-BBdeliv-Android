package dsc.vn.mybbdeliv.Process


import android.content.Context
import android.util.Log
import org.json.JSONObject
import dsc.vn.mybbdeliv.Service.JsonWebservice

/**
 * Created by thaiphan on 11/29/17.
 */
class CustomerProcess {
    // Danh sách phòng ban
    fun getDepartment(applicationContext: Context,customerId: String, callback: (String) -> Unit) {
        val url = "/CusDepartment/GetByCustomerId?customerId=$customerId"
        val urlEndpoint = JsonWebservice().crm_api_endPoint + url
        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }
}