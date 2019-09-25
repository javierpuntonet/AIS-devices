package pl.sviete.dom.devices.ui.adddevicecreator.connectdevice

import pl.sviete.dom.devices.aiscontrollers.AisDeviceConfigurator
import pl.sviete.dom.devices.models.AisDeviceType
import pl.sviete.dom.devices.mvp.BaseView
import pl.sviete.dom.devices.mvp.IPresenter
import pl.sviete.dom.devices.ui.adddevicecreator.area.CreatorAreaView

interface ConnectDeviceView {
    interface View : BaseView<Presenter> {
        fun onPairError(errorCode: AisDeviceConfigurator.ErrorCode)
        fun setIconForDevice(iconResourceId: Int)
        fun onStep(step: ConnectStep)
    }

    interface Presenter : IPresenter<View> {
        fun onStop()
        fun pairDevice(deviceSsid: String, apName: String, apPassword: String, deviceName: String)

        fun attach(listener: OnConnectDevice)
        fun detach()
    }

    interface OnConnectDevice {
        fun onConnectDeviceSuccess(deviceType: AisDeviceType?, deviceMAC: String)
        fun onConnectDeviceFaild()
    }
}

enum class ConnectStep { ConnectToDevice, SendConfiguration, Waiting, NetworkScan1, NetworkScan2, NetworkScan3 }