package pl.sviete.dom.devices.ui.devicedetails

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_device_details.*
import pl.sviete.dom.devices.R
import pl.sviete.dom.devices.db.AisDeviceEntity
import java.lang.Exception

class DeviceDetailsActivity : AppCompatActivity(), DeviceDetailsView.View {

    override val presenter: DeviceDetailsView.Presenter = DeviceDetailsPresenter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        webView.settings.javaScriptEnabled = true

        btn_save.setOnClickListener {
            try {

                clearError()
                presenter.saveView(txt_device_name.text.toString(), txt_device_ip.text.toString())
            }catch (e: Exception) {

            }
        }

        btn_delete.setOnClickListener {
            showQuestionForDelete()
        }

        val detailId = intent.getIntExtra(ARG_DEVICE_ITEM_ID, 0)
        presenter.loadView(detailId)
    }

    override fun showView(device: AisDeviceEntity) {
        txt_device_name.setText(device.name)
        txt_device_ip.setText(device.ip)

        webView.loadUrl("http://" + device.ip)
    }

    private fun showQuestionForDelete() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.delete_question)
        builder.setPositiveButton(R.string.delete){_, _ -> presenter.delete() }
        builder.setNegativeButton(R.string.no){_, _ -> }
        builder.create().show()
    }

    override fun showNameValidationError(resId: Int) {
        txt_device_name.error = resources.getString(resId)
    }

    override fun showIPValidationError(resId: Int) {
        txt_device_ip.error = resources.getString(resId)
    }

    private fun clearError(){
        txt_device_name.error = null
        txt_device_ip.error = null
    }

    companion object {
        val ARG_DEVICE_ITEM_ID: String = "DEVICE_ID"
    }
}
