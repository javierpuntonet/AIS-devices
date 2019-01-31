package pl.sviete.dom.devices.ui.mainview

import pl.sviete.dom.devices.aiscontrollers.models.PowerStatus
import pl.sviete.dom.devices.db.AisDeviceEntity
import pl.sviete.dom.devices.models.AisDevice
import pl.sviete.dom.devices.mvp.*

interface MainView {
    interface View : BaseView<Presenter> {
        fun refreshData(deviceEntity: List<DeviceViewModel>?)
        fun showDetail(id: Int)
    }

    interface Presenter : IPresenter<View> {
        fun loadView()
        fun addNewDevice(device: AisDevice)
        fun checkPermissionsGranted(requestCode: Int, grantResults: IntArray)
        fun showDeviceDetail(device: DeviceViewModel)
        fun toggleDeviceState(device: DeviceViewModel)
    }
}