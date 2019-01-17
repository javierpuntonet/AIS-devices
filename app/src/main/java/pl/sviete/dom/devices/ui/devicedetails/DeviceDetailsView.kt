package pl.sviete.dom.devices.ui.devicedetails

import pl.sviete.dom.devices.mvp.BaseView
import pl.sviete.dom.devices.mvp.IPresenter

interface DeviceDetailsView {
    interface View : BaseView<Presenter> {

    }

    interface Presenter : IPresenter<View> {
        fun loadView()
    }
}