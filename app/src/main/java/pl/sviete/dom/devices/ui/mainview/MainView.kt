package pl.sviete.dom.devices.ui.mainview

import pl.sviete.dom.devices.models.AisDeviceType
import pl.sviete.dom.devices.mvp.*
import pl.sviete.dom.devices.ui.areas.AreaViewModel

interface MainView {
    interface View : BaseView<Presenter> {
        fun refreshData(devices: List<DeviceViewModel>?)
        fun showDetail(id: Long)
        fun showProgress()
        fun hideProgress()
        fun refreshAreas(areas: List<AreaViewModel>)
    }

    interface Presenter : IPresenter<View> {
        fun loadView()
        fun clearCache()
        fun checkPermissions()
        fun addNewDevice(name: String, mac: String, type: AisDeviceType)
        fun checkPermissionsGranted(requestCode: Int, grantResults: IntArray)
        fun showDeviceDetail(device: DeviceViewModel)
        fun toggleDeviceState(device: DeviceViewModel)
        fun scanNetwork()
        fun resumeView()
        fun pauseView()
        fun areaSelected(area: AreaViewModel)
    }
}