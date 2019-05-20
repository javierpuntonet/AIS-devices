package pl.sviete.dom.devices.db

import android.arch.persistence.room.*

@Entity(tableName = "AisDevice")
class AisDeviceEntity(
    @PrimaryKey(autoGenerate = true) var uid: Long? = null,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "mac") var mac: String,
    @ColumnInfo(name = "ip") var ip: String?,
    @ColumnInfo(name = "type") var type: Int?
)
{
    override fun toString(): String {
        return name
    }
}