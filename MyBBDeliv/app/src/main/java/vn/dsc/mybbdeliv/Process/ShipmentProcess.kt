package dsc.vn.mybbdeliv.Process

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import dsc.vn.mybbdeliv.Model.Shipment
import dsc.vn.mybbdeliv.Service.JsonWebservice
import dsc.vn.mybbdeliv.Service.LocationTrackingService
import dsc.vn.mybbdeliv.Utils.ToastUtils
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by NhatAnh on 11/16/2017.
 */
class ShipmentProcess {
    // Danh sách yêu cầu lấy hàng
    fun getShipmentRequestPickup(applicationContext: Context,callback: (String) -> Unit) {
        val url = "/requestshipment/GetByStatusCurrentEmp?statusId=41&cols=Sender"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url

        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    // Danh sách hàng đang đi lấy
    fun getShipmentCreated(applicationContext: Context,callback: (String) -> Unit) {
        val url = "/shipment/GetByStatusCurrentEmp?statusId=3&pageSize=10&pageNumber=1&cols=ShipmentStatus,Sender,FromWard,FromWard.District,FromWard.District.Province,ToWard,ToWard.District,ToWard.District.Province,Service,Structure,PaymentType"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url

        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    // Danh sách hàng đang đi lấy
    fun getShipmentPickup(applicationContext: Context,callback: (String) -> Unit) {
        val url = "/requestshipment/GetByStatusCurrentEmp?statusId=2&cols=ShipmentStatus,Sender,FromWard,FromWard.District,FromWard.District.Province,ToWard,ToWard.District,ToWard.District.Province,Service,Structure,PaymentType"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url

        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    // Cập nhật trạng thái cho yêu cầu
    fun updateRequestShipment(applicationContext: Context, shipment: Shipment, callback: (String) -> Unit)
    {
        val jsonString = Gson().toJson(shipment)
        val json = JSONObject(jsonString)
        val url = "/requestshipment/updateStatusCurrentEmp"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url

        JsonWebservice().postJson(
                applicationContext, urlEndpoint, json
        ) {
            callback.invoke(it)
        }
    }

    // Lấy hàng thư lô
    fun acceptCompleteByCurrentEmp(applicationContext: Context, shipment: Shipment, callback: (String) -> Unit)
    {
        val jsonString = Gson().toJson(shipment)
        val json = JSONObject(jsonString)
        val url = "/requestshipment/AcceptCompleteByCurrentEmp"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url

        JsonWebservice().postJson(
                applicationContext, urlEndpoint, json
        ) {
            Log.v("return",it)
            callback.invoke("{\"data\":\"success\"}")
        }
    }

    //Cập nhật thành công
    fun updatePickupCompleteByCurrentEmp(applicationContext: Context, shipment: Shipment, callback: (String) -> Unit) {

        val jsonString = Gson().toJson(shipment)
        val json = JSONObject(jsonString)

        val url = "/requestshipment/pickupCompleteByCurrentEmp"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url

        JsonWebservice().postJson(
                applicationContext, urlEndpoint, json
        ) {
            callback.invoke(it)
        }
    }

    //Danh sách chờ trung chuyển - trả hàng
    fun getShipmentReadyToTransfer(applicationContext: Context, callback: (String) -> Unit)
    {
        val url = "/shipment/getByStatusCurrentEmp?statusId=49&cols=Sender,ToHub"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    fun getShipmentReadyToTransferReturn(applicationContext: Context, callback: (String) -> Unit)
    {
        val url = "/shipment/getByStatusCurrentEmp?statusId=50&cols=Sender,ToHub"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    //Danh sách đang trung chuyển - trả hàng
    fun getShipmentTransfer(applicationContext: Context, callback: (String) -> Unit)
    {
        val url = "/shipment/getByStatusCurrentEmp?statusId=8&cols=Sender,ToHub"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    fun getShipmentTransferReturn(applicationContext: Context, callback: (String) -> Unit)
    {
        val url = "/shipment/getByStatusCurrentEmp?statusId=37&cols=Sender,ToHub"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    //Danh sách chờ giao hàng
    fun trackingShipment(applicationContext: Context, shipmentNumber: String , callback: (String) -> Unit)
    {
        val url = "/shipment/tracking?shipmentNumber=$shipmentNumber&cols=Sender,FromWard.District.Province,ToWard.District.Province,ShipmentStatus,Structure,Service,PaymentType,LadingSchedule"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    //Danh sách chờ giao hàng - trả hàn
    fun getShipmentWaitingDelivery(applicationContext: Context, callback: (String) -> Unit)
    {
        val url = "/shipment/getByStatusCurrentEmp?statusId=48&cols=Sender"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    fun getShipmentWaitingReturn(applicationContext: Context, callback: (String) -> Unit)
    {
        val url = "/shipment/getByStatusCurrentEmp?statusId=51&cols=Sender"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    //Danh sách đang giao hàng - trả hàng
    fun getShipmentDelivery(applicationContext: Context, callback: (String) -> Unit)
    {
        val url = "/shipment/getByStatusCurrentEmp?statusId=11&cols=Sender,PaymentType"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    fun getShipmentReturn(applicationContext: Context, callback: (String) -> Unit)
    {
        val url = "/shipment/getByStatusCurrentEmp?statusId=31&cols=Sender,PaymentType"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    //Lịch sử
    fun getCurrentEmpHistory(applicationContext: Context, callback: (String) -> Unit)
    {
        val url = "/account/getCurrentEmpHistory?pageSize=20&pageNumber=1&cols=Shipment,ShipmentStatus"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    // Cập nhật trạng thái cho vận đơn
    fun createShipment(applicationContext: Context, shipment: Shipment, callback: (String) -> Unit)
    {
        val jsonString = Gson().toJson(shipment)
        val json = JSONObject(jsonString)

        val url = "/shipment/create"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().postJson(
                applicationContext, urlEndpoint, json
        ) {
            callback.invoke(it)
        }
    }

    // Cập nhật trạng thái cho vận đơn
    fun updateShipment(applicationContext: Context, shipment: Shipment, callback: (String) -> Unit)
    {
        val jsonString = Gson().toJson(shipment)
        val json = JSONObject(jsonString)

        val url = "/shipment/updateStatusCurrentEmp"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().postJson(
                applicationContext, urlEndpoint, json
        ) {
            callback.invoke(it)
        }
    }

    //upload hình ảnh
    fun uploadImagePickupComplete(applicationContext: Context, uploadData: JSONObject, callback: (String) -> Unit)
    {

        Log.v("json upload data",uploadData.toString())
        val url = "/upload/UploadImagePickupComplete"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url

        JsonWebservice().postJson(
                applicationContext, urlEndpoint, uploadData
        ) {
            callback.invoke(it)
        }
    }

    //upload hình ảnh
    fun uploadDeliveryComplete(applicationContext: Context, uploadData: JSONObject, callback: (String) -> Unit)
    {

        Log.v("json upload data",uploadData.toString())
        val url = "/upload/UploadDeliveryComplete"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url

        JsonWebservice().postJson(
                applicationContext, urlEndpoint, uploadData
        ) {
            callback.invoke(it)
        }
    }

    //download hình ảnh
    fun downloadImage(applicationContext: Context, imagePath: String, callback: (String) -> Unit)
    {
        val url = "/upload/getImageByPath?imagePath=$imagePath"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url

        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    //Scan barcode trung chuyển
    fun receiveTransitShipment(applicationContext: Context, shipmentNumber: String, callback: (String) -> Unit)
    {
        val json = JSONObject()
        json.put("shipmentNumber", shipmentNumber)
        json.put("currentLat", LocationTrackingService.lastLocation.latitude)
        json.put("currentLng", LocationTrackingService.lastLocation.latitude)
        val geocoder = Geocoder(applicationContext, Locale.getDefault())
        val addresses = geocoder.getFromLocation(
                LocationTrackingService.lastLocation.latitude,
                LocationTrackingService.lastLocation.longitude,
                1)

        var address: Address?
        if (addresses.count() > 0) {
            address = addresses[0]
        }
        else
        {
            ToastUtils.error(applicationContext,"Định vị của bạn không hoạt động, không thể cập nhật thông tin")
            return
        }
        val addressFragments = (0..address.maxAddressLineIndex).map(address::getAddressLine)
        json.put("location",  TextUtils.join(System.getProperty("line.separator"),
                addressFragments).replace("\n", ", ") )

        val url = "/shipment/receiveTransitShipment"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url

        JsonWebservice().postJson(
                applicationContext, urlEndpoint, json
        ) {
            callback.invoke(it)
        }
    }

    //Scan barcode giao hàng
    fun receiveDeliveryReturnShipment(applicationContext: Context, shipmentNumber: String, callback: (String) -> Unit)
    {
        val json = JSONObject()
        json.put("shipmentNumber", shipmentNumber)
        json.put("currentLat", LocationTrackingService.lastLocation.latitude)
        json.put("currentLng", LocationTrackingService.lastLocation.latitude)
        val geocoder = Geocoder(applicationContext, Locale.getDefault())
        val addresses = geocoder.getFromLocation(
                LocationTrackingService.lastLocation.latitude,
                LocationTrackingService.lastLocation.longitude,
                1)

        var address: Address?
        if (addresses.count() > 0) {
            address = addresses[0]
        }
        else
        {
            ToastUtils.error(applicationContext,"Định vị của bạn không hoạt động, không thể cập nhật thông tin")
            return
        }
        val addressFragments = (0..address.maxAddressLineIndex).map(address::getAddressLine)
        json.put("location",  TextUtils.join(System.getProperty("line.separator"),
                addressFragments).replace("\n", ", ") )

        val url = "/shipment/receiveDeliveryReturnShipment"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url

        JsonWebservice().postJson(
                applicationContext, urlEndpoint, json
        ) {
            callback.invoke(it)
        }
    }

    //Scan barcode chuyển hàng
    fun receiveShipment(applicationContext: Context, shipmentNumber: String, callback: (String) -> Unit)
    {
        val json = JSONObject()
        json.put("shipmentNumber", shipmentNumber)
        json.put("currentLat", LocationTrackingService.lastLocation.latitude)
        json.put("currentLng", LocationTrackingService.lastLocation.latitude)
        val geocoder = Geocoder(applicationContext, Locale.getDefault())
        val addresses = geocoder.getFromLocation(
                LocationTrackingService.lastLocation.latitude,
                LocationTrackingService.lastLocation.longitude,
                1)

        var address: Address?
        if (addresses.count() > 0) {
            address = addresses[0]
        }
        else
        {
            ToastUtils.error(applicationContext,"Định vị của bạn không hoạt động, không thể cập nhật thông tin")
            return
        }
        val addressFragments = (0..address.maxAddressLineIndex).map(address::getAddressLine)
        json.put("location",  TextUtils.join(System.getProperty("line.separator"),
                addressFragments).replace("\n", ", ") )


        val url = "/shipment/receiveShipment"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url

        JsonWebservice().postJson(
                applicationContext, urlEndpoint, json
        ) {
            callback.invoke(it)
        }
    }

    //Danh sách vận đơn trong yêu cầu
    fun getByRequestShipmentId(applicationContext: Context, id: String, callback: (String) -> Unit)
    {
        val url = "/shipment/getByRequestShipmentId?id=$id&cols=ShipmentStatus,Sender,FromWard,FromWard.District,FromWard.District.Province,ToWard,ToWard.District,ToWard.District.Province,Service,Structure,PaymentType"
        Log.v("url" , url)
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    //Scan barcode xem nơi đến
    fun checkExistShipmentNumber(applicationContext: Context, shipmentNumber: String, callback: (String) -> Unit)
    {
        val url = "/shipment/checkExistShipmentNumber?shipmentNumber=$shipmentNumber"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url

        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke("true")
        }
    }

    //Get Report Keeping Money
    fun getReportKeepingMoneyEmployee(applicationContext: Context, callback: (String) -> Unit)
    {
        val empId = UserProcess.token!!.userId
        val url = "/report/GetReportEmployee?empId=$empId"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url

        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }


    //Get Report
    fun getReportEmployee(applicationContext: Context, date: Date, callback: (String) -> Unit)
    {
        val format = SimpleDateFormat("yyyy-MM-dd")

        val url = "/report/GetReportDeliveryComplete?date=${format.format(date)}"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url

        JsonWebservice().getJson(
                applicationContext, urlEndpoint
        ) {
            callback.invoke(it)
        }
    }

    //Lấy hàng vse
    fun pickupVietstar(applicationContext: Context, param: List<Pair<String,Any>>, callback: (String) -> Unit)
    {
        val url = "/MsgGateWay/SyncVDTrang"
        val urlEndpoint = JsonWebservice().vietstar_api_endPoint + url

        JsonWebservice().postJsonVietstar(
                applicationContext, urlEndpoint, param
        ) {
            callback.invoke(it)
        }
    }
    //Giao hàng vse
    fun deliveryVietstar(applicationContext: Context, param: List<Pair<String,Any>>, callback: (String) -> Unit)
    {
        val url = "/MsgGateWay/SyncVDHong"
        val urlEndpoint = JsonWebservice().vietstar_api_endPoint + url

        JsonWebservice().postJsonVietstar(
                applicationContext, urlEndpoint, param
        ) {
            callback.invoke(it)
        }
    }

    //Scan barcode xuat kho trung chuyển
    fun issueTransfer(applicationContext: Context, listGoodsId:Int ,toHubId:Int, shipmentNumber: String, callback: (String) -> Unit)
    {
        val json = JSONObject()
        json.put("listGoodsId", listGoodsId)
        json.put("toHubId", toHubId)
        json.put("shipmentNumber", shipmentNumber)

        val url = "/shipment/IssueTransfer"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().postJson(
                applicationContext, urlEndpoint, json
        ) {
            callback.invoke(it)
        }
    }

    fun getByListGoodsId(applicationContext: Context, listGoodsId:Int, callback: (String) -> Unit)
    {
        val json = JSONObject()
        json.put("id", listGoodsId)
        json.put("pageNumber", 1)
        json.put("pageSize", 10)

        val url = "/shipment/getByListGoodsId"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().postJson(
                applicationContext, urlEndpoint, json
        ) {
            callback.invoke(it)
        }
    }

    //Scan barcode xuat kho báo phát
    fun issueDelivery(applicationContext: Context, listGoodsId:Int , shipmentNumber: String, callback: (String) -> Unit)
    {
        val json = JSONObject()
        json.put("listGoodsId", listGoodsId)
        json.put("shipmentNumber", shipmentNumber)

        val url = "/shipment/IssueDelivery"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().postJson(
                applicationContext, urlEndpoint, json
        ) {
            callback.invoke(it)
        }
    }

    //Scan barcode xuat kho báo phát
    fun addDelay(applicationContext: Context, json:JSONObject , callback: (String) -> Unit)
    {
        val url = "/shipment/addDelay"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().postJson(
                applicationContext, urlEndpoint, json
        ) {
            callback.invoke(it)
        }
    }

    fun addIncidents(applicationContext: Context, json:JSONObject , callback: (String) -> Unit)
    {
        val url = "/Shipment/AddIncidents"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().postJson(
                applicationContext, urlEndpoint, json
        ) {
            callback.invoke(it)
        }
    }

    fun uploadImageIncidents(applicationContext: Context, json:JSONObject , callback: (String) -> Unit)
    {
        val url = "/upload/UploadImageIncidents"
        val urlEndpoint = JsonWebservice().post_api_endPoint + url
        JsonWebservice().postJson(
                applicationContext, urlEndpoint, json
        ) {
            callback.invoke(it)
        }
    }
}