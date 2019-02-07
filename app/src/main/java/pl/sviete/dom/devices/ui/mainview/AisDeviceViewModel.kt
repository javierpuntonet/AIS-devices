package pl.sviete.dom.devices.ui.mainview

import pl.sviete.dom.devices.aiscontrollers.models.PowerStatus
import pl.sviete.dom.devices.models.AisDeviceType

class DeviceViewModel(val uid: Int, val name: String, val ip: String?) {
    var status = PowerStatus.Unknown
    var type: AisDeviceType? = null
}