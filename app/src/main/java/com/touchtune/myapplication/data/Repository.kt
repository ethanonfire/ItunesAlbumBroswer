package com.touchtune.myapplication.data

import com.touchtune.myapplication.UiState
import kotlinx.coroutines.flow.Flow


interface Repository {

    suspend fun getAlbumByArtist(artistName: String): List<Album>

    fun getRecentSearches(): Flow<UiState>

    suspend fun insertRecentSearch(search: RecentArtistSearch)

    suspend fun getSearchCount(): Int

    suspend fun searchArtistByName(name: String): Flow<UiState>

    suspend fun getAlbumById(id: Long): List<Album>

}