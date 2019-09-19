package pl.sviete.dom.devices.ui.adddevicecreator.aplist

data class AccessPointViewModel(val ssid: String, val isAis: Boolean): Comparable<AccessPointViewModel> {

    override fun compareTo(other: AccessPointViewModel): Int {
        val result = isAis.compareTo(other.isAis)
        if (result == 0)
            return ssid.compareTo(other.ssid)
        if (result == 1)
            return -1
        return 1
    }

    override fun equals(other: Any?): Boolean {
        val o = other as AccessPointViewModel?
        if (o?.ssid == ssid) return true
        return false
    }

    override fun hashCode(): Int {
        return ssid.hashCode()
    }
}