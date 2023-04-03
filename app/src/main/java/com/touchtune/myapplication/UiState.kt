package com.touchtune.myapplication

sealed class UiState {
    object Loading : UiState()

    data class Success<T>(val items: List<T>) : UiState()

    data class Error(val message: String) : UiState()
}