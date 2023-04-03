package com.touchtune.myapplication.data

import kotlinx.coroutines.flow.Flow

class RecentSearchLocalDataSource(

    private val recentSearchDao: RecentSearchDao,

    ) : RecentSearchDataSource {
    override fun getRecentSearches(): Flow<List<RecentArtistSearch>> {
        return recentSearchDao.getAll()
    }

    override suspend fun insertRecentSearch(search: RecentArtistSearch) {
        return recentSearchDao.updateRecentSearch(search)
    }

    override suspend fun getCount(): Int {
        return recentSearchDao.getEntriesCount()
    }
}