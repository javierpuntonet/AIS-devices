package pl.sviete.dom.devices.helpers

import pl.sviete.dom.devices.R
import pl.sviete.dom.devices.models.AisDeviceType
import java.util.*

class AisDeviceHelper {
    companion object{
        fun getResourceForType(type: AisDeviceType?): Int {
            return when (type) {
                AisDeviceType.Bulb -> R.drawable.bulb
                AisDeviceType.Socket -> R.drawable.socket
                AisDeviceType.Box -> R.drawable.mqtt_gate_logo
                else -> R.drawable.logo
            }
        }

        fun apIsAisDevice(isOpen: Boolean, mac: String): Boolean {
            if (isOpen){
                val macFragment = mac.substring(3, 8).toUpperCase(Locale.ROOT)
                if (suportedVendorsIdList.contains(macFragment)) {
                    return true
                }
            }
            return false
        }

        private var suportedVendorsIdList = listOf(
            "FE:34",
            "0A:C4",
            "62:AB",
            "6F:28",
            "B2:DE",
            "3A:E8",
            "F4:32",
            "AE:A4",
            "71:BF",
            "11:AE",
            "02:91",
            "5A:A6",
            "CF:7F",
            "01:94",
            "C6:3A",
            "7D:3A",
            "0D:8E",
            "F3:EB",
            "97:D5",
            "F4:AB",
            "20:A6",
            "7B:9D",
            "CF:12",
            "D0:74",
            "E6:2D",
            "DD:C2",
            "4F:33",
            "2B:96",
            "50:E3",
            "A0:1D",
            "BF:C0",
            "F1:5B",
            "4F:22",
            "FA:BC"
        )
    }
}