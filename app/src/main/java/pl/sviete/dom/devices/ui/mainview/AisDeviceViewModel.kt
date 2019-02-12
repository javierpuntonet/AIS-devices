package pl.sviete.dom.devices.ui.mainview

import pl.sviete.dom.devices.aiscontrollers.models.PowerStatus
import pl.sviete.dom.devices.models.AisDeviceType

data class DeviceViewModel(val uid: Int?, val name: String, val ip: String?) {
    var status = PowerStatus.Unknown
    var type: AisDeviceType? = null
    var isFounded: Boolean = false

    constructor(name: String, ip: String?, mstatus: PowerStatus, mtype: AisDeviceType?, misFounded: Boolean): this(null, name, ip) {
        status = mstatus
        type = mtype
        isFounded = misFounded
    }
}