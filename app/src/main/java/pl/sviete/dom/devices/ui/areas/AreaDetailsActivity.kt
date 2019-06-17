package pl.sviete.dom.devices.ui.areas

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
            presenter.delete()
        }
        btn_area_save.setOnClickListener {
            presenter.save(txt_area_name.text.toString())
        }

        presenter.loadView(null)
    }
}
