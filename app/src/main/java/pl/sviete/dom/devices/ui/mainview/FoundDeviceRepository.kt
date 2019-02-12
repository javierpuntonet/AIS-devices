package pl.sviete.dom.devices.ui.mainview

import android.arch.lifecycle.MutableLiveData
import pl.sviete.dom.devices.aiscontrollers.models.PowerStatus
import pl.sviete.dom.devices.models.AisDeviceType
import java.util.concurrent.ConcurrentHashMap

class FoundDeviceRepository {
    companion object {
        private var instance: FoundDeviceRepository? = null

        fun getInstance(): FoundDeviceRepository {
            if (instance == null) {
                instance = FoundDeviceRepository()
            }
            return instance!!
        }
    }

    private var map = ConcurrentHashMap<String, FoundDeviceModel>()
    val devices = MutableLiveData<ConcurrentHashMap<String, FoundDeviceModel>>()

    fun add(mac: String, ip: String): Boolean{
        if (!map.containsKey(mac)) {
            map[mac] = FoundDeviceModel(mac, ip)
            return true
        }
        return false
    }

    fun get(mac: String) : FoundDeviceModel? {
        if (map.containsKey(mac))
            return map[mac]!!
        return null
    }

    fun set(mac: String, isAisDevice: Boolean, name: String? = null, type: AisDeviceType? = null, status: PowerStatus? = null) {
        if (map.containsKey(mac)) {
            val device = map[mac]!!
            device.isAisDevice = isAisDevice
            device.name = name
            device.type = type
            device.status = status
            devices.postValue(map)
        }
    }

    fun clear(){
        map.clear()
    }
}