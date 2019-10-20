package pl.sviete.dom.devices.ui.adddevicecreator.aplist

import pl.sviete.dom.devices.mvp.BaseView
import pl.sviete.dom.devices.mvp.IPresenter

interface ApListView {
    interface View : BaseView<Presenter> {
        fun setData(list: List<AccessPointViewModel>)
    }

    interface Presenter : IPresenter<View> {
        fun loadView()
        fun onApSelected(ap: AccessPointViewModel)
        fun refreshApList()
        fun onStop()
        fun onAttach()
    }

    interface OnAPSelectedListener{
        fun onAPSelected(selectedAP: AccessPointViewModel)
    }
}

