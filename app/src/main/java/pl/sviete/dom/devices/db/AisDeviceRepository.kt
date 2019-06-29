package pl.sviete.dom.devices.db

import android.arch.lifecycle.LiveData
import android.support.annotation.WorkerThread

class AisDeviceRepository internal constructor(private val dao: AisDeviceDao) {

    fun getAll(): LiveData<List<AisDeviceEntity>>{
        return dao.getAll()
    }

    fun getById(id: Long): LiveData<AisDeviceEntity> {
        return dao.getById(id)
    }

    fun getByArea(areaId: Long?): LiveData<List<AisDeviceEntity>> {
        return dao.getByArea(areaId)
    }

    fun getByAreaIsEmpty(): LiveData<List<AisDeviceEntity>> {
        return dao.getByAreaIsEmpty()
    }

    @WorkerThread
    fun insert(device: AisDeviceEntity): Long {
        return dao.insert(device)
    }

    @WorkerThread
    fun update(device: AisDeviceEntity) {
        dao.update(device)
    }

    @WorkerThread
    fun delete(device: AisDeviceEntity) {
        dao.delete(device)
    }
}