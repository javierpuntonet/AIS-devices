package pl.sviete.dom.devices.ui.adddevicecreator.apdata

import pl.sviete.dom.devices.mvp.BaseView
import pl.sviete.dom.devices.mvp.IPresenter

interface ApDataView {
    interface View : BaseView<Presenter> {
        fun setPassword(password: String)
        fun selectAP(apName: String?)
        fun setAPDataSource(aps: List<String>)
    }

    interface Presenter : IPresenter<View> {
        fun loadData(ssidToAdd: String)
        fun onAttach()
        fun onDetach()
        fun onCancel()
        fun onAccept(name: String, password: String, save: Boolean)
        fun onAPSelected(apName: String)
        fun refreshAPList()
    }

    interface OnAPDataAcceptListener{
        fun onAPDataCancel()
        fun onAPDataAccept(name: String, password: String)
    }
}