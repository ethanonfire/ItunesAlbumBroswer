package com.touchtune.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "recentArtistSearches")
data class RecentArtistSearch(
    @PrimaryKey val id: Long,
    val artistName: String,
    val timeAdded: Long
) {
    override fun toString() = artistName
}