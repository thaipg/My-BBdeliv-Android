package vn.dsc.mybbdeliv.Process

import android.content.Context
import dsc.vn.mybbdeliv.Service.JsonWebservice
import org.json.JSONObject


/**
 * Created by thaiphan on 11/29/17.
 */
class WarehouseProcess {

    fun createListGoods(applicationContext: Context, callback: (String) -> Unit) {
        val json = JSONObject()
        json.put("typeWareHousing", 2)
        val url = "/listGoods/createListGoods"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().postJson(
                applicationContext, urlEndpoint, json
        ) {
            callback.invoke(it)
        }
    }

    fun receiptWarehousing(applicationContext: Context,requestParam: JSONObject, callback: (String) -> Unit) {
        val url = "/shipment/ReceiptWarehousing"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().postJson(
                applicationContext, urlEndpoint, requestParam
        ) {
            callback.invoke(it)
        }
    }



    // Lấy trạm từ phường xã
    fun getHubSearchByValue(value: String, callback: (String) -> Unit) {
        val url = "/hub/searchByValue?value=$value&id=null"
        val urlEndpoint = JsonWebservice().core_api_endPoint + url
        JsonWebservice().getJson(urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    fun getListGoodsTransferNew(id: String, callback: (String) -> Unit) {
        val url = "/listGoods/GetListGoodsTransferNew?toHubId=$id"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().getJson(urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    fun listGoodsCreateTransfer(id: String, callback: (String) -> Unit) {
        val json = JSONObject()
        json.put("listGoodsTypeId", 8)
        json.put("toHubId", id)
        val url = "/listGoods/create"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().postJson(urlEndpoint, json
        ) {
            callback.invoke(it)
        }
    }

    // Lấy hinh thuc trung chuyen
    fun getTransportType(applicationContext: Context,callback: (String) -> Unit) {
        val url = "/transportType/getAll"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().getJson(applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }
    // Lấy xe
    fun searchByTruckNumber(value: String, callback: (String) -> Unit) {
        val url = "/truck/SearchByTruckNumber?truckNumber=$value"
        val urlEndpoint = JsonWebservice().core_api_endPoint + url
        JsonWebservice().getJson(urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    fun blockListGoods(applicationContext: Context,json: JSONObject, callback: (String) -> Unit) {
        val url = "/listGoods/BlockListGoods"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().postJson(applicationContext , urlEndpoint, json
        ) {
            callback.invoke(it)
        }
    }


    fun listGoodsCreateDelivery(empId: String, callback: (String) -> Unit) {
        val json = JSONObject()
        json.put("listGoodsTypeId", 3)
        json.put("empId", empId)
        val url = "/listGoods/create"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().postJson(urlEndpoint, json
        ) {
            callback.invoke(it)
        }
    }

    fun getListGoodsDeliveryNew(id: String, callback: (String) -> Unit) {
        val url = "/listGoods/GetListGoodsDeliveryNew?empId=$id"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().getJson(urlEndpoint
        ) {
            callback.invoke(it)
        }
    }
}