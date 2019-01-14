package vn.dsc.mybbdeliv.View.Vietstar

import android.os.Bundle
import dsc.vn.mybbdeliv.Base.BaseActivity
import dsc.vn.mybbdeliv.R
import kotlinx.android.synthetic.main.content_check_extra_price.*

class CheckExtraPriceActivity : BaseActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_check_extra_price)
        super.onCreate(savedInstanceState)

        var url = "http://api.bbdeliv.vn/ExtraPrice/Index"
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url)
    }
}
