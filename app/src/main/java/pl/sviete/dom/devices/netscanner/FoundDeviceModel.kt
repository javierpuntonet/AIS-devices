package pl.sviete.dom.devices.netscanner

import pl.sviete.dom.devices.aiscontrollers.models.PowerStatus
import pl.sviete.dom.devices.models.AisDeviceType

data class FoundDeviceModel(val ip: String,
                            var founded: Boolean,
                            var mac: String? = null,
                            var isAisDevice: Boolean? = null,
                            var name: String? = null,
                            var type: AisDeviceType? = null,
                            var status: PowerStatus? = null
)