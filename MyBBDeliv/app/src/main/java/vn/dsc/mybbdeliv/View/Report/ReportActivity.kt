package dsc.vn.mybbdeliv.View.Report

import android.app.DatePickerDialog
import android.os.Bundle
import dsc.vn.mybbdeliv.Base.BaseActivity
import dsc.vn.mybbdeliv.Model.Report
import dsc.vn.mybbdeliv.Process.ShipmentProcess
import dsc.vn.mybbdeliv.R
import kotlinx.android.synthetic.main.content_report.*
import java.util.*

class ReportActivity : BaseActivity() {

    var calendar = Calendar.getInstance()!!
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_report)
        super.onCreate(savedInstanceState)
        prepareUI()
        bindData()
    }

    private fun prepareUI() {

        txtDay.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                calendar.set(year,monthOfYear,dayOfMonth)
                bindData()
            }, year, month, day)
            val locale = Locale("vi_VN")
            Locale.setDefault(locale)
            dpd.show()
        }
    }


    private fun bindData() {
        txtMonth.text = "Tháng ${calendar.get(Calendar.MONTH) + 1}"
        txtDay.text = calendar.get(Calendar.DAY_OF_MONTH).toString()
        txtYear.text = calendar.get(Calendar.YEAR).toString()

        ShipmentProcess().getReportEmployee (this, calendar.time) {
            val data = Report.Deserializer().deserializeSingle(it)
            txtPickupDay.text =  data.totalPickupCompleteOfCurrentDay.toString()
            txtPickupMonth.text = data.totalPickupCompleteOfCurrentMonth.toString()
            txtDeliveryDay.text =  data.totalDeliveyCompleteOfCurrentDay.toString()
            txtDeliveryMonth.text = data.totalDeliveyCompleteOfCurrentMonth.toString()
            txtReturnDay.text = data.totalReturnCompleteOfCurrentDay.toString()
            txtReturnMonth.text = data.totalReturnCompleteOfCurrentMonth.toString()
        }
    }
}
