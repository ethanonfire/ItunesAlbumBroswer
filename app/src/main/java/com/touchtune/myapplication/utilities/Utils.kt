package com.touchtune.myapplication.utilities

import com.touchtune.myapplication.UiState
import com.touchtune.myapplication.data.Album
import java.time.DateTimeException
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object Utils {

    @JvmStatic
    fun javaDateTimeConverter(dateTimeString: String?): LocalDateTime?{
        val formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
        return dateTimeString?.let {
            try {
                val zonedDateTime = ZonedDateTime.parse(it, formatter)
                LocalDateTime.from(zonedDateTime)
            } catch (dateTimeException: DateTimeException) {
                null
            }
        }
    }

    @JvmStatic
    fun processAlbumList(albums: List<Album>): List<Album> {
        val newAlbums = albums.drop(1)
        newAlbums.forEach {
            it.releaseDate = javaDateTimeConverter(it.releaseDate)?.year?.toString() ?: "0"
        }
        return newAlbums.sortedByDescending { it.releaseDate?.toInt() }
    }


    @JvmStatic
    fun getCurrentAlbums(uiState: UiState):List<Album>?{
        var albums:List<Album>? = null
        when(uiState) {
            is UiState.Success<*> -> {
                albums = uiState.items as List<Album>
            }
            else -> {}
        }
        return albums
    }

    @JvmStatic
    fun extractReleaseYears(uiState: UiState):List<Int>?{
        var years:List<Int>? = null
        when(uiState) {
            is UiState.Success<*> -> {
                years =  uiState.items
                    .filterIsInstance<Album>()
                    .mapNotNull { it.releaseDate?.toIntOrNull() }
                    .toSortedSet().toList()
            }
            else -> {}
        }
        return years
    }

    @JvmStatic
    fun filterAlbumsByYear(albums: List<Album>?, year: Int): List<Album>? {
        return albums?.filter { it.releaseDate?.toIntOrNull() == year }
    }
}