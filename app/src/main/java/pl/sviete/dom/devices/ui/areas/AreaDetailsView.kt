package pl.sviete.dom.devices.ui.areas

import pl.sviete.dom.devices.mvp.BaseView
import pl.sviete.dom.devices.mvp.IPresenter

class AreaDetailsView {
    interface View : BaseView<Presenter>

    interface Presenter : IPresenter<View> {
        fun loadView(area: AreaViewModel?)
        fun delete()
        fun save(name: String)
    }
}