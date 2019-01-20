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

    init {
        val dao = DataBase.getInstance(application, scope).dao()
        repository = Repository(dao)
    }

    fun  getAll():LiveData<List<AisDeviceEntity>>{
        return  repository.getAll()
    }

    fun getById(id: Int): LiveData<AisDeviceEntity>{
        return repository.getById(id)
    }

    fun insert(device: AisDeviceEntity) = scope.launch(Dispatchers.IO) {
        repository.insert(device)
    }

    fun update(device: AisDeviceEntity) = scope.launch(Dispatchers.IO) {
        repository.update(device)
    }

    fun delete(device: AisDeviceEntity) = scope.launch(Dispatchers.IO) {
        repository.delete(device)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}