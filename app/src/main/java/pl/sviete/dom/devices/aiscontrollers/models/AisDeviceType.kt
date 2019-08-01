package pl.sviete.dom.devices.models

enum class AisDeviceType (val value: Int){
    Socket(8),
    Bulb(26),
    Box(999);

    companion object {
        fun fromInt(value: Int) = AisDeviceType.values().firstOrNull { it.value == value }
    }
}