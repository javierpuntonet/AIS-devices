package pl.sviete.dom.devices.ui.areas

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import pl.sviete.dom.devices.R
import kotlinx.android.synthetic.main.activity_areas.*
import android.widget.ArrayAdapter

class AreasActivity : AppCompatActivity(), AreasView.View {

    override val presenter: AreasView.Presenter = AreasPresenter(this, this)
    private lateinit var mAreasAdapter: ArrayAdapter<AreaViewModel>
    private var mAreaList = ArrayList<AreaViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_areas)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mAreasAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, mAreaList)
        list_areas.adapter = mAreasAdapter

        btn_add_area.setOnClickListener {
            val intent = Intent(this, AreaDetailsActivity::class.java)
            startActivity(intent)
        }

        presenter.loadView()
    }

    override fun refreshData(areas: List<AreaViewModel>?) {
        areas?.let {
            mAreaList.clear()
            mAreaList.addAll(it.sorted())
        }
        mAreasAdapter.notifyDataSetChanged()
    }
}
