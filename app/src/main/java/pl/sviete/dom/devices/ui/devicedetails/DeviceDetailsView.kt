package pl.sviete.dom.devices.ui.devicedetails

import pl.sviete.dom.devices.db.AisDeviceEntity
import pl.sviete.dom.devices.mvp.BaseView
import pl.sviete.dom.devices.mvp.IPresenter
import pl.sviete.dom.devices.ui.areas.AreaViewModel

interface DeviceDetailsView {
    interface View : BaseView<Presenter> {
        fun showView(device: AisDeviceEntity)
        fun showNameValidationError(resId: Int)
        fun showIPValidationError(resId: Int)
        fun showSaveErrorInfo()
        fun setAreas(areas: List<AreaViewModel>, selectedIdx: Int)
        fun showNoBoxesMessage()
        fun showPairStatus(success: Boolean)
        fun selectBoxToPair(boxes: List<BoxVM>)
    }

    interface Presenter : IPresenter<View> {
        fun loadView(id: Long)
        fun saveView(name: String, ip: String, area: AreaViewModel?): Boolean
        fun delete()
        fun initPairWithBox()
        fun pairWithBox(boxId: String)
    }
}