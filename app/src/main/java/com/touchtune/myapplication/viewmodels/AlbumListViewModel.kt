package com.touchtune.myapplication.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.touchtune.myapplication.UiState
import com.touchtune.myapplication.data.Album
import com.touchtune.myapplication.data.Repository
import com.touchtune.myapplication.utilities.Utils.extractReleaseYears
import com.touchtune.myapplication.utilities.Utils.getCurrentAlbums
import kotlinx.coroutines.flow.*

class AlbumListViewModel(var repository: Repository) : ViewModel() {

    private var currentAlbums: List<Album>? = null

    private val _albumsFlow = MutableStateFlow<UiState?>(null)
    val albumsFlow: StateFlow<UiState?> = _albumsFlow

    private var _years = MutableLiveData<List<Int>?>(null)
    val years: LiveData<List<Int>?> = _years

    init{
        Log.d("BindingAdapter", "new created viewmodel")
    }

    suspend fun searchAlbumsById(id: Long) {
        Log.d("BindingAdapter", "getAlbumsByArtistId: $id")

        repository.getAlbumsByArtistId(id)
            .collect { uiState ->
                Log.d("BindingAdapter from collected from flow", "getAlbumsByArtistId collect: " + uiState.toString())
                currentAlbums = getCurrentAlbums(uiState)
                _years.postValue(extractReleaseYears(uiState))
                _albumsFlow.value = uiState
            }
    }

    fun setFiltering(year: Int) {
        currentAlbums?.let {
            _albumsFlow.value =
                UiState.Success(currentAlbums?.filter {
                    it.releaseDate?.toIntOrNull() == year
                } as List<Album>, UiState.DataType.ALBUM_SEARCH)
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