package pl.sviete.dom.devices.aiscontrollers.models

data class Status (
    val Status: StatusInternal,
    val StatusNET: StatusNet
)

data class StatusInternal(
    val Module: Int,
    val Power: Int,
    val FriendlyName: List<String>
)

data class StatusNet(
    val Mac: String
)