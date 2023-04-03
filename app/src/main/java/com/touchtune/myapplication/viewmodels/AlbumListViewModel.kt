package com.touchtune.myapplication.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.touchtune.myapplication.UiState
import com.touchtune.myapplication.data.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class AlbumListViewModel(var repository: Repository) : ViewModel() {

    private var _albums: MutableLiveData<UiState> = MutableLiveData()

    val albums: LiveData<UiState>
        get() = _albums

    init {
        //initial request for taylor swift's albums
        getAlbumById(159260351)
    }

    fun getAlbumById(id: Long): Job {
        return viewModelScope.launch {
            try {
                _albums.value = UiState.Loading
                withContext(Dispatchers.IO) {
                    val albums = repository.getAlbumById(id)
                    _albums.postValue(UiState.Success(albums))
                }
            } catch (ioException: IOException) {
                Log.d("MyViewModel", "IOException ${ioException.message}")
            }
        }
    }

    class MapViewModelFactory(private val repository: Repository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AlbumListViewModel::class.java)) {
                return AlbumListViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}