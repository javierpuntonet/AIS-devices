package pl.sviete.dom.devices.ui.mainview

import android.arch.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.sviete.dom.devices.aiscontrollers.AisDeviceController
import pl.sviete.dom.devices.aiscontrollers.models.PowerStatus

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

    private var map = HashMap<String, PowerStatus>()

    val statuses = MutableLiveData<HashMap<String, PowerStatus>>()

    fun add(ip: String){
        if (!map.contains(ip)) {
            map[ip] = PowerStatus.Unknown
            refreshStatus(ip)
        }
    }

    fun get(ip: String) : PowerStatus {
        if (map.containsKey(ip))
            return map[ip]!!
        return PowerStatus.Unknown
    }

    fun set(ip: String, status: PowerStatus) {
        if (map.containsKey(ip))
            map[ip] = status
    }

    private fun refreshStatus(ip: String){
        if (!ip.isNullOrEmpty()) {
            GlobalScope.launch(Dispatchers.Main) {
                val status = AisDeviceController.getPowerStatus(ip)
                if (status != null) {
                    map[ip] = status
                    statuses.postValue(map)
                }
            }
        }
    }
}