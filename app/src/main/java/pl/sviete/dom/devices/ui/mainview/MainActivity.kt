package pl.sviete.dom.devices.ui.mainview

import android.app.AlertDialog
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import pl.sviete.dom.devices.models.AisDeviceType
import pl.sviete.dom.devices.ui.adddevicecreator.MainCreatorActivity
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import android.widget.TextView
import pl.sviete.dom.devices.*
import pl.sviete.dom.devices.ui.details.DetailsFabric
import pl.sviete.dom.devices.BuildConfig
import pl.sviete.dom.devices.ui.areas.*
import pl.sviete.dom.devices.ui.areas.AreaViewModel.Companion.EMPTY
import java.lang.Exception


class MainActivity : AppCompatActivity(), MainView.View, NavigationView.OnNavigationItemSelectedListener {

    private val TAG = MainActivity::class.java.simpleName
    private lateinit var mAisAdapter: MainGridAdapter
    private var mAisList = ArrayList<DeviceViewModel>()
    private val mLock = ReentrantLock()
    private var mProgressCounter = 0

    override val presenter: MainView.Presenter = MainPresenter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler())
        setContentView(R.layout.activity_main)
        // set app version info
        nav_view.getHeaderView(0).findViewById<TextView>(R.id.app_version_name).text = BuildConfig.VERSION_NAME

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
            presenter.scanNetwork()
            main_swipe.isRefreshing = false
        }

        btn_select_area.setOnClickListener {
            showAreaSelector()
        }

        btn_main_add_area.setOnClickListener {
            AreaDialog.showAddNewArea(this, ::addNewArea)
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
        return when (item.itemId) {
            R.id.main_net_scan -> {
                presenter.scanNetwork()
                true
            }
            else -> super.onOptionsItemSelected(item)
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
            /*R.id.nav_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }*/
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return false
    }

    override fun refreshData(devices: List<DeviceViewModel>?) {
        devices?.let {
            mAisList.clear()
            mAisList.addAll(it.sorted())
        }
        mAisAdapter.notifyDataSetChanged()
        showAddWelcomeButton()
    }

    override fun showDetail(id: Long, type: AisDeviceType?) {
        DetailsFabric.openDetialsView(this, id, type)
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
        startActivity(intent)
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

    private fun showAreaSelector(){
        try {
            val areas = mutableListOf<AreaViewModel>()
            var selectedAreaIdx = 0
            areas.add(AreaViewModel(EMPTY, resources.getString(R.string.all_devices)))
            areas.addAll(presenter.getAreas().sorted())
            var area = presenter.getSelectedArea()
            if (area != null)
                selectedAreaIdx = areas.indexOf(area)
            val alert = AlertDialog.Builder(this)
            alert.setTitle(R.string.select_area)
            alert.setSingleChoiceItems(
                areas.map { x -> x.name }.toTypedArray(),
                selectedAreaIdx
            ) { dialog, item ->
                area = areas[item]
                if (area != null) {
                    btn_select_area.text = area!!.name
                    if (area!!.id == EMPTY)
                        presenter.areaSelect(null)
                    else
                        presenter.areaSelect(area!!.id)
                }
                dialog.dismiss()
            }
            alert
                .create()
                .show()
        }catch (ex: Exception){
            Log.e(TAG, "showAreaSelector", ex)
        }
    }

    private fun addNewArea(areaName: String){
        presenter.addArea(areaName)
        btn_select_area.text = areaName
    }
}
