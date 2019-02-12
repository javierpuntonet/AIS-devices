package pl.sviete.dom.devices.ui.mainview

import pl.sviete.dom.devices.aiscontrollers.models.PowerStatus
import pl.sviete.dom.devices.models.AisDeviceType

data class FoundDeviceModel(val mac: String,
                            val ip: String,
                            var isAisDevice: Boolean? = null,
                            var name: String? = null,
                            var type: AisDeviceType? = null,
                            var status: PowerStatus? = null
)