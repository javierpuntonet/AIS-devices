package pl.sviete.dom.devices.ui.devicedetails

import pl.sviete.dom.devices.db.AisDeviceEntity
import pl.sviete.dom.devices.mvp.BaseView
import pl.sviete.dom.devices.mvp.IPresenter

interface DeviceDetailsView {
    interface View : BaseView<Presenter> {
        fun showView(device: AisDeviceEntity)
        fun showNameValidationError(resId: Int)
        fun showIPValidationError(resId: Int)
        fun showSaveErrorInfo()
    }

    interface Presenter : IPresenter<View> {
        fun loadView(id: Long)
        fun saveView(name: String, ip: String): Boolean
        fun delete()
    }
}