package pl.sviete.dom.devices.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface AisDeviceDao {
    @Query("SELECT * from AisDevice")
    fun getAll(): LiveData<List<AisDeviceEntity>>

    @Query("SELECT * FROM AisDevice WHERE uid=:id")
    fun getById(id: Int): LiveData<AisDeviceEntity>

    @Query("SELECT count(*) from AisDevice")
    fun getCount(): Int

    @Insert
    fun insert(vararg device: AisDeviceEntity)

    @Delete
    fun delete(vararg device: AisDeviceEntity)

    @Update
    fun update(vararg device: AisDeviceEntity)
}