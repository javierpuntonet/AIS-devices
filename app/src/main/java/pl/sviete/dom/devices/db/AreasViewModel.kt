package pl.sviete.dom.devices.db

import android.app.Application
import android.arch.lifecycle.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class AreasViewModel(application: Application) : AndroidViewModel(application) {

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)
    var insertionId = MutableLiveData<Long>()

    private val repository: AreaRepository

    init {
        val dao = DataBase.getInstance(application).areaDao()
        repository = AreaRepository(dao)
    }

    fun  getAll(): LiveData<List<AreaEntity>> {
        return  repository.getAll()
    }

    fun insert(area: AreaEntity) {
        scope.launch(Dispatchers.IO) {
            val result =
                try {
                    val id = repository.insert(area)
                    id
                } catch (e: Exception) {
                    -1L
                }
            insertionId.postValue(result)
        }
    }


    fun update(area: AreaEntity) = scope.launch(Dispatchers.IO) {
        repository.update(area)
    }

    fun delete(area: AreaEntity) = scope.launch(Dispatchers.IO) {
        repository.delete(area)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}