package pl.sviete.dom.devices.db

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class AisDeviceViewModel(application: Application) : AndroidViewModel(application) {
    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private val repository: Repository
    val allDevices: LiveData<List<AisDeviceEntity>>

    init {
        val dao = DataBase.getInstance(application, scope).dao()
        repository = Repository(dao)
        allDevices = repository.allDevices
    }

    fun insert(device: AisDeviceEntity) = scope.launch(Dispatchers.IO) {
        repository.insert(device)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}