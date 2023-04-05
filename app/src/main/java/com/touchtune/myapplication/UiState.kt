package com.touchtune.myapplication

sealed class UiState {
    enum class DataType {
        ARTIST_SEARCH, RECENT_SEARCH, ALBUM_SEARCH
    }

    object Loading : UiState()
    data class Success<T>(val items: List<T>, val dataType: DataType) : UiState()
    object NoData : UiState()
    data class Error(val message: String) : UiState()
}