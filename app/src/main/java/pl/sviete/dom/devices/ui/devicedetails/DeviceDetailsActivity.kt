package pl.sviete.dom.devices.ui.devicedetails

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import pl.sviete.dom.devices.R

class DeviceDetailsActivity : AppCompatActivity(), DeviceDetailsView.View {

    private var detailId: Int? = null
    override val presenter: DeviceDetailsView.Presenter = DeviceDetailsPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        detailId = intent.getIntExtra(ARG_DEVICE_ITEM_ID, 0)


    }

    companion object {
        val ARG_DEVICE_ITEM_ID: String = "DEVICE_ID"
    }
}
