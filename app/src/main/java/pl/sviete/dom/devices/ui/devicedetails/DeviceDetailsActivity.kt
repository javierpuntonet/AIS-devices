package pl.sviete.dom.devices.ui.devicedetails

import android.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_device_details.*
import pl.sviete.dom.devices.R
import pl.sviete.dom.devices.db.AisDeviceEntity
import java.lang.Exception
import android.content.Intent
import android.net.Uri
import android.view.*

class DeviceDetailsActivity : AppCompatActivity(), DeviceDetailsView.View {

    override val presenter: DeviceDetailsView.Presenter = DeviceDetailsPresenter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btn_save.setOnClickListener {
            try {
                progress_device_details.visibility = View.VISIBLE
                clearError()
                if (!presenter.saveView(txt_device_name.text.toString(), txt_device_ip.text.toString()))
                    progress_device_details.visibility = View.GONE
            }catch (e: Exception) {
                progress_device_details.visibility = View.GONE
            }
        }

        btn_delete.setOnClickListener {
            deleteDevice()
        }

        val detailId = intent.getLongExtra(ARG_DEVICE_ITEM_ID, 0)
        presenter.loadView(detailId)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.device_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_open_device_website -> {
                openDeviceWebSite()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun showView(device: AisDeviceEntity) {
        txt_device_name.setText(device.name)
        txt_device_ip.setText(device.ip)
    }

    private fun deleteDevice() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.delete_device_question)
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

    override fun showSaveErrorInfo(){
        progress_device_details.visibility = View.GONE
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.save_error)
        builder.setMessage(R.string.save_error_info)
        builder.setPositiveButton(R.string.ok) { _, _ -> }
        val dialog = builder.create()
        dialog.show()
    }

    private fun clearError(){
        txt_device_name.error = null
        txt_device_ip.error = null
    }

    private fun openDeviceWebSite() {
        val ip = txt_device_ip.text.toString()
        if (ip.isNotEmpty()) {
            val uri = Uri.parse("http://$ip")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    companion object {
        val ARG_DEVICE_ITEM_ID: String = "DEVICE_ID"
    }
}
