package pl.sviete.dom.devices.ui.mainview

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.sviete.dom.devices.aiscontrollers.AisDeviceRestController
import pl.sviete.dom.devices.db.AisDeviceEntity
import pl.sviete.dom.devices.db.AisDeviceViewModel
import pl.sviete.dom.devices.models.AisDeviceType
import pl.sviete.dom.devices.mvp.*
import pl.sviete.dom.devices.netscanner.FoundDeviceModel
import pl.sviete.dom.devices.netscanner.IScannerResult
import pl.sviete.dom.devices.netscanner.Scanner

class MainPresenter(val activity: FragmentActivity, override var view: MainView.View)
    : BasePresenter<MainView.View, MainView.Presenter>()
    , MainView.Presenter
    , IScannerResult {

    private val PERMISSIONS_REQUEST_LOCATION: Int = 111
    private lateinit var mAisDeviceViewModel: AisDeviceViewModel
    private var mAisList = ArrayList<DeviceViewModel>()
    private val mScanner = Scanner(activity, this)
    private var mEntities = emptyList<AisDeviceEntity>()

    override fun loadView() {
        view.showProgress()
        try {
            mScanner.repository.devices.observe(activity, Observer {
                refreshStatuses()
                refreshFounded(mScanner.repository.getFoundedDevices())
                refreshIps(mScanner.repository.getDevicesWithMAC())
                view.refreshData(mAisList)
            })

            mAisDeviceViewModel = ViewModelProviders.of(activity).get(AisDeviceViewModel::class.java)
            mAisDeviceViewModel.getAll().observe(activity, Observer { devices ->
                if (devices != null) {
                    mEntities = devices
                    refreshFromDB(devices)
                    refreshStatuses()
                    refreshFounded(mScanner.repository.getFoundedDevices())
                    view.refreshData(mAisList)
                    devices.filter { x -> !x.ip.isNullOrEmpty() }.forEach {
                        mScanner.add(it.ip!!, it.mac, false)
                    }
                }
            })
        }
        finally {
            view.hideProgress()
        }
    }

    override fun resumeView() {
        mScanner.scan()
    }

    override fun pauseView() {
        mScanner.stop()
    }

    override fun addNewDevice(name: String, mac: String, type: AisDeviceType){
        val newDevice = AisDeviceEntity(null, name, mac, null, type.value)
        mAisDeviceViewModel.insert(newDevice)
    }

    override fun scanNetwork() {
        view.showProgress()
        mScanner.runIpScanner()
    }

    override fun checkPermissionsGranted(requestCode: Int, grantResults: IntArray){
        when (requestCode) {
            PERMISSIONS_REQUEST_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted
                } else {
                    activity.finish()
                }
                return
            }
        }
    }

    override fun showDeviceDetail(device: DeviceViewModel) {
        if (device.isFounded) {
            mAisDeviceViewModel.insertionId.observe(activity, Observer { id ->
                if (id != null && id > 0)
                    view.showDetail(id)
            })
            mScanner.repository.deleteDevice(device.mac)
            val newDevice = AisDeviceEntity(null, device.name, device.mac, device.ip, device.type?.value)
            mAisDeviceViewModel.insert(newDevice)
        }
        else {
            view.showDetail(device.uid!!)
        }
    }

    override fun toggleDeviceState(device: DeviceViewModel) {
        if (!device.ip.isNullOrEmpty()) {
            GlobalScope.launch(Dispatchers.Main) {
                val status = AisDeviceRestController.toggleStatus(device.ip)
                if (status != null) {
                    mScanner.repository.setStatus(device.mac, status)
                }
            }
        }
    }

    override fun clearCache() {
        mScanner.repository.clear()
        mScanner.stop()
        mScanner.scan()
    }

    override fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSIONS_REQUEST_LOCATION)
        }
    }

    override fun ipScanFinished() {
        view.hideProgress()
    }

    private fun refreshFromDB(entities: List<AisDeviceEntity>) {
        val toRemove = mAisList.filter { x -> !x.isFounded }
        mAisList.removeAll(toRemove)
        entities.forEach {
            val device = DeviceViewModel(it.uid!!, it.name, it.ip, it.mac)
            if (it.type != null)
                device.type = AisDeviceType.fromInt(it.type!!)
            mAisList.add(device)
        }
    }

    private fun refreshStatuses(){
        mAisList.forEach {
            it.status = mScanner.repository.getStatus(it.mac)
        }
    }

    private fun refreshFounded(list: List<FoundDeviceModel>){
        val toRemove = mAisList.filter { x -> x.isFounded }
        mAisList.removeAll(toRemove)
        list.forEach {
            mAisList.add(DeviceViewModel(it.name!!, it.ip, it.mac!!, it.status!!, it.type, true))
        }
    }

    private fun refreshIps(list: List<FoundDeviceModel>){
        if (list.count() == 0) return
        mEntities.forEach {
            val founded = list.firstOrNull { x -> x.mac?.toUpperCase() == it.mac.toUpperCase() }
            if (founded != null && founded.ip != it.ip){
                it.ip = founded.ip
                mAisDeviceViewModel.update(it)
            }
        }
    }
}