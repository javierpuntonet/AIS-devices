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
import android.widget.ArrayAdapter
import pl.sviete.dom.devices.ExceptionHandler
import pl.sviete.dom.devices.ui.areas.AreaViewModel

class DeviceDetailsActivity : AppCompatActivity(), DeviceDetailsView.View {

    override val presenter: DeviceDetailsView.Presenter = DeviceDetailsPresenter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler())
        setContentView(R.layout.activity_device_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        btn_save.setOnClickListener {
            try {
                progress_device_details.visibility = View.VISIBLE
                clearError()
                var area = spinner_device_area.selectedItem as AreaViewModel?
                if (area?.id == -1L)
                    area = null
                if (!presenter.saveView(txt_device_name.text.toString(), txt_device_ip.text.toString(), area))
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
            R.id.menu_pair_device_with_box -> {
                presenter.initPairWithBox()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun showView(device: AisDeviceEntity) {
        txt_device_name.setText(device.name)
        txt_device_ip.setText(device.ip)
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

    override fun setAreas(areas: List<AreaViewModel>, selectedIdx: Int) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, areas)
        spinner_device_area.adapter = adapter
        spinner_device_area.setSelection(selectedIdx)
    }

    override fun showNoBoxesMessage() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.no_boxes_to_pair)
        builder.setPositiveButton(R.string.ok) { _, _ -> }
        val dialog = builder.create()
        dialog.show()
    }

    override fun showPairStatus(success: Boolean) {
        val builder = AlertDialog.Builder(this)
        if (success)
            builder.setMessage(R.string.pair_success)
        else
            builder.setMessage(R.string.pair_error)
        builder.setPositiveButton(R.string.ok) { _, _ -> }
        val dialog = builder.create()
        dialog.show()
    }

    override fun selectBoxToPair(boxes: List<BoxVM>) {
        AlertDialog
            .Builder(this)
            .setItems(boxes.map { x -> x.name }.toTypedArray()) { _, which ->
                presenter.pairWithBox(boxes[which].id)
            }
            .show()
    }

    private fun deleteDevice() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.delete_device_question)
        builder.setPositiveButton(R.string.delete){_, _ -> presenter.delete() }
        builder.setNegativeButton(R.string.no){_, _ -> }
        builder.create().show()
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
