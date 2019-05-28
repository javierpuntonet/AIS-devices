package pl.sviete.dom.devices.ui.adddevicecreator

import android.support.v7.app.AppCompatActivity
import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main_creator.*
import pl.sviete.dom.devices.R
import pl.sviete.dom.devices.net.models.AccessPointInfo
import java.lang.Exception
import pl.sviete.dom.devices.ui.adddevicecreator.connectdevice.ConnectDeviceFragment

private const val POS_START_CREATOR = 0
private const val POS_AP_LIST = 1
private const val POS_NAME = 2
private const val POS_AP_DATA = 3
private const val POS_CONNECT = 4

class MainCreatorActivity : AppCompatActivity(), StartCreatorFragment.OnNextStepListener, AplistCreatorFragment.OnAPSelectedListener
                            , ApDataCreatorFragment.OnAPDataAcceptListener, NameCreatorFragment.OnNameAcceptListener
                            , ProgressBarManager{

    private var mDeviceInfo: AccessPointInfo? = null
    private var mAccessibleDevices: List<AccessPointInfo>? = null
    private var mNewDeviceName: String? = null
    private var mAPName: String? = null
    private var mAPPassword: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_creator)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState != null) {
            return
        }

        val firstFragment = getFragment(POS_START_CREATOR)!!
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, firstFragment)
            .commit()
    }

    override fun onPause() {
        super.onPause()
        progressBar.visibility = View.GONE
    }

    override fun onStartDesigner() {
        changeFragment(POS_AP_LIST)
    }

    override fun onAPSelected(selectedAP : AccessPointInfo, accessibleAP: List<AccessPointInfo>){
        mDeviceInfo = selectedAP
        mAccessibleDevices = accessibleAP
        changeFragment(POS_NAME, true)
    }

    override fun onNameCancel(name: String) {
        mNewDeviceName = name
        supportFragmentManager.popBackStack()
    }

    override fun onNameAccept(name: String) {
        mNewDeviceName = name
        changeFragment(POS_AP_DATA, true)
    }

    override fun onAPDataCancel() {
        supportFragmentManager.popBackStack()
    }

    override fun onAPDataAccept(name: String, password: String) {
        mAPName = name
        mAPPassword = password
        changeFragment(POS_CONNECT, true)
    }

    companion object {
        const val CREATOR_REQUEST_CODE = 111
        const val RESULT_TYPE = "type"
        const val RESULT_NAME = "name"
        const val RESULT_MAC = "mac"
    }

    private fun changeFragment(position: Int, canBack: Boolean = false) {
        val newFragment = getFragment(position) ?: return
        val transaction = supportFragmentManager.beginTransaction()

        transaction.setCustomAnimations(R.anim.slide_out_right, R.anim.exit_to_left, R.anim.slide_in_left, R.anim.exit_to_right)
        transaction.replace(R.id.fragment_container, newFragment)
        if (canBack)
            transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun getFragment(position: Int): Fragment? {
        when (position) {
            POS_START_CREATOR -> return StartCreatorFragment.newInstance()
            POS_AP_LIST -> return AplistCreatorFragment.newInstance()
            POS_NAME -> {
                val bundle = Bundle()
                bundle.putString("APname", mDeviceInfo?.ssid)
                bundle.putString("defDeviceName", mNewDeviceName)
                val nameFragment = NameCreatorFragment.newInstance()
                nameFragment.arguments = bundle
                return nameFragment
            }
            POS_AP_DATA ->{
                val aps = mAccessibleDevices!!.filter { x -> x != mDeviceInfo }.map { x -> x.ssid }.toTypedArray()
                if (aps.isEmpty()) return null
                val bundle = Bundle()
                bundle.putStringArray("accessibleAP", aps)
                val fragment = ApDataCreatorFragment.newInstance()
                fragment.arguments = bundle
                return fragment
            }
            POS_CONNECT -> {
                return ConnectDeviceFragment.newInstance(mNewDeviceName!!, mDeviceInfo!!.ssid, mAPName!!, mAPPassword!!)
            }
        }
        throw Exception("Not implemented")
    }

    override fun show() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hide(){
        progressBar.visibility = View.GONE
    }
}

interface ProgressBarManager {
    fun show()
    fun hide()
}
