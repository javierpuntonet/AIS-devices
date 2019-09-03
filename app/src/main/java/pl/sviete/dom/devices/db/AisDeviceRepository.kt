package pl.sviete.dom.devices.db

import android.arch.lifecycle.LiveData
import android.support.annotation.WorkerThread
import pl.sviete.dom.devices.models.AisDeviceType

class AisDeviceRepository internal constructor(private val dao: AisDeviceDao) {

    fun getAll(): LiveData<List<AisDeviceEntity>>{
        return dao.getAll()
    }

    fun getById(id: Long): LiveData<AisDeviceEntity> {
        return dao.getById(id)
    }

    fun getBoxes(): LiveData<List<AisDeviceEntity>> {
        return dao.getByType(AisDeviceType.Box.value)
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
    fun updateArea(deviceId: Long, areaId: Long) {
        dao.updateArea(deviceId, areaId)
    }

    @WorkerThread
    fun delete(device: AisDeviceEntity) {
        dao.delete(device)
    }
}