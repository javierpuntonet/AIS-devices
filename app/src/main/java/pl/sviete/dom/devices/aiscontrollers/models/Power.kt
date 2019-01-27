package pl.sviete.dom.devices.aiscontrollers.models

import com.google.gson.annotations.SerializedName


data class Power (
    @SerializedName("POWER")
    val Power: PowerStatus
)