package com.touchtune.myapplication.data

import com.touchtune.myapplication.UiState
import com.touchtune.myapplication.utilities.Utils
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
            .map {
                val state = UiState.Success(it, UiState.DataType.RECENT_SEARCH) as UiState
                state
            }
            .onStart {
                emit(UiState.Loading)
            }.onCompletion {
            }.flowOn(Dispatchers.IO)
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
            if (it.isEmpty()) {
                UiState.NoData
            } else {
                val state = UiState.Success(it, UiState.DataType.ARTIST_SEARCH) as UiState
                state
            }
        }.onStart {
            emit(UiState.Loading)
        }.onCompletion {

        }.catch {

        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getAlbumsByArtistId(id: Long): Flow<UiState> {

        return flow {
            emit(albumRemoteDataSource.getAlbumsByArtistId(id))
        }.map {
            if (it.isEmpty()) {
                UiState.NoData
            } else {
                UiState.Success(Utils.processAlbumList(it), UiState.DataType.ALBUM_SEARCH)
            }
        }.onStart {
            emit(UiState.Loading)
        }.onCompletion {}.catch {
        }.flowOn(Dispatchers.IO)
    }
}