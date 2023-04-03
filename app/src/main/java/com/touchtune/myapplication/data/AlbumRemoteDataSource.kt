package com.touchtune.myapplication.data


import com.touchtune.myapplication.api.ItunesApiService

class AlbumRemoteDataSource(
    private val apiService: ItunesApiService

) : AlbumDataSource {
    override suspend fun getAlbumByArtist(artistName: String): List<Album> {
        val response = apiService.getAlbumsByArtistName(artistName)
        return response.results
    }

    override suspend fun getArtistByName(artistName: String): List<Artist> {
        val response = apiService.getArtistsByName(artistName)
        return response.results
    }

    override suspend fun getArtistById(id: Long): List<Album> {
        val response = apiService.getAlbumById(id)
        return response.results
    }
}