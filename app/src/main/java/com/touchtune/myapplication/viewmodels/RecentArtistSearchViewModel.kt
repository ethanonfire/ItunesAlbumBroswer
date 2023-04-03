package com.touchtune.myapplication.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.touchtune.myapplication.UiState
import com.touchtune.myapplication.data.RecentArtistSearch
import com.touchtune.myapplication.data.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

class RecentArtistSearchViewModel(var repository: Repository) : ViewModel() {

    private var _artistSearch = MutableLiveData<UiState>()

    var artistSearch: LiveData<UiState> = _artistSearch

    val recentSearchFlow: LiveData<UiState>
        get() = repository.getRecentSearches().asLiveData()

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

    fun searchArtistByName(name: String): Job {
        return viewModelScope.launch {
            try {
                repository.searchArtistByName(name).collect {
                    _artistSearch.value = it
                }
            } catch (ioException: IOException) {
                Log.d(
                    "IOException", "IOException: ${ioException.message}"
                )
            }
        }
    }

    class MapViewModelFactory(private val repository: Repository) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RecentArtistSearchViewModel::class.java)) {
                return RecentArtistSearchViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}