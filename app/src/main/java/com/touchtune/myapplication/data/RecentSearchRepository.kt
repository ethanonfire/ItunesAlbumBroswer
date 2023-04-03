package com.touchtune.myapplication.data

interface RecentSearchRepository {

    suspend fun getRecentSearches(): List<RecentArtistSearch>

}