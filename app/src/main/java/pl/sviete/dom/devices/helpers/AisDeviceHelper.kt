package pl.sviete.dom.devices.helpers

import pl.sviete.dom.devices.R
import pl.sviete.dom.devices.models.AisDeviceType

class AisDeviceHelper {
    companion object{
        fun getResourceForType(type: AisDeviceType?): Int {
            return when (type) {
                AisDeviceType.Bulb -> R.drawable.bulb
                AisDeviceType.Socket -> R.drawable.socket
                else -> R.drawable.logo
            }
        }
    }
}