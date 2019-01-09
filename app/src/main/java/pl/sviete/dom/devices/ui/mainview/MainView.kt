package pl.sviete.dom.devices.ui.mainview

import pl.sviete.dom.devices.db.AisDeviceEntity
import pl.sviete.dom.devices.models.AisDevice
import pl.sviete.dom.devices.mvp.*

interface MainView {
    interface View : BaseView<Presenter> {
        fun refreshData(deviceEntity: List<AisDeviceEntity>?)
    }

    interface Presenter : IPresenter<View> {
        fun loadView()
        fun addNewDevice(device: AisDevice, name: String)
        fun checkPermissionsGranted(requestCode: Int, grantResults: IntArray)
    }
}