package pl.sviete.dom.devices.ui.adddevicecreator.connectdevice

import pl.sviete.dom.devices.aiscontrollers.AisDeviceConfigurator
import pl.sviete.dom.devices.mvp.BaseView
import pl.sviete.dom.devices.mvp.IPresenter

interface ConnectDeviceView {
    interface View : BaseView<Presenter> {
        fun onPairError(errorCode: AisDeviceConfigurator.ErrorCode)
        fun setIconForDevice(iconResourceId: Int)
        fun onStep(step: ConnectStep)
    }

    interface Presenter : IPresenter<View> {
        fun onStop()
        fun pairDevice(deviceSsid: String, apName: String, apPassword: String, deviceName: String)
    }
}

enum class ConnectStep { ConnectToDevice, SendConfiguration, Waiting, NetworkScan }