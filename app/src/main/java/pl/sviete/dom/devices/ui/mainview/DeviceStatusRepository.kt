package pl.sviete.dom.devices.ui.mainview

import android.arch.lifecycle.MutableLiveData
import pl.sviete.dom.devices.aiscontrollers.models.PowerStatus
import java.util.concurrent.ConcurrentHashMap

class DeviceStatusRepository {

    companion object {
        private var instance: DeviceStatusRepository? = null

        fun getInstance(): DeviceStatusRepository {
            if (instance == null) {
                instance = DeviceStatusRepository()
            }
            return instance!!
        }
    }

    private var map = ConcurrentHashMap<String, PowerStatus>()

    val statuses = MutableLiveData<ConcurrentHashMap<String, PowerStatus>>()

    fun add(ip: String): Boolean{
        if (!map.containsKey(ip)) {
            map[ip] = PowerStatus.Unknown
            return true
        }
        return false
    }

    fun get(ip: String) : PowerStatus {
        if (map.containsKey(ip))
            return map[ip]!!
        return PowerStatus.Unknown
    }

    fun set(ip: String, status: PowerStatus) {
        if (map.containsKey(ip)) {
            map[ip] = status
            statuses.postValue(map)
        }
    }

    fun clear(){
        map.clear()
    }
}