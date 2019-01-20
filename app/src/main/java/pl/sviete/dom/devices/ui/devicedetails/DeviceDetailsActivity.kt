package pl.sviete.dom.devices.ui.devicedetails

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_device_details.*
import pl.sviete.dom.devices.R
import pl.sviete.dom.devices.db.AisDeviceEntity
import android.content.Intent
import android.view.MenuItem
import pl.sviete.dom.devices.MainActivity
import pl.sviete.dom.devices.R.id.toolbar





class DeviceDetailsActivity : AppCompatActivity(), DeviceDetailsView.View {

    override val presenter: DeviceDetailsView.Presenter = DeviceDetailsPresenter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        webView.settings.javaScriptEnabled = true

        btn_save.setOnClickListener {
            presenter.saveView(txt_device_name.text.toString(), txt_device_ip.text.toString())
        }

        btn_delete.setOnClickListener {
            presenter.delete()
            finish()
        }

        val detailId = intent.getIntExtra(ARG_DEVICE_ITEM_ID, 0)

        presenter.loadView(detailId)
    }

    override fun showView(device: AisDeviceEntity) {
        txt_device_name.setText(device.name)
        txt_device_ip.setText(device.ip)

        webView.loadUrl("http://" + device.ip)
    }

    companion object {
        val ARG_DEVICE_ITEM_ID: String = "DEVICE_ID"
    }
}
