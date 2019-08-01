package pl.sviete.dom.devices.netscanner

import android.arch.lifecycle.MutableLiveData
import pl.sviete.dom.devices.aiscontrollers.models.PowerStatus
import pl.sviete.dom.devices.models.AisDeviceType
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class FoundDeviceRepository {
    companion object {
        private var instance: FoundDeviceRepository? = null

        fun getInstance(): FoundDeviceRepository {
            if (instance == null) {
                instance =
                    FoundDeviceRepository()
            }
            return instance!!
        }
    }

    private val mLock = ReentrantLock()
    private val coll = mutableListOf<FoundDeviceModel>()
    private var map = Collections.synchronizedList(coll)
    val liveData = MutableLiveData<List<FoundDeviceModel>>()

    fun add(ip: String, mac: String?, founded: Boolean): Boolean{
        mLock.withLock {
            val dev = map.firstOrNull { x -> x.ip == ip}
            if (dev == null) {
                map.add(FoundDeviceModel(ip, founded))
                return true
            }
            else{
                if (!mac.isNullOrEmpty())
                    dev.mac = mac
                if (!founded)
                    dev.founded = founded
            }
            return false
        }
    }

    fun setAisDevice(ip: String, mac: String, name: String, type: AisDeviceType?, status: PowerStatus) {
        val device = map.firstOrNull { x -> x.ip == ip }
        if (device != null) {
            device.isAisDevice = true
            device.name = name
            device.type = type
            device.status = status
            device.mac = mac
            liveData.postValue(coll)
        }
    }

    fun setNonAisDevice(ip: String) {
        val device = map.firstOrNull { x -> x.ip == ip }
        if (device != null) {
            device.isAisDevice = false
            liveData.postValue(coll)
        }
    }

    fun setStatus(mac: String, status: PowerStatus) {
        val device = map.firstOrNull { x -> x.mac?.toUpperCase() == mac.toUpperCase() }
        if (device != null) {
            device.status = status
            liveData.postValue(coll)
        }
    }

    fun getStatus(mac: String) : PowerStatus {
        val device = map.firstOrNull { x -> x.mac?.toUpperCase() == mac.toUpperCase() }
        return device?.status ?: PowerStatus.Unknown
    }

    fun getFoundedDevices(): List<FoundDeviceModel> {
        return map.filter { x -> x.isAisDevice == true && x.founded }
    }

    fun getDevicesWithMAC(): List<FoundDeviceModel> {
        return map.filter { x -> x.isAisDevice == true && !x.mac.isNullOrEmpty() }
    }

    fun deleteDevice(mac: String){
        val device = map.firstOrNull { x -> x.mac?.toUpperCase() == mac.toUpperCase() }
        if (device != null)
            map.remove(device)
    }

    fun clear(){
        mLock.withLock {
            map.clear()
        }
    }
}