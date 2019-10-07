package pl.sviete.dom.devices.ui.adddevicecreator.aplist

import android.support.v4.app.Fragment
import android.util.Log
import pl.sviete.dom.devices.helpers.AisDeviceHelper
import pl.sviete.dom.devices.mvp.BasePresenter
import pl.sviete.dom.devices.net.WiFiScanner
import pl.sviete.dom.devices.net.models.AccessPointInfo

class ApListPresenter(private val fragment: Fragment, override var view: ApListView.View)
    : BasePresenter<ApListView.View, ApListView.Presenter>(), ApListView.Presenter
    ,WiFiScanner.OnScanResultsListener{

    private val TAG = ApListPresenter::class.java.simpleName

    private var mWifi: WiFiScanner? = null
    private var mApSelectedListener: ApListView.OnAPSelectedListener? = null

    override fun loadView(){
        mWifi = WiFiScanner(fragment.context!!)
    }

    override fun onApSelected(ap: AccessPointViewModel, apList: List<AccessPointViewModel>){
        mWifi?.stopScan()
        mApSelectedListener?.onAPSelected(ap, apList)
    }

    override fun refreshApList() {
        mWifi!!.startScan(this)
    }

    override fun onStop(){
        mWifi!!.stopScan()
    }

    override fun onAttach(){
        if (fragment.context is ApListView.OnAPSelectedListener)
            mApSelectedListener = fragment.context as ApListView.OnAPSelectedListener
    }

    override fun onScanResults(scanResult: List<AccessPointInfo>) {
        val apList = mutableListOf<AccessPointViewModel>()
        try {
            scanResult.forEach {
                val isAis = AisDeviceHelper.apIsAisDevice(it.isOpen, it.mac)
                if (isAis)
                    apList.add(AccessPointViewModel(it.ssid, isAis))
            }
        }
        catch (ex: Exception){
            Log.e(TAG,"onScanResults: $ex")
        }
        finally {
            view.setData(apList)
        }
    }
}