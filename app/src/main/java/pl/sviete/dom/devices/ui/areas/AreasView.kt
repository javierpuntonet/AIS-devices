package pl.sviete.dom.devices.ui.areas

import pl.sviete.dom.devices.mvp.BaseView
import pl.sviete.dom.devices.mvp.IPresenter

interface AreasView {
    interface View : BaseView<Presenter> {
       fun refreshData(devices: List<AreaViewModel>?)
    }

    interface Presenter : IPresenter<View> {
        fun loadView()

    }
}