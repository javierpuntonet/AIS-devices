package pl.sviete.dom.devices.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface AreaDao {
    @Query("SELECT * FROM Area")
    fun getAll(): LiveData<List<AreaEntity>>

    @Insert
    fun insert(device: AreaEntity): Long

    @Delete
    fun delete(vararg device: AreaEntity)

    @Update
    fun update(vararg device: AreaEntity)

    @Query("UPDATE AisDevice SET area_id = null WHERE area_id = :areaId")
    fun clearArea(vararg areaId: Long)

    @Transaction
    fun deleteAndClear(area: AreaEntity){
        clearArea(area.uid!!)
        delete(area)
    }
}