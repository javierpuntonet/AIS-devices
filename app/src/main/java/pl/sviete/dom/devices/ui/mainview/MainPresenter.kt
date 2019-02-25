package pl.sviete.dom.devices.ui.mainview

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.sviete.dom.devices.aiscontrollers.AisDeviceController
import pl.sviete.dom.devices.db.AisDeviceEntity
import pl.sviete.dom.devices.db.AisDeviceViewModel
import pl.sviete.dom.devices.models.AisDevice
import pl.sviete.dom.devices.models.AisDeviceType
import pl.sviete.dom.devices.mvp.*
import pl.sviete.dom.devices.netscanner.FoundDeviceModel
import pl.sviete.dom.devices.netscanner.FoundDeviceRepository
import pl.sviete.dom.devices.netscanner.IScannerResult
import pl.sviete.dom.devices.netscanner.Scanner

class MainPresenter(val activity: FragmentActivity, override var view: MainView.View) : BasePresenter<MainView.View, MainView.Presenter>(), MainView.Presenter
    , IScannerResult {

    private val PERMISSIONS_REQUEST_LOCATION: Int = 111
    private lateinit var mAisDeviceViewModel: AisDeviceViewModel
    private var mAisList = ArrayList<DeviceViewModel>()
    private val mScanner = Scanner(activity, this)

    override fun loadView() {
        view.showProgress()
        try {
            DeviceStatusRepository.getInstance().statuses.observe(activity, Observer {
                refreshStatuses()
            })
            FoundDeviceRepository.getInstance().devices.observe(activity, Observer {
                refreshFounded(FoundDeviceRepository.getInstance().getFoundedAisDevices())
            })

            mAisDeviceViewModel = ViewModelProviders.of(activity).get(AisDeviceViewModel::class.java)
            mAisDeviceViewModel.getAll().observe(activity, Observer { devices ->
                if (devices != null) {
                    refreshFromDB(devices)
                    devices.filter { x -> !x.ip.isNullOrEmpty() }.forEach {
                        if (DeviceStatusRepository.getInstance().add(it.ip!!))
                            getDeviceStatus(it.ip!!)
                    }
                }
            })

            Handler().postDelayed({
                scanNet()
            }, 1000)
        }
        finally {
            view.hideProgress()
        }
    }

    override fun addNewDevice(device: AisDevice){
        val newDevice = AisDeviceEntity(null, device.name!!, device.mMac, null, device.type?.value)
        mAisDeviceViewModel.insert(newDevice)
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
        if (!device.isFounded)
            view.showDetail(device.uid!!)
    }

    override fun toggleDeviceState(device: DeviceViewModel) {
        if (!device.ip.isNullOrEmpty()) {
            GlobalScope.launch(Dispatchers.Main) {
                val status = AisDeviceController.toggleStatus(device.ip)
                if (status != null) {
                    DeviceStatusRepository.getInstance().set(device.ip, status)
                }
            }
        }
    }

    override fun clearCache() {
        DeviceStatusRepository.getInstance().clear()
        FoundDeviceRepository.getInstance().clear()
    }

    override fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSIONS_REQUEST_LOCATION)
        }
    }

    override fun scanFinished() {
        view.hideProgress()
    }

    private fun scanNet(){
        view.showProgress()
        mScanner.scan()
    }

    private fun refreshFromDB(entities: List<AisDeviceEntity>) {
        val toRemove = mAisList.filter { x -> !x.isFounded }
        mAisList.removeAll(toRemove)
        entities.forEach {
            val device = DeviceViewModel(it.uid!!, it.name, it.ip)
            if (it.type != null)
                device.type = AisDeviceType.fromInt(it.type!!)
            mAisList.add(device)
        }
        view.refreshData(mAisList)
    }

    private fun refreshStatuses(){
        mAisList.forEach {
            if (it.ip != null)
                it.status = DeviceStatusRepository.getInstance().get(it.ip)
        }
        view.refreshData(mAisList)
    }

    private fun refreshFounded(list: List<FoundDeviceModel>){
        val toRemove = mAisList.filter { x -> x.isFounded }
        mAisList.removeAll(toRemove)
        list.forEach {
            mAisList.add(DeviceViewModel(it.name!!, it.ip, it.status!!, it.type, true))
        }
        view.refreshData(mAisList)
    }

    private fun getDeviceStatus(ip: String){
        GlobalScope.launch(Dispatchers.Main) {
            try {
                view.showProgress()
                val status = AisDeviceController.getPowerStatus(ip)
                if (status != null) {
                    DeviceStatusRepository.getInstance().set(ip, status)
                }
            } finally {
                view.hideProgress()
            }
        }
    }
}