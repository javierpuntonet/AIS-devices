package pl.sviete.dom.devices.net.models

data class AccessPointInfo(val ssid: String, val isOpen: Boolean, val mac: String) {

    override fun toString(): String {
        return ssid
    }

    override fun equals(other: Any?): Boolean {
        val o = other as AccessPointInfo?
        if (o?.ssid == ssid) return true
        return false
    }

    override fun hashCode(): Int {
        return ssid.hashCode()
    }
}