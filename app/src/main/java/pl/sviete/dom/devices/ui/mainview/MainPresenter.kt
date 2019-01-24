package pl.sviete.dom.devices.ui.mainview

import android.Manifest
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.sviete.dom.devices.aiscontrollers.AisFactory
import pl.sviete.dom.devices.aiscontrollers.models.PowerStatus
import pl.sviete.dom.devices.db.AisDeviceEntity
import pl.sviete.dom.devices.db.AisDeviceViewModel
import pl.sviete.dom.devices.models.AisDevice
import pl.sviete.dom.devices.mvp.*
import retrofit2.HttpException
import java.lang.Exception

class MainPresenter(val activity: FragmentActivity, override var view: MainView.View) : BasePresenter<MainView.View, MainView.Presenter>(), MainView.Presenter {

    private val PERMISSIONS_REQUEST_LOCATION: Int = 111
    private lateinit var mAisDeviceViewModel: AisDeviceViewModel
    private var mAisList = ArrayList<DeviceViewModel>()

    override fun loadView() {
        DeviceStatusRepository.getInstance().statuses.observe(activity, Observer {
            refreshViewModel(null)
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

    override fun addNewDevice(device: AisDevice, name: String){
        val newDevice = AisDeviceEntity(null, name, device.mMac, null, null)
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

    override fun selectDeviceDetail(device: DeviceViewModel) {
        view.showDetail(device.uid)
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSIONS_REQUEST_LOCATION)
        }
    }

    private fun refreshViewModel(entities: List<AisDeviceEntity>?) {
        if (entities != null) {
            mAisList.clear()
            entities.forEach {
                mAisList.add(DeviceViewModel(it.uid!!, it.name, it.ip))
            }
        }
        mAisList.forEach {
            if (it.ip != null)
                it.status = DeviceStatusRepository.getInstance().get(it.ip)
        }
        view.refreshData(mAisList)
    }
}