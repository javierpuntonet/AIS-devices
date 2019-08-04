package pl.sviete.dom.devices.netscanner

import android.arch.lifecycle.MutableLiveData
import pl.sviete.dom.devices.aiscontrollers.models.PowerStatus
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class FoundBoxRepository {
    companion object {
        private var instance: FoundBoxRepository? = null

        fun getInstance(): FoundBoxRepository {
            if (instance == null) {
                instance =
                    FoundBoxRepository()
            }
            return instance!!
        }
    }

    private val mLock = ReentrantLock()
    private val coll = mutableListOf<BoxModel>()
    private var map = Collections.synchronizedList(coll)
    val liveData = MutableLiveData<List<BoxModel>>()

    fun add(name: String, gateId: String, ip: String, founded: Boolean) {
        mLock.withLock {
            val dev = map.firstOrNull { x -> x.gateId == gateId }
            if (dev == null) {
                map.add(BoxModel(name, gateId, ip, founded))
                liveData.postValue(coll)
            }
        }
    }

    fun deleteDevice(gateId: String){
        val device = map.firstOrNull { x -> x.gateId.toUpperCase() == gateId.toUpperCase() }
        if (device != null)
            map.remove(device)
    }

    fun getStatus(mac: String) : PowerStatus {
        val device = map.firstOrNull { x -> x.gateId?.toUpperCase() == mac.toUpperCase() }
        return if (device?.founded == false) PowerStatus.On else PowerStatus.Off
    }
}