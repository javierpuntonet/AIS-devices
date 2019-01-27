package pl.sviete.dom.devices.aiscontrollers.models

import com.google.gson.annotations.SerializedName

enum class PowerStatus {
    @SerializedName("UNKNOWN")
    Unknown,
    @SerializedName("ON")
    On,
    @SerializedName("OFF")
    Off
}