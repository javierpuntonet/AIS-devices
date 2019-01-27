package pl.sviete.dom.devices

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import android.content.Intent
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.content_main.*
import pl.sviete.dom.devices.models.AisDevice
import pl.sviete.dom.devices.ui.adddevicecreator.MainCreatorActivity
import pl.sviete.dom.devices.ui.devicedetails.DeviceDetailsActivity
import pl.sviete.dom.devices.ui.mainview.*

class MainActivity : AppCompatActivity(), MainView.View, NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mAisAdapter: MainGridAdapter
    private var mAisList = ArrayList<DeviceViewModel>()

    override val presenter: MainView.Presenter = MainPresenter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        mAisAdapter = MainGridAdapter(mAisList, presenter)
        ais_device_list.adapter = mAisAdapter

        findViewById<Button>(R.id.btn_welcome_add).setOnClickListener{
            showCreator()
        }

        presenter.loadView()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_add_creator -> {
                showCreator()
            }
            R.id.nav_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MainCreatorActivity.CREATOR_REQUEST_CODE){
            data?.let {
                val ais = it.getSerializableExtra("aisdevice")
                val name = it.getStringExtra("name")
                presenter.addNewDevice(ais as AisDevice, name)
            }
        }
    }

    override fun refreshData(devices: List<DeviceViewModel>?) {
        devices?.let {
            mAisList.clear()
            mAisList.addAll(it)
        }
        mAisAdapter.notifyDataSetChanged()
        showAddWelcomeButton()
    }

    override fun showDetail(id: Int) {
        val intent = Intent(this, DeviceDetailsActivity::class.java).apply {
            putExtra(DeviceDetailsActivity.ARG_DEVICE_ITEM_ID, id)
        }
        startActivity(intent)
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
        if (mAisList != null && mAisList!!.count() > 0)
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
