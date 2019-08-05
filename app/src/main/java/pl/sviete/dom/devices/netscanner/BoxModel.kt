package pl.sviete.dom.devices.netscanner

import pl.sviete.dom.devices.aiscontrollers.models.PowerStatus

data class BoxModel (
    val name: String,
    val gateId: String,
    val ip: String,
    var founded: Boolean,
    var status: PowerStatus
)