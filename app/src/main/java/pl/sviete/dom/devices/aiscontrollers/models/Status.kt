package pl.sviete.dom.devices.aiscontrollers.models

data class Status (
    val Status: StatusInternal
)

data class StatusInternal(
    val Module: Int
)