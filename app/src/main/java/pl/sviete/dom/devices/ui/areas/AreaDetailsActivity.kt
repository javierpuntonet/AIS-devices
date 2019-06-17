package pl.sviete.dom.devices.ui.areas

import android.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_area_details.*
import pl.sviete.dom.devices.R

class AreaDetailsActivity : AppCompatActivity(), AreaDetailsView.View {

    override val presenter: AreaDetailsView.Presenter = AreaDetailsPresenter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_area_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btn_area_delete.setOnClickListener {
            deleteArea()
        }
        btn_area_save.setOnClickListener {
            presenter.save(txt_area_name.text.toString())
        }

        val area = intent.getSerializableExtra(ARG_AREA_ITEM) as AreaViewModel?
        presenter.loadView(area)
        if (area != null)
            txt_area_name.setText(area.name)
    }

    private fun deleteArea() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.delete_area_question)
        builder.setPositiveButton(R.string.delete){_, _ -> presenter.delete() }
        builder.setNegativeButton(R.string.no){_, _ -> }
        builder.create().show()
    }

    companion object {
        val ARG_AREA_ITEM: String = "AREA"
    }
}
