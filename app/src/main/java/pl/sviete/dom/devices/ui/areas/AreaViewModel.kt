package pl.sviete.dom.devices.ui.areas

data class AreaViewModel(
    val id: Long,
    val name: String
): Comparable<AreaViewModel>
{
    override fun compareTo(other: AreaViewModel): Int {
        return this.name.compareTo(other.name, true)
    }

    override fun toString(): String {
        return name
    }
}