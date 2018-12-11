package pl.sviete.dom.devices.bo

import android.arch.persistence.room.*

@Entity(tableName = "AisDevice")
class AisDeviceEntity(
    @PrimaryKey(autoGenerate = true) var uid: Int,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "mac") var mac: String,
    @ColumnInfo(name = "ip") var ip: String?
)