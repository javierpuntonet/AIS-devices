package pl.sviete.dom.devices.ui.mainview

import pl.sviete.dom.devices.aiscontrollers.models.PowerStatus
import pl.sviete.dom.devices.models.AisDeviceType

data class DeviceViewModel(val uid: Long?, val name: String, val ip: String?, val mac: String) : Comparable<DeviceViewModel> {

    var status = PowerStatus.Unknown
    var type: AisDeviceType? = null
    var isFounded: Boolean = false

    constructor(name: String, ip: String?, mac: String, mstatus: PowerStatus, mtype: AisDeviceType?, misFounded: Boolean)
            : this(null, name, ip, mac) {
        status = mstatus
        type = mtype
        isFounded = misFounded
    }

    override fun compareTo(other: DeviceViewModel): Int {
        if (this.isFounded > other.isFounded) {
            return 1
        } else if (this.isFounded < other.isFounded) {
            return -1
        }
        if (this.isFounded == other.isFounded) {
            return this.name.compareTo(other.name, true)
        }
        return 0
    }
}