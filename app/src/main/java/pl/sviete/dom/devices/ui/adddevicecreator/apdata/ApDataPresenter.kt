package pl.sviete.dom.devices.ui.adddevicecreator.apdata

import android.content.Context
import android.support.v4.app.Fragment
import pl.sviete.dom.devices.mvp.BasePresenter
import pl.sviete.dom.devices.net.WiFiScanner
import pl.sviete.dom.devices.net.models.AccessPointInfo
import pl.sviete.dom.devices.ui.adddevicecreator.MainCreatorView

class ApDataPresenter(private val fragment: Fragment, override var view: ApDataView.View)
    : BasePresenter<ApDataView.View, ApDataView.Presenter>(), ApDataView.Presenter
    , WiFiScanner.OnScanResultsListener {

    private val AP_NAME = "ap_name"
    private val AP_PASSWORD = "password"
    private lateinit var mSsiToAdd: String
    private var mAPDataAcceptListener: ApDataView.OnAPDataAcceptListener? = null
    private var mProgressBarManager: MainCreatorView.ProgressBarManager? = null
    private var mAPName: String? = null
    private var mPassword: String? = null
    private var mWiFi : WiFiScanner? = null

    override fun loadData(ssidToAdd: String){
        mSsiToAdd = ssidToAdd
        val pref = fragment.activity?.getPreferences(Context.MODE_PRIVATE)!!
        mAPName = pref.getString(AP_NAME, null)
        mPassword = pref.getString(AP_PASSWORD, null)

        mWiFi = WiFiScanner(fragment.context!!)
        if (mAPName == null){
            mAPName = mWiFi!!.getCurrentAccessPointName()
        }
        mWiFi!!.startScan(this)
    }

    override fun onCancel() {
        mAPDataAcceptListener?.onAPDataCancel()
    }
    override fun onAccept(name: String, password: String, save: Boolean){
        if (save)
            savePassword(name, password)

        mAPDataAcceptListener?.onAPDataAccept(name, password)
    }

    override fun onAttach(){
        if (fragment.context is ApDataView.OnAPDataAcceptListener)
            mAPDataAcceptListener = fragment.context as ApDataView.OnAPDataAcceptListener
        if (fragment.activity is MainCreatorView.ProgressBarManager)
            mProgressBarManager = fragment.context as MainCreatorView.ProgressBarManager
    }

    override fun onDetach() {
        mWiFi?.stopScan()
    }

    override fun onAPSelected(apName: String){
        if (!mPassword.isNullOrEmpty() && mAPName == apName) {
            view.setPassword(mPassword!!)
        }
    }

    override fun onScanResults(scanResult: List<AccessPointInfo>) {
        view.setAPDataSource(scanResult.filter { x -> x.ssid != mSsiToAdd }.map { it.ssid })
        view.selectAP(mAPName)
        mProgressBarManager?.hideProgressBar()
    }

    override fun refreshAPList(){
        mProgressBarManager?.showProgressBar()
        mWiFi!!.startScan(this)
    }

    private fun savePassword(apName: String, password: String){
        val sharedPref = fragment.activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString(AP_NAME, apName)
            putString(AP_PASSWORD, password)
            apply()
        }
        mAPName = apName
        mPassword = password
    }
}