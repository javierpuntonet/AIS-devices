package pl.sviete.dom.devices.ui.adddevicecreator

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main_creator.*
import pl.sviete.dom.devices.R
import pl.sviete.dom.devices.models.AisDeviceType
import pl.sviete.dom.devices.net.models.AccessPointInfo
import pl.sviete.dom.devices.ui.adddevicecreator.apdata.ApDataCreatorFragment
import pl.sviete.dom.devices.ui.adddevicecreator.aplist.AplistCreatorFragment
import pl.sviete.dom.devices.ui.adddevicecreator.area.CreatorAreaView
import pl.sviete.dom.devices.ui.adddevicecreator.connectdevice.ConnectDeviceView
import pl.sviete.dom.devices.ui.adddevicecreator.namecreator.NameCreatorFragment
import pl.sviete.dom.devices.ui.adddevicecreator.startcreator.StartCreatorFragment


class MainCreatorActivity : AppCompatActivity(), MainCreatorView.View
                            , MainCreatorView.ProgressBarManager
    , StartCreatorFragment.OnNextStepListener, AplistCreatorFragment.OnAPSelectedListener
    , ApDataCreatorFragment.OnAPDataAcceptListener, NameCreatorFragment.OnNameAcceptListener
    , ConnectDeviceView.OnConnectDevice, CreatorAreaView.OnFinishCreatorArea{
    override val presenter: MainCreatorView.Presenter= MainCreatorPresenter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_creator)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState != null) {
            return
        }

        val firstFragment = presenter.getFragment(POS_START_CREATOR)!!
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, firstFragment)
            .commit()
    }

    override fun onPause() {
        super.onPause()
        hideProgressBar()
    }

    override fun onStartDesigner() {
        changeFragment(POS_AP_LIST)
    }

    override fun onAPSelected(selectedAP : AccessPointInfo, accessibleAP: List<AccessPointInfo>){
        presenter.storeAP(selectedAP, accessibleAP)
        changeFragment(POS_NAME, true)
    }

    override fun onNameCancel(name: String) {
        presenter.storeName(name)
        backFragment()
    }

    override fun onNameAccept(name: String) {
        presenter.storeName(name)
        changeFragment(POS_AP_DATA, true)
    }

    override fun onAPDataCancel() {
        backFragment()
    }

    override fun onAPDataAccept(name: String, password: String) {
        presenter.storeAPData(name, password)
        changeFragment(POS_CONNECT, true)
    }

    override fun onConnectDeviceFaild() {
        backFragment()
    }

    override fun onConnectDeviceSuccess(deviceType: AisDeviceType, deviceMAC: String) {
        presenter.saveNewDevice(deviceType, deviceMAC)
    }

    override fun onAreaFinish() {
        finishCreator()
    }

    override fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgressBar(){
        progressBar.visibility = View.GONE
    }

    override fun backFragment(){
        supportFragmentManager.popBackStack()
    }

    override fun changeFragment(position: Int, canBack: Boolean) {
        val newFragment = presenter.getFragment(position) ?: return
        val transaction = supportFragmentManager.beginTransaction()

        transaction.setCustomAnimations(R.anim.slide_out_right, R.anim.exit_to_left, R.anim.slide_in_left, R.anim.exit_to_right)
        transaction.replace(R.id.fragment_container, newFragment)
        if (canBack)
            transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun finishCreator(){
        val intentResult = Intent()
        //intentResult.putExtra(MainCreatorActivity.RESULT_NAME, mNewDeviceName)
        //intentResult.putExtra(MainCreatorActivity.RESULT_MAC, mNewDeviceMAC)
       // intentResult.putExtra(MainCreatorActivity.RESULT_TYPE, mNewDeviceType)
        setResult(MainCreatorActivity.CREATOR_REQUEST_CODE, intentResult)
        runOnUiThread {
            finish()
        }
    }

    companion object {
        const val CREATOR_REQUEST_CODE = 111
        const val RESULT_TYPE = "type"
        const val RESULT_NAME = "name"
        const val RESULT_MAC = "mac"
    }
}


