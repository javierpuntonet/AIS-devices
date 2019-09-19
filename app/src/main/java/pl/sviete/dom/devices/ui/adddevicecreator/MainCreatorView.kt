package pl.sviete.dom.devices.ui.adddevicecreator

import android.support.v4.app.Fragment
import pl.sviete.dom.devices.models.AisDeviceType
import pl.sviete.dom.devices.mvp.BaseView
import pl.sviete.dom.devices.mvp.IPresenter
import pl.sviete.dom.devices.net.models.AccessPointInfo
import pl.sviete.dom.devices.ui.adddevicecreator.aplist.AccessPointViewModel

const val POS_START_CREATOR = 0
const val POS_AP_LIST = 1
const val POS_NAME = 2
const val POS_AP_DATA = 3
const val POS_CONNECT = 4
const val POS_AREA = 5

interface MainCreatorView {
    interface View : BaseView<Presenter> {
        fun changeFragment(position: Int, canBack: Boolean = false)
        fun backFragment()
        fun finishCreator()
    }

    interface Presenter : IPresenter<View> {
        fun getFragment(position: Int): Fragment?

        fun storeAP(selected: AccessPointViewModel, accessibleAPs: List<AccessPointViewModel>)
        fun storeName(name: String)
        fun storeAPData(apSsid: String, password: String)
        fun saveNewDevice(deviceType: AisDeviceType, deviceMAC: String)
    }

    interface ProgressBarManager {
        fun showProgressBar()
        fun hideProgressBar()
    }
}