package pl.sviete.dom.devices

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
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
import android.content.pm.PackageManager
import android.os.LocaleList
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import kotlinx.android.synthetic.main.content_main.*
import pl.sviete.dom.devices.db.AisDeviceEntity
import pl.sviete.dom.devices.db.AisDeviceViewModel
import pl.sviete.dom.devices.db.DataBase
import pl.sviete.dom.devices.db.Repository
import pl.sviete.dom.devices.models.AisDevice
import pl.sviete.dom.devices.models.AisDeviceType
import pl.sviete.dom.devices.ui.adddevicecreator.MainCreatorActivity
import android.widget.GridView
import pl.sviete.dom.devices.ui.mainview.MainGridAdapter


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val PERMISSIONS_REQUEST_LOCATION: Int = 111
    private lateinit var mAisAdapter: MainGridAdapter
    private var mAisList = ArrayList<AisDeviceEntity>()
    private lateinit var mAisDeviceViewModel: AisDeviceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mAisDeviceViewModel = ViewModelProviders.of(this).get(AisDeviceViewModel::class.java)
        mAisDeviceViewModel.allDevices.observe(this, Observer { devices ->
            // Update the cached copy of the words in the adapter.
            devices?.let {
                mAisList.addAll(it)
            }
            mAisAdapter.notifyDataSetChanged()
            showAddWelcomeButton()
        })

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        mAisAdapter = MainGridAdapter(this, mAisList)
        ais_device_list.adapter = mAisAdapter

        findViewById<Button>(R.id.btn_welcome_add).setOnClickListener{
            showCreator()
        }

        checkPermissions()
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
                val ais = data?.getSerializableExtra("aisdevice")
                val name = data?.getStringExtra("name")
                if (ais != null && ais is AisDevice) {
                    if (name != null)
                        ais.name = name
                    val newDevice = AisDeviceEntity(null, name, ais.mMac, null, null)
                    mAisDeviceViewModel.insert(newDevice)
                }
            }
        }
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

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSIONS_REQUEST_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted
                } else {
                    finish()
                }
                return
            }
        }
    }
}
