package pl.sviete.dom.devices.ui.boxdetails

import pl.sviete.dom.devices.db.AisDeviceEntity
import pl.sviete.dom.devices.mvp.BaseView
import pl.sviete.dom.devices.mvp.IPresenter

interface BoxDetailsView {
    interface View : BaseView<Presenter> {
        fun showView(device: AisDeviceEntity)
        fun showNameValidationError(resId: Int)
    }

    interface Presenter : IPresenter<View> {
        fun loadView(id: Long)
        fun saveView(name: String): Boolean
        fun delete()
    }
}