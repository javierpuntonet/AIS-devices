package pl.sviete.dom.devices.ui.devicedetails

import pl.sviete.dom.devices.db.AisDeviceEntity
import pl.sviete.dom.devices.mvp.BaseView
import pl.sviete.dom.devices.mvp.IPresenter

interface DeviceDetailsView {
    interface View : BaseView<Presenter> {
        fun showView(device: AisDeviceEntity)
    }

    interface Presenter : IPresenter<View> {
        fun loadView(id: Int)
        fun saveView(name: String, ip: String)
        fun delete()
    }
}