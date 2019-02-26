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
    val devices = MutableLiveData<List<FoundDeviceModel>>()

    fun add(ip: String, founded: Boolean): Boolean{
        mLock.withLock {
            if (!map.any { x -> x.ip == ip }) {
                map.add(FoundDeviceModel(ip, founded))
                return true
            }
            return false
        }
    }

    fun set(ip: String, isAisDevice: Boolean, mac: String? = null, name: String? = null, type: AisDeviceType? = null, status: PowerStatus? = null) {
        val device = map.firstOrNull { x -> x.ip == ip }
        if (device != null) {
            device.isAisDevice = isAisDevice
            device.name = name
            device.type = type
            device.status = status
            device.mac = mac
            devices.postValue(coll)
        }
    }

    fun set(ip: String, status: PowerStatus) {
        val device = map.firstOrNull { x -> x.ip == ip }
        if (device != null) {
            device.status = status
            devices.postValue(coll)
        }
    }

    fun getStatus(ip: String) : PowerStatus {
        val device = map.firstOrNull { x -> x.ip == ip }
        return device?.status ?: PowerStatus.Unknown
    }

    fun getFoundedDevices(): List<FoundDeviceModel> {
        return map.filter { x -> x.isAisDevice == true && x.founded }
    }

    fun getDevicesWithMAC(): List<FoundDeviceModel> {
        return map.filter { x -> x.isAisDevice == true && !x.mac.isNullOrEmpty() }
    }

    fun clear(){
        mLock.withLock {
            map.clear()
        }
    }
}