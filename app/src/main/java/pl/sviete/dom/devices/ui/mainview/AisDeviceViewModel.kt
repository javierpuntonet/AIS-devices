package pl.sviete.dom.devices.ui.mainview

import pl.sviete.dom.devices.aiscontrollers.models.PowerStatus

class DeviceViewModel(val uid: Int, val name: String, val ip: String?) {
    var status = PowerStatus.UNKNOWN
}