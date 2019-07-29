package pl.sviete.dom.devices.netscanner

import android.arch.lifecycle.MutableLiveData
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
    val boxes = MutableLiveData<List<BoxModel>>()

    fun add(name: String, gateId: String){
        mLock.withLock {
            val dev = map.firstOrNull { x -> x.gateId == gateId}
            if (dev == null) {
                map.add(BoxModel(name, gateId))
                boxes.postValue(coll)
            }
        }
    }
}