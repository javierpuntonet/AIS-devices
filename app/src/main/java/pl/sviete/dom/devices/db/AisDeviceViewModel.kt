package pl.sviete.dom.devices.db

import android.app.Application
import android.arch.lifecycle.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class AisDeviceViewModel(application: Application) : AndroidViewModel(application) {
    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)
    var insertionId = MutableLiveData<Long>()

    private val repository: Repository

    init {
        val dao = DataBase.getInstance(application, scope).dao()
        repository = Repository(dao)
    }

    fun  getAll():LiveData<List<AisDeviceEntity>>{
        return  repository.getAll()
    }

    fun getById(id: Long): LiveData<AisDeviceEntity>{
        return repository.getById(id)
    }

    fun insert(device: AisDeviceEntity) {
        scope.launch(Dispatchers.IO) {
            val result =
                try {
                    val id = repository.insert(device)
                    id
                } catch (e: Exception) {
                    -1L
                }

            insertionId.postValue(result)
        }
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