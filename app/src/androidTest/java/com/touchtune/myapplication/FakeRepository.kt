package com.touchtune.myapplication

import android.util.Log
import com.touchtune.myapplication.data.Album
import com.touchtune.myapplication.data.RecentArtistSearch
import com.touchtune.myapplication.data.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn

class FakeRepository : Repository {
    private val albums = mutableMapOf<Long, List<Album>>()

    fun addAlbums(artistId: Long, albums: List<Album>) {
        this.albums[artistId] = albums
    }

    override suspend fun getAlbumByArtist(artistName: String): List<Album> {
        TODO("Not yet implemented")
    }

    override fun getRecentSearches(): Flow<UiState> {
        TODO("Not yet implemented")
    }

    override suspend fun insertRecentSearch(search: RecentArtistSearch) {
        TODO("Not yet implemented")
    }

    override suspend fun getSearchCount(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun searchArtistByName(name: String): Flow<UiState> {
        TODO("Not yet implemented")
    }

    override suspend fun getAlbumsByArtistId(artistId: Long): Flow<UiState> {
        val artistAlbums = albums[artistId]
        return if (artistAlbums != null) {
            Log.d("ANDROIDTEST","in fake albums")

            flow{
                emit(UiState.Success(artistAlbums, UiState.DataType.ALBUM_SEARCH))
            }.flowOn(Dispatchers.IO)

        } else {
            flowOf(UiState.Error("No albums found for artistId: $artistId"))
        }
    }
}