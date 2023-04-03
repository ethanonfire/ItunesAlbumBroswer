package com.touchtune.myapplication.data

import kotlinx.coroutines.flow.Flow

interface RecentSearchDataSource {

    fun getRecentSearches(): Flow<List<RecentArtistSearch>>

    suspend fun insertRecentSearch(search: RecentArtistSearch)

    suspend fun getCount(): Int
}