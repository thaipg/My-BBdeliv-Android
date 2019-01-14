package dsc.vn.mybbdeliv.Model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

/**
 * Created by NhatAnh on 11/16/2017.
 */
open class Shipment : Serializable {
    var id: Int = 0
    var shipmentNumber: String = ""
    var concurrencyStamp: String? = null
    var isEnabled: Boolean = false
    var orderDate: String? = null

    var sellerName: String? = null
    var sellerPhone: String? = null

    var sender: Sender? = null
    var senderId: Int? = null
    var senderName: String? = ""
    var senderPhone: String? = ""
    var pickingAddress: String? = null
    var addressNoteFrom: String? = null
    var companyFrom: String? = null
    var deliveryImagePath: String? = null

    var fromProvinceId: Int = 0
    var fromDistrictId: Int = 0
    var fromWardId: Int = 0
    var fromHubId: Int = 0

    var receiverName: String? = null
    var receiverPhone: String? = null
    var shippingAddress: String? = null
    var addressNoteTo: String? = null
    var companyTo: String? = null

    var reasonId: Int? = null
    var realRecipientName: String? = null
    var cusNote: String? = null
    var toProvinceId: Int? = null
    var toDistrictId: Int? = null
    var toWardId: Int? = null
    var toHubId: Int? = null

    var note: String? = null
    var weight: Double? = null
    var length: Int? = null
    var height: Int? = null
    var width: Int? = null
    var calWeight: Double? = null
    var totalBox: Int? = null
    var countShipment: Int? = null

    var shipmentStatusId: Int = 0
    var latFrom: Double = 0.0
    var lngFrom: Double = 0.0
    var latTo: Double? = null
    var lngTo: Double? = null
    var numPick: Int = 0
    var numDeliver: Int = 0
    var numReturn: Int = 0
    var numTransfer: Int = 0
    var numTransferReturn: Int = 0

    var fromHub: Hub? = null
    var toHub: Hub? = null
    //    var fromHubRouting": null,
//    var toHubRouting": null,
    var fromWard: Ward? = null
    var toWard: Ward? = null
    var shipmentStatus: Status? = null
    var currentLat: Double? = null
    var currentLng: Double? = null
    var location: String? = null
    var ladingSchedules: List<LadingSchedule>? = null

    var priceListId: Double? = null
    var cod: Double? = null
    var insured: Double? = null
    var defaultPrice: Double? = null
    var remoteAreasPrice: Double? = null
    var fuelPrice: Double? = null
    var totalDVGT: Double? = null
    var otherPrice: Double? = null
    var vatPrice: Double? = null
    var totalPrice: Double? = null

    var serviceId: Int? = null
    var structureId: Int? = null
    var packTypeId: Int? = null
    var paymentTypeId: Int? = null

    var expectedDeliveryTime: String? = null

    var service: Service? = null
    var structure: Structure? = null
    var paymentType: PaymentType? = null

    var pickupAppointmentTime: String? = null
    var deliveryAppointmentTime: String? = null
    var returnAppointmentTime: String? = null

    var startPickTime: String? = null
    var startTransferTime: String? = null
    var startDeliveryTime: String? = null
    var startReturnTime: String? = null
    var startTransferReturnTime: String? = null

    var firstPickupTime: String? = null
    var firstTransferTime: String? = null
    var firstDeliveredTime: String? = null
    var firstReturnTime: String? = null
    var firstReturnTransferTime: String? = null


    var pickUserId: Int? = null
    var currentEmpId: Int? = null
    var createdWhen: String? = null

    var checkBoxState: Boolean = false
    var requestShipmentId: Int? = null
    var totalShipment: Int? = null
    var serviceDVGTIds: Any? = null
    var cusDepartmentId: Int? = null

    //User Deserializer
    class Deserializer : ResponseDeserializable<MutableList<Shipment>> {
        fun deserializeSingle(content: String) = Gson().fromJson(content, Shipment::class.java)!!

        override fun deserialize(content: String): MutableList<Shipment>? {
            return Gson().fromJson(
                    content,
                    object : TypeToken<MutableList<Shipment>>() {}.type
            )
        }
    }
}