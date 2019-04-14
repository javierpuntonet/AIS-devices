package pl.sviete.dom.devices.net.models

data class AccessPointInfo(val ssid: String, val isAis: Boolean): Comparable<AccessPointInfo> {

    override fun toString(): String {
        return ssid
    }

    override fun compareTo(other: AccessPointInfo): Int {
        val result = isAis.compareTo(other.isAis)
        if (result == 0)
            return ssid.compareTo(other.ssid)
        if (result == 1)
            return -1
        return 1
    }

    override fun equals(other: Any?): Boolean {
        val o = other as AccessPointInfo?
        if (o?.ssid == ssid) return true
        return false
    }
}