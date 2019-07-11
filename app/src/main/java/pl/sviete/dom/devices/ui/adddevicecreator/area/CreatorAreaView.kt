package pl.sviete.dom.devices.ui.adddevicecreator.area

import pl.sviete.dom.devices.mvp.BaseView
import pl.sviete.dom.devices.mvp.IPresenter
import pl.sviete.dom.devices.ui.areas.AreaViewModel

class CreatorAreaView {
    interface View : BaseView<Presenter> {
        fun refreshAreas(areas: List<AreaViewModel>, selectArea: Long)
        fun selectArea(selectArea: Long)
    }

    interface Presenter : IPresenter<View> {
        fun storeInitData(deviceId: Long)
        fun loadView()
        fun addArea(areaName: String)
        fun finishClick(area: AreaViewModel?)

        fun attach(listener: OnFinishCreatorArea)
        fun detach()
    }

    interface OnFinishCreatorArea {
        fun onAreaFinish()
    }
}