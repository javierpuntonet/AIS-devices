package pl.sviete.dom.devices.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import pl.sviete.dom.devices.models.AisDeviceType

@Dao
interface AisDeviceDao {
    @Query("SELECT * FROM AisDevice")
    fun getAll(): LiveData<List<AisDeviceEntity>>

    @Query("SELECT * FROM AisDevice WHERE uid=:id")
    fun getById(id: Long): LiveData<AisDeviceEntity>

    @Query("SELECT * FROM AisDevice WHERE type=:type")
    fun getByType(type: Int): LiveData<List<AisDeviceEntity>>

    @Query("SELECT count(*) from AisDevice")
    fun getCount(): Int

    @Insert
    fun insert(device: AisDeviceEntity): Long

    @Delete
    fun delete(vararg device: AisDeviceEntity)

    @Update
    fun update(vararg device: AisDeviceEntity)

    @Query("UPDATE AisDevice SET area_id = :areaId WHERE uid = :deviceId")
    fun updateArea(deviceId: Long, areaId: Long)
}