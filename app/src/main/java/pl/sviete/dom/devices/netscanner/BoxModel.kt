package pl.sviete.dom.devices.netscanner

data class BoxModel (
    val name: String,
    val gateId: String,
    val ip: String,
    var founded: Boolean
)