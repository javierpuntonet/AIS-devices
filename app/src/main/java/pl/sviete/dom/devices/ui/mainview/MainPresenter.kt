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
import pl.sviete.dom.devices.aiscontrollers.AisFactory
import pl.sviete.dom.devices.db.AisDeviceEntity
import pl.sviete.dom.devices.db.AisDeviceViewModel
import pl.sviete.dom.devices.models.AisDevice
import pl.sviete.dom.devices.mvp.*
import retrofit2.HttpException

class MainPresenter(val activity: FragmentActivity, override var view: MainView.View) : BasePresenter<MainView.View, MainView.Presenter>(), MainView.Presenter {

    private val PERMISSIONS_REQUEST_LOCATION: Int = 111
    private lateinit var mAisDeviceViewModel: AisDeviceViewModel

    override fun loadView() {
        mAisDeviceViewModel = ViewModelProviders.of(activity).get(AisDeviceViewModel::class.java)
        mAisDeviceViewModel.getAll().observe(activity, Observer { devices ->
            view.refreshData(devices)
            //checkDevicesStatus(devices)
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

    override fun selectDeviceDetail(device: AisDeviceEntity) {
        view.showDetail(device.uid!!)
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSIONS_REQUEST_LOCATION)
        }
    }

    private fun checkDevicesStatus(devices: List<AisDeviceEntity>?){
        devices?.forEach {
            if (!it.ip.isNullOrEmpty()) {
                val service = AisFactory.makeSocketService(it.ip!!)
                GlobalScope.launch(Dispatchers.Main) {
                    val request = service.getPowerStatus()
                    try {
                        val response = request.await()
                        val a = response.POWER
                        if (a != null)
                        {

                        }
                    } catch (e: HttpException) {

                    } catch (e: Throwable) {

                    }
                }
            }
        }
    }
}