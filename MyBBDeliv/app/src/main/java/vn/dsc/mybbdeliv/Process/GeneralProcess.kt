package dsc.vn.mybbdeliv.Process

import android.content.Context
import android.util.Log
import dsc.vn.mybbdeliv.Service.JsonWebservice
import org.json.JSONObject

/**
 * Created by thaiphan on 11/29/17.
 */
class GeneralProcess {
    // Danh sách lý do
    fun getReason(applicationContext: Context, callback: (String) -> Unit) {
        val url = "/reason/getAll"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url

        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    fun getProvinceVN(applicationContext: Context, callback: (String) -> Unit) {
        val url = "/province/getProvinceInVietNam"
        val urlEndpoint = JsonWebservice().core_api_endPoint + url
        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    fun getProvinceByName(applicationContext: Context, name: String, callback: (String) -> Unit) {
        val url = "/province/GetProvinceByName?name=$name"
        val urlEndpoint = JsonWebservice().core_api_endPoint + url
        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    // Danh sách quận huyện
    fun getDistrictByProvinceID(applicationContext: Context, provinceId: String, callback: (String) -> Unit) {
        val url = "/district/getDistrictByProvinceId?provinceId=$provinceId"
        val urlEndpoint = JsonWebservice().core_api_endPoint + url
        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    // Lấy quận huyện
    fun getDistrictByName(applicationContext: Context, name: String, provinceId: String, callback: (String) -> Unit) {
        val url = "/district/GetDistrictByName?name=$name&provinceId=$provinceId"
        val urlEndpoint = JsonWebservice().core_api_endPoint + url
        JsonWebservice().getJson(
                applicationContext, urlEndpoint,
                {
                    callback.invoke(it)
                }
        )
    }

    // Danh sách phường xã
    fun getWardByDistrictID(applicationContext: Context, districtId: String, callback: (String) -> Unit) {
        val url = "/ward/getWardByDistrictId?districtId=$districtId"
        val urlEndpoint = JsonWebservice().core_api_endPoint + url
        JsonWebservice().getJson(
                applicationContext, urlEndpoint,
                {
                    callback.invoke(it)
                }
        )
    }

    // Lấy phường xã
    fun getWardByName(applicationContext: Context, name: String, districtId: String, callback: (String) -> Unit) {
        Log.v("Phuong Xa", name)
        val url = "/Ward/GetWardByName?name=$name&districtId=$districtId"
        val urlEndpoint = JsonWebservice().core_api_endPoint + url
        JsonWebservice().getJson(
                applicationContext, urlEndpoint,
                {
                    callback.invoke(it)
                }
        )
    }

    // Lấy trạm từ phường xã
    fun getHubByWardId(applicationContext: Context, id: String, callback: (String) -> Unit) {
        val url = "/hub/GetHubByWardId?wardId=$id"
        val urlEndpoint = JsonWebservice().core_api_endPoint + url
        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    // Danh sách dịch vụ
    fun getService(applicationContext: Context, callback: (String) -> Unit) {
        val url = "/service/getAll"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    // Danh sách dịch vụ gia tăng
    fun getServiceDVGT(applicationContext: Context, callback: (String) -> Unit) {
        val url = "/serviceDVGT/getAll"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    // Danh sách hình thức thanh toán
    fun getPaymentType(applicationContext: Context, callback: (String) -> Unit) {
        val url = "/paymentType/getAll"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    // Danh sách đóng gói
    fun getPackType(applicationContext: Context, callback: (String) -> Unit) {
        val url = "/packType/getAll"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    // Danh sách đóng gói
    fun getStructure(applicationContext: Context, callback: (String) -> Unit) {
        val url = "/structure/getAll"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    //Tính giá
    fun getCalculate(applicationContext: Context, requestParam: JSONObject, callback: (String) -> Unit) {
        val url = "/price/calculate"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().postJson(
                applicationContext, urlEndpoint, requestParam
        ) {
            callback.invoke(it)
        }
    }

    //Tính giá
    fun getPriceListService(applicationContext: Context, requestParam: JSONObject, callback: (String) -> Unit) {
        val url = "/price/GetListService"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().postJson(
                applicationContext, urlEndpoint, requestParam
        ) {
            callback.invoke(it)
        }
    }


    // Lấy menu badge
    fun getShipmentReportCurrentEmp(callback: (String) -> Unit) {
        val url = "/shipment/getShipmentReportCurrentEmp"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().getJson(urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    // Lấy trạm từ phường xã
    fun getEmployeeSearchByValue(value: String, callback: (String) -> Unit) {
        val url = "/account/searchByValue?value=$value"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().getJson(urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    // Danh sách lý do
    fun getReasonDelay(applicationContext: Context, callback: (String) -> Unit) {
        val url = "/reason/GetByType?type=delay"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url

        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    fun getReasonIncidents(applicationContext: Context, callback: (String) -> Unit) {
        val url = "/reason/GetByType?type=incidents"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url

        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }
}