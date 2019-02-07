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
import pl.sviete.dom.devices.aiscontrollers.AisDeviceController
import pl.sviete.dom.devices.db.AisDeviceEntity
import pl.sviete.dom.devices.db.AisDeviceViewModel
import pl.sviete.dom.devices.models.AisDevice
import pl.sviete.dom.devices.models.AisDeviceType
import pl.sviete.dom.devices.mvp.*

class MainPresenter(val activity: FragmentActivity, override var view: MainView.View) : BasePresenter<MainView.View, MainView.Presenter>(), MainView.Presenter {


    private val PERMISSIONS_REQUEST_LOCATION: Int = 111
    private lateinit var mAisDeviceViewModel: AisDeviceViewModel
    private var mAisList = ArrayList<DeviceViewModel>()

    override fun loadView() {
        DeviceStatusRepository.getInstance().statuses.observe(activity, Observer {
            refreshViewModel()
        })

        mAisDeviceViewModel = ViewModelProviders.of(activity).get(AisDeviceViewModel::class.java)
        mAisDeviceViewModel.getAll().observe(activity, Observer { devices ->
            refreshViewModel(devices)
            devices?.filter { x -> !x.ip.isNullOrEmpty() }?.forEach {
                DeviceStatusRepository.getInstance().add(it.ip!!)
            }
        })

        checkPermissions()
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
        view.showDetail(device.uid)
    }

    override fun toggleDeviceState(device: DeviceViewModel) {
        if (!device.ip.isNullOrEmpty()) {
            GlobalScope.launch(Dispatchers.Main) {
                val status = AisDeviceController.toggleStatus(device.ip)
                if (status != null) {
                    DeviceStatusRepository.getInstance().set(device.ip, status)
                    refreshViewModel()
                }
            }
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSIONS_REQUEST_LOCATION)
        }
    }

    private fun refreshViewModel(entities: List<AisDeviceEntity>? = null) {
        if (entities != null) {
            mAisList.clear()
            entities.forEach {
                val device = DeviceViewModel(it.uid!!, it.name, it.ip)
                if (it.type != null)
                    device.type = AisDeviceType.fromInt(it.type!!)
                mAisList.add(device)
            }
        }
        mAisList.forEach {
            if (it.ip != null)
                it.status = DeviceStatusRepository.getInstance().get(it.ip)
        }
        view.refreshData(mAisList)
    }
}