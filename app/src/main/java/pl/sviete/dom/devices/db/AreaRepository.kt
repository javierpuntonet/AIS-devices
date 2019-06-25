package pl.sviete.dom.devices.db

import android.arch.lifecycle.LiveData
import android.support.annotation.WorkerThread

class AreaRepository internal constructor(private val dao: AreaDao) {

    fun getAll(): LiveData<List<AreaEntity>> {
        return dao.getAll()
    }

    @WorkerThread
    fun insert(area: AreaEntity): Long {
        return dao.insert(area)
    }

    @WorkerThread
    fun update(area: AreaEntity) {
        dao.update(area)
    }

    @WorkerThread
    fun delete(area: AreaEntity) {
        dao.deleteAndClear(area)
    }
}