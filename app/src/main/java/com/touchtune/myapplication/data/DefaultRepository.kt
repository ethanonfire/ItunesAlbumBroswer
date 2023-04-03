package com.touchtune.myapplication.data

import com.touchtune.myapplication.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*


class DefaultRepository(
    private val albumRemoteDataSource: AlbumDataSource,
    private val recentSearchDataSource: RecentSearchDataSource
) : Repository {

    override suspend fun getAlbumByArtist(artistName: String): List<Album> {
        return albumRemoteDataSource.getAlbumByArtist(artistName)
    }

    override fun getRecentSearches(): Flow<UiState> {

        return recentSearchDataSource.getRecentSearches()
            .flowOn(Dispatchers.IO)
            .map {
                UiState.Success(it)
            }
            .onStart {
                emit(UiState.Loading)
            }
    }

    override suspend fun insertRecentSearch(search: RecentArtistSearch) {
        return recentSearchDataSource.insertRecentSearch(search)
    }

    override suspend fun getSearchCount(): Int {
        return recentSearchDataSource.getCount()
    }

    override suspend fun searchArtistByName(name: String): Flow<UiState> {
        return flow {
            emit(albumRemoteDataSource.getArtistByName(name))
        }.map {
            UiState.Success(it)
        }.onStart {
            emit(UiState.Loading)
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getAlbumById(id: Long): List<Album> {
        return albumRemoteDataSource.getArtistById(id)
    }
}