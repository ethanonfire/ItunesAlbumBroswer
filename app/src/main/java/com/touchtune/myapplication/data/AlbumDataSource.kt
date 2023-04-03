package com.touchtune.myapplication.data


interface AlbumDataSource {

    suspend fun getAlbumByArtist(artistName: String): List<Album>

    suspend fun getArtistByName(artistName: String): List<Artist>

    suspend fun getArtistById(id: Long): List<Album>
}