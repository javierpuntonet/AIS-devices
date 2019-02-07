package pl.sviete.dom.devices.models

enum class AisDeviceType (val value: Int){
    Socket(8),
    Bulb(27);

    companion object {
        fun fromInt(value: Int) = AisDeviceType.values().first { it.value == value }
    }
}