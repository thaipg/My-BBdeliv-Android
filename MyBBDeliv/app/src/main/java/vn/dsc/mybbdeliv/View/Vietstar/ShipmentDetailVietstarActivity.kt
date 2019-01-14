package vn.dsc.mybbdeliv.View.Vietstar

import android.os.Bundle
import dsc.vn.mybbdeliv.Base.BaseSubActivity
import dsc.vn.mybbdeliv.R
import kotlinx.android.synthetic.main.content_check_extra_price.*

class ShipmentDetailVietstarActivity : BaseSubActivity()  {

    override val layoutResourceId: Int
        get() {
            return R.layout.activity_shipment_detail_vietstar
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_shipment_detail_vietstar)
        super.onCreate(savedInstanceState)

        val intent = intent
        val bundle = intent.extras
        val shipment = (bundle!!.getSerializable("bill") as String)

        var url = "http://tracking.bbdeliv.vn/tracking/bill/$shipment"
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url)
    }
}
