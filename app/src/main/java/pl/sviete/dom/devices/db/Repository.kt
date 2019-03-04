package pl.sviete.dom.devices.db

import android.arch.lifecycle.LiveData
import android.support.annotation.WorkerThread

class Repository internal constructor(private val dao: AisDeviceDao) {

    fun getAll(): LiveData<List<AisDeviceEntity>>{
        return dao.getAll()
    }

    fun getById(id: Int): LiveData<AisDeviceEntity> {
        return dao.getById(id)
    }

    @WorkerThread
    fun insert(device: AisDeviceEntity) {
        dao.insert(device)
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