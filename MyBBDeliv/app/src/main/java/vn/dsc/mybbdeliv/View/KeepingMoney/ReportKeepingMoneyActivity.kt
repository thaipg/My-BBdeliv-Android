package dsc.vn.mybbdeliv.View.Report

import android.os.Bundle
import kotlinx.android.synthetic.main.content_report_keeping_money.*
import dsc.vn.mybbdeliv.Base.BaseActivity
import dsc.vn.mybbdeliv.Model.ReportKeepingMoney
import dsc.vn.mybbdeliv.Process.ShipmentProcess
import dsc.vn.mybbdeliv.R
import java.text.DecimalFormat

class ReportKeepingMoneyActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_report_keeping_money)
        super.onCreate(savedInstanceState)
        prepareUI()
        bindData()
    }

    private fun prepareUI() {
    }


    private fun bindData() {
        ShipmentProcess().getReportKeepingMoneyEmployee (this, {
            val data = ReportKeepingMoney.Deserializer().deserialize(it)!![0]
            val formatter = DecimalFormat("#,###,###")
            txtCODKeeping.text =  formatter.format(data.totalCODKeeping)
            txtPriceKeeping.text = formatter.format(data.totalPriceKeeping)
            txtShipmentKeeping.text = data.totalShipmentKeeping.toString()
            txtTranfering.text = (data.transferring + data.transferReturning).toString()
            txtDelivering.text = data.delivering.toString()
            txtDeliveryFail.text = data.deliveryFail.toString()
            txtReturn.text = data.returning.toString()
            txtPickupComplete.text = data.pickupComplete.toString()
            txtReturnFail.text = data.returnFail.toString()
        })
    }
}
