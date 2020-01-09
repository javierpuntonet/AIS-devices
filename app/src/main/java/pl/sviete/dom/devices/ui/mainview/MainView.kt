package pl.sviete.dom.devices.ui.mainview

import pl.sviete.dom.devices.models.AisDeviceType
import pl.sviete.dom.devices.mvp.*
import pl.sviete.dom.devices.ui.areas.AreaViewModel

interface MainView {
    interface View : BaseView<Presenter> {
        fun refreshData(devices: List<DeviceViewModel>?)
        fun showDetail(id: Long, type: AisDeviceType?)
        fun showProgress()
        fun hideProgress()
    }

    interface Presenter : IPresenter<View> {
        fun loadView()
        fun clearCache()
        fun checkPermissions()
        fun checkPermissionsGranted(requestCode: Int, grantResults: IntArray)
        fun showDeviceDetails(device: DeviceViewModel)
        fun deviceClick(device: DeviceViewModel)
        fun scanNetwork()
        fun resumeView()
        fun pauseView()

        fun areaSelect(areaId: Long?)
        fun getAreas():List<AreaViewModel>
        fun getSelectedArea(): AreaViewModel?
        fun addArea(areaName: String)

    }
}