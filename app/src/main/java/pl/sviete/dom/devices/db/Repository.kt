package pl.sviete.dom.devices.db

import android.arch.lifecycle.LiveData
import android.support.annotation.WorkerThread

class Repository internal constructor(private val dao: AisDeviceDao) {

    val allDevices: LiveData<List<AisDeviceEntity>> = dao.getAll()

    @WorkerThread
    suspend fun insert(device: AisDeviceEntity) {
        dao.insert(device)
    }
}