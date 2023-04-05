package com.touchtune.myapplication.viewmodels

import androidx.lifecycle.*
import com.touchtune.myapplication.UiState
import com.touchtune.myapplication.data.Repository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class AlbumListViewModel(var repository: Repository) : ViewModel() {

    private var _album = MutableStateFlow<UiState?>(null)
    val album: StateFlow<UiState?> = _album

    suspend fun searchAlbumsById(id: Long) {
        repository.getAlbumsByArtistId(id).stateIn(
            scope = viewModelScope,
            initialValue = UiState.Loading,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000)
        ).collect {
            _album.value = it
        }
    }

    class MapViewModelFactory(private val repository: Repository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AlbumListViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AlbumListViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}