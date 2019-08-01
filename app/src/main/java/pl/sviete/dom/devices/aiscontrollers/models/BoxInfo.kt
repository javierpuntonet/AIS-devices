package pl.sviete.dom.devices.aiscontrollers.models

import com.google.gson.annotations.SerializedName

data class BoxInfo (
    @SerializedName("Hostname")
    val Hostname: String,
    @SerializedName("gate_id")
    val GateId: String
)