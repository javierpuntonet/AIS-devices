package pl.sviete.dom.devices.ui.areas

import java.io.Serializable

data class AreaViewModel(
    val id: Long,
    val name: String
): Comparable<AreaViewModel>, Serializable
{
    val isEmpty: Boolean
        get() = this.id == EMPTY

    override fun compareTo(other: AreaViewModel): Int {
        return this.name.compareTo(other.name, true)
    }

    override fun toString(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {
        if (other is AreaViewModel)
            return id == other.id
        return false
    }

    companion object{
        val EMPTY = -1L
    }
}