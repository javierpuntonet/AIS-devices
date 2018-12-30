package pl.sviete.dom.devices.models

import java.io.Serializable

class AisDevice(mac: String): Serializable {

    val mMac = mac
    var id: Int? = null
    var name: String? = null
    var ip: String? = null
    var type: AisDeviceType? = null

    override fun toString(): String {
        return "$name, MAC:$mMac"
    }
}