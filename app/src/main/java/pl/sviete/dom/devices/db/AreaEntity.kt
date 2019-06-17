package pl.sviete.dom.devices.db

import android.arch.persistence.room.*

@Entity(tableName = "Area")
class AreaEntity (
    @PrimaryKey(autoGenerate = true) var uid: Long? = null,
    @ColumnInfo(name = "name") var name: String)
{
    override fun toString(): String {
        return name
    }
}