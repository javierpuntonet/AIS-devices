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
    private val viewModel = MutableLiveData<List<DeviceViewModel>>()

    override fun loadView() {
        viewModel.observe(activity, Observer {
            view.refreshData(it)
        })

        mAisDeviceViewModel = ViewModelProviders.of(activity).get(AisDeviceViewModel::class.java)
        mAisDeviceViewModel.getAll().observe(activity, Observer { devices ->
            val list = mutableListOf<DeviceViewModel>()
            devices?.forEach {
                val nm= DeviceViewModel(it.uid!!, it.name, it.ip)
                val m = viewModel.value?.firstOrNull { x -> x.uid == it.uid }
                if (m != null)
                    nm.status = m.status
                list.add(nm)
            }
            viewModel.postValue(list)

            //view.refreshData(list.toList())
            checkDevicesStatus(list)
        })

        checkPermissions()
    }

    override fun addNewDevice(device: AisDevice, name: String){
        if (device != null) {
            val newDevice = AisDeviceEntity(null, name, device.mMac, null, null)
            mAisDeviceViewModel.insert(newDevice)
        }
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

    private fun checkDevicesStatus(devices: List<DeviceViewModel>){
        devices?.forEach {
            if (!it.ip.isNullOrEmpty()) {
                val service = AisFactory.makeSocketService(it.ip!!)
                GlobalScope.launch(Dispatchers.Main) {
                    var status = PowerStatus.UNKNOWN
                    val request = service.getPowerStatus()
                    try {
                        val response = request.await()
                        status = response.POWER
                    } catch (e: Exception) {
                        val ee = e
                    }
                    if (viewModel.value != null) {
                        val m = viewModel.value!!.first {x -> x.uid == it.uid }
                        if (m.status != status) {
                            m.status = status
                            viewModel.postValue(viewModel.value!!)
                        }
                    }
                }
            }
        }
    }
}