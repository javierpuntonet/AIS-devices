package pl.sviete.dom.devices.ui.adddevicecreator.area

import pl.sviete.dom.devices.mvp.BaseView
import pl.sviete.dom.devices.mvp.IPresenter

class CreatorAreaView {
    interface View : BaseView<Presenter> {
    }

    interface Presenter : IPresenter<View> {
    }
}