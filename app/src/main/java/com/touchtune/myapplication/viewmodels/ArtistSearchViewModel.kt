package com.touchtune.myapplication.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.touchtune.myapplication.UiState
import com.touchtune.myapplication.data.RecentArtistSearch
import com.touchtune.myapplication.data.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException

class ArtistSearchViewModel(var repository: Repository) : ViewModel() {

    private var _search = MutableStateFlow<UiState>(UiState.Loading)

    var search: StateFlow<UiState> = _search

    init {
        viewModelScope.launch {
            getRecentSearch()
        }
    }

    private suspend fun getRecentSearch() {
        repository.getRecentSearches().stateIn(
            scope = viewModelScope,
            initialValue = UiState.Loading,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000)
        ).collect {
            _search.value = it
        }
    }

    fun insertRecentSearch(search: RecentArtistSearch) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.insertRecentSearch(search)
            } catch (ioException: IOException) {
                Log.d(
                    "IOException",
                    "IOException: ${ioException.message}"
                )
            }
        }
    }

    suspend fun searchArtistByName(name: String) {
        repository.searchArtistByName(name).stateIn(
            scope = viewModelScope,
            initialValue = UiState.Loading,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000)
        ).collect {
            _search.value = it
        }
    }

    class MapViewModelFactory(private val repository: Repository) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ArtistSearchViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ArtistSearchViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}