package pl.sviete.dom.devices.ui.devicedetails

data class BoxVM(val name: String, val id: String) {
    override fun toString(): String {
        return name
    }
}