package pl.sviete.dom.devices.ui.mainview

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import pl.sviete.dom.devices.R
import pl.sviete.dom.devices.SettingsActivity
import pl.sviete.dom.devices.models.AisDeviceType
import pl.sviete.dom.devices.ui.adddevicecreator.MainCreatorActivity
import pl.sviete.dom.devices.ui.areas.AreaViewModel
import pl.sviete.dom.devices.ui.areas.AreasActivity
import pl.sviete.dom.devices.ui.devicedetails.DeviceDetailsActivity
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import android.widget.TextView

class MainActivity : AppCompatActivity(), MainView.View, NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mAisAdapter: MainGridAdapter
    private var mAisList = ArrayList<DeviceViewModel>()
    private val mLock = ReentrantLock()
    private var mProgressCounter = 0

    override val presenter: MainView.Presenter = MainPresenter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        mAisAdapter = MainGridAdapter(mAisList, presenter)
        ais_device_list.adapter = mAisAdapter

        presenter.checkPermissions()

        findViewById<Button>(R.id.btn_welcome_add).setOnClickListener{
            showCreator()
        }

        presenter.loadView()

        main_swipe.setOnRefreshListener {
            presenter.clearCache()
            presenter.loadView()
            main_swipe.isRefreshing = false
        }

        spinner_main_areas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                (view as TextView).setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                val area = spinner_main_areas.selectedItem as AreaViewModel?
                if (area != null)
                    presenter.areaSelected(area)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.resumeView()
    }

    override fun onPause() {
        super.onPause()
        presenter.pauseView()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_net_scan -> {
                presenter.scanNetwork()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_add_creator -> {
                showCreator()
            }
            R.id.nav_areas -> {
                val intent = Intent(this, AreasActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MainCreatorActivity.CREATOR_REQUEST_CODE){
            /*data?.let {
                val name = it.getStringExtra(MainCreatorActivity.RESULT_NAME)
                val mac = it.getStringExtra(MainCreatorActivity.RESULT_MAC)
                val type = it.getSerializableExtra(MainCreatorActivity.RESULT_TYPE) as AisDeviceType
                presenter.addNewDevice(name, mac, type)
            }*/
        }
    }

    override fun refreshData(devices: List<DeviceViewModel>?) {
        devices?.let {
            mAisList.clear()
            mAisList.addAll(it.sorted())
        }
        mAisAdapter.notifyDataSetChanged()
        showAddWelcomeButton()
    }

    override fun refreshAreas(areas: List<AreaViewModel>){
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, areas)
        spinner_main_areas.adapter = adapter
    }

    override fun showDetail(id: Long) {
        val intent = Intent(this, DeviceDetailsActivity::class.java).apply {
            putExtra(DeviceDetailsActivity.ARG_DEVICE_ITEM_ID, id)
        }
        startActivity(intent)
    }

    override fun showProgress() {
        mLock.withLock {
            if (mProgressCounter <= 0) {
                progress_main.visibility = View.VISIBLE
                mProgressCounter = 1
            } else {
                mProgressCounter += 1
            }
        }
    }

    override fun hideProgress() {
         mLock.withLock {
            mProgressCounter -= 1
            if (mProgressCounter <= 0) {
                progress_main.visibility = View.GONE
                mProgressCounter = 0
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        presenter.checkPermissionsGranted(requestCode, grantResults)
    }

    private fun showCreator() {
        val intent = Intent(this, MainCreatorActivity::class.java)
        startActivityForResult(intent, MainCreatorActivity.CREATOR_REQUEST_CODE)
    }

    private fun showAddWelcomeButton() {
        if (mAisList.count() > 0)
        {
            welcome_text.visibility = View.GONE
            ais_device_list.visibility = View.VISIBLE
        }
        else
        {
            welcome_text.visibility = View.VISIBLE
            ais_device_list.visibility = View.GONE
        }
    }
}
