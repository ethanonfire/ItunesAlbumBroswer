package com.touchtune.myapplication.utilities

import com.touchtune.myapplication.data.Album
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object Utils {

    @JvmStatic
    fun javaDateTimeConverter(dateTimeString: String): LocalDateTime {
        val formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
        val zonedDateTime = ZonedDateTime.parse(dateTimeString, formatter)
        return LocalDateTime.from(zonedDateTime)
    }

    @JvmStatic
    fun processAlbumList(albums: List<Album>): List<Album> {
        var newAlbums = albums.drop(1)
        newAlbums = newAlbums.distinctBy { it.collectionName }
        newAlbums.forEach {
            it.releaseDate = javaDateTimeConverter(it.releaseDate!!).year.toString()
        }
        return newAlbums.sortedByDescending { it.releaseDate!!.toInt() }
    }
}