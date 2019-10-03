package pl.sviete.dom.devices.ui.mainview

import android.Manifest
import android.arch.lifecycle.*
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.sviete.dom.devices.R
import pl.sviete.dom.devices.aiscontrollers.AisDeviceRestController
import pl.sviete.dom.devices.aiscontrollers.models.PowerStatus
import pl.sviete.dom.devices.db.*
import pl.sviete.dom.devices.models.AisDeviceType
import pl.sviete.dom.devices.mvp.*
import pl.sviete.dom.devices.netscanner.*
import pl.sviete.dom.devices.ui.areas.AreaViewModel

class MainPresenter(val activity: FragmentActivity, override var view: MainView.View)
    : BasePresenter<MainView.View, MainView.Presenter>()
    , MainView.Presenter
    , IScannerResult {

    private val TAG = MainPresenter::class.java.simpleName
    private val PERMISSIONS_REQUEST_LOCATION: Int = 111
    private lateinit var mAisDeviceViewModel: AisDeviceViewModel
    private var mAisList = ArrayList<DeviceViewModel>()
    private val mScanner = Scanner(activity, this)
    private var mEntities = emptyList<AisDeviceEntity>()
    private var mSelectedArea: AreaViewModel? = null

    override fun loadView() {
        view.showProgress()
        try {
            mScanner.devices.liveData.observe(activity, Observer {
                refreshStatuses()
                refreshFoundedDevices(mScanner.devices.getFoundedDevices())
                refreshDeviceIps(mScanner.devices.getDevicesWithMAC())
                refreshStatuses()
                view.refreshData(mAisList)
            })

            mScanner.boxes.liveData.observe(activity, Observer {
                if (it != null) {
                    refreshFoundedBoxes(it.filter { x -> x.founded })
                    refreshBoxIps(mScanner.boxes.getFoundBoxes())
                    refreshStatuses()
                    view.refreshData(mAisList)
                }
            })

            mAisDeviceViewModel = ViewModelProviders.of(activity).get(AisDeviceViewModel::class.java)
            mAisDeviceViewModel.getAll().observe(activity, Observer { devices ->
                if (devices != null) {
                    mEntities = devices
                    refreshFromDB(devices)
                    refreshStatuses()
                    refreshFoundedDevices(mScanner.devices.getFoundedDevices())
                    view.refreshData(mAisList)
                    devices.filter { x -> !x.ip.isNullOrEmpty() }.forEach {
                        if (it.type != null && it.type!! == AisDeviceType.Box.value)
                            mScanner.addBox(it.ip!!, it.mac, it.name)
                        else
                            mScanner.addDevice(it.ip!!, it.mac, false)
                    }
                }
            })

            val areasViewModel = ViewModelProviders.of(activity).get(AreasViewModel::class.java)
            areasViewModel.getAll().observe(activity, Observer {
                val areas = mutableListOf<AreaViewModel>()
                areas.add(AreaViewModel(AreaViewModel.EMPTY, activity.resources.getString(R.string.None)))
                it?.forEach { a ->
                    areas.add(AreaViewModel(a.uid!!, a.name))
                }
                view.refreshAreas(areas)
            })
        }
        finally {
            view.hideProgress()
        }
    }

    override fun resumeView() {

    }

    override fun pauseView() {

    }

    override fun areaSelected(area: AreaViewModel){
        if (!area.isEmpty)
            mSelectedArea = area
        else
            mSelectedArea = null

        refreshFromDB(mEntities)
        refreshStatuses()
        view.refreshData(mAisList)
    }

    override fun scanNetwork() {
        if (mScanner.runIpScanner())
            view.showProgress()
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

    override fun showDeviceDetails(device: DeviceViewModel) {
        if (device.isFounded) {
            mAisDeviceViewModel.insertionId.observe(activity, Observer { id ->
                if (id != null && id > 0) {
                    view.showDetail(id, device.type)
                    mAisDeviceViewModel.insertionId.removeObservers(activity)
                }
            })
            if (device.type == AisDeviceType.Box)
                mScanner.boxes.deleteBox(device.mac)
            else
                mScanner.devices.deleteDevice(device.mac)
            val newDevice = AisDeviceEntity(null, device.name, device.mac, device.ip, device.type?.value)
            mAisDeviceViewModel.insert(newDevice)
        }
        else {
            view.showDetail(device.uid!!, device.type)
        }
    }

    override fun deviceClick(device: DeviceViewModel) {
        if (!device.ip.isNullOrEmpty()) {
            if (device.type == AisDeviceType.Box){
                val url = "http://${device.ip}:8180"
                activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
            else {
                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        val status = AisDeviceRestController.toggleStatus(device.ip)
                        if (status != null) {
                            mScanner.devices.setStatus(device.mac, status)
                        }
                    }catch (ex: Exception){
                        Log.e(TAG, "deviceClick,toggleStatus: $ex")
                    }
                }
            }
        }
    }

    override fun clearCache() {
        mScanner.devices.clear()
        mScanner.runIpScanner()
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
        //clear actual list from elements from DB
        val toRemove = mAisList.filter { x -> !x.isFounded }
        mAisList.removeAll(toRemove)
        //add again to list
        entities.forEach {
            if (it.areaId == mSelectedArea?.id) {
                val device = DeviceViewModel(it.uid!!, it.name, it.ip, it.mac)
                if (it.type != null)
                    device.type = AisDeviceType.fromInt(it.type!!)
                mAisList.add(device)
            }
        }
    }

    private fun refreshStatuses(){
        mAisList.forEach {
            it.status = if (it.type == AisDeviceType.Box)
                mScanner.boxes.getStatus(it.mac)
            else
                mScanner.devices.getStatus(it.mac)
        }
    }

    private fun refreshFoundedDevices(list: List<FoundDeviceModel>){
        val toRemove = mAisList.filter { x -> x.type != AisDeviceType.Box && x.isFounded }
        mAisList.removeAll(toRemove)
        list.forEach {
            mAisList.add(DeviceViewModel(it.name!!, it.ip, it.mac!!, it.status!!, it.type, true))
        }
    }

    private fun refreshFoundedBoxes(list: List<BoxModel>){
        val toRemove = mAisList.filter { x -> x.type == AisDeviceType.Box && x.isFounded }
        mAisList.removeAll(toRemove)
        list.forEach {
            mAisList.add(DeviceViewModel(it.name, it.ip, it.gateId, PowerStatus.On, AisDeviceType.Box, true))
        }
    }

    private fun refreshDeviceIps(list: List<FoundDeviceModel>){
        if (list.count() == 0) return
        mEntities.forEach {
            val founded = list.firstOrNull { x -> x.mac?.toUpperCase() == it.mac.toUpperCase() }
            if (founded != null && founded.ip != it.ip){
                it.ip = founded.ip
                mAisDeviceViewModel.update(it)
            }
        }
    }

    private fun refreshBoxIps(list: List<BoxModel>){
        if (list.count() == 0) return
        list.forEach {
            val founded = mEntities.firstOrNull { x -> x.type == AisDeviceType.Box.value && x.mac.toUpperCase() == it.gateId.toUpperCase() }
            if (founded != null && founded.ip != it.ip){
                founded.ip = it.ip
                mAisDeviceViewModel.update(founded)
            }
        }
    }
}