package pl.sviete.dom.devices.ui.boxdetails

import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.*
import kotlinx.android.synthetic.main.activity_box_details.*
import kotlinx.android.synthetic.main.activity_device_details.*
import pl.sviete.dom.devices.R
import pl.sviete.dom.devices.db.AisDeviceEntity
import java.lang.Exception

class BoxDetailsActivity:  AppCompatActivity(), BoxDetailsView.View {
    override val presenter: BoxDetailsView.Presenter = BoxDetailsPresenter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_box_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btn_save_box.setOnClickListener {
            try {
                progress_box_details.visibility = View.VISIBLE
                clearError()
                if (!presenter.saveView(txt_box_name.text.toString()))
                    progress_box_details.visibility = View.GONE
            }catch (e: Exception) {
                progress_box_details.visibility = View.GONE
            }
        }

        btn_delete_box.setOnClickListener {
            deleteBox()
        }

        val detailId = intent.getLongExtra(ARG_BOX_ITEM_ID, 0)
        presenter.loadView(detailId)
    }

    override fun showView(device: AisDeviceEntity) {
        txt_box_name.setText(device.name)
    }

    private fun deleteBox() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.delete_device_question)
        builder.setPositiveButton(R.string.delete){ _, _ -> presenter.delete() }
        builder.setNegativeButton(R.string.no){ _, _ -> }
        builder.create().show()
    }

    override fun showNameValidationError(resId: Int) {
        txt_box_name.error = resources.getString(resId)
    }

    private fun clearError(){
        txt_box_name.error = null
    }

    companion object {
        val ARG_BOX_ITEM_ID: String = "BOX_ID"
    }
}