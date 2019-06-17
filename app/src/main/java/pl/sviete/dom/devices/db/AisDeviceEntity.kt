package pl.sviete.dom.devices.db

import android.arch.persistence.room.*


@Entity(tableName = "AisDevice",
    foreignKeys = arrayOf(ForeignKey(
    entity = AreaEntity::class,
    parentColumns = arrayOf("uid"),
    childColumns = arrayOf("area_id")))
)
class AisDeviceEntity(
    @PrimaryKey(autoGenerate = true) var uid: Long? = null,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "mac") var mac: String,
    @ColumnInfo(name = "ip") var ip: String?,
    @ColumnInfo(name = "type") var type: Int?,
    @ColumnInfo(name = "area_id") val areaId: Long? = null
)
{
    override fun toString(): String {
        return name
    }
}