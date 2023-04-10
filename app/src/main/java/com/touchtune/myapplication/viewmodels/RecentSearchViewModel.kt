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

class RecentSearchViewModel(var repository: Repository) : ViewModel() {

    private var _searchFlow = MutableStateFlow<UiState?>(null)

    var recentSearchFlow: StateFlow<UiState?> = _searchFlow

    init {
        viewModelScope.launch {
            getRecentSearch()
        }
    }

    private suspend fun getRecentSearch() {
        repository.getRecentSearches().collect {
            Log.d("ArtistSearchViewModel", it.toString())
            _searchFlow.value = it
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

//    suspend fun searchArtistByName(name: String) {
//        repository.searchArtistByName(name).collect {
//            _searchFlow.value = it
//        }
//    }

    class MapViewModelFactory(private val repository: Repository) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RecentSearchViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RecentSearchViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}