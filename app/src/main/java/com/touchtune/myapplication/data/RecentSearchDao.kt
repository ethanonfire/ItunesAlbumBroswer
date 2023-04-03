package com.touchtune.myapplication.data

import androidx.room.*
import com.touchtune.myapplication.data.AppDatabase.Companion.MAX_ENTRIES
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentSearchDao {

    @Query("SELECT * FROM recentArtistSearches")
    fun getAll(): Flow<List<RecentArtistSearch>>

    @Query("UPDATE recentArtistSearches SET id = :newId WHERE id = :oldId")
    suspend fun updateIds(newId: Long, oldId: Long)

    @Query("SELECT MAX(id) FROM recentArtistSearches")
    suspend fun getMaxId(): Long?

    @Transaction
    suspend fun updateRecentSearch(search: RecentArtistSearch) {
        if (countRowsWithValue(search.artistName) == 0) {
            if (getEntriesCount() == MAX_ENTRIES) {
                //only maintain 5 entries, replace the oldest search
                updateItem(getOldestSearchEntry(), search.artistName, search.timeAdded)
            } else {
                insert(search)
            }
        }
    }

    @Query("SELECT COUNT(*) FROM recentArtistSearches WHERE artistName = :value")
    suspend fun countRowsWithValue(value: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(search: RecentArtistSearch): Long

    @Query("SELECT COUNT(*) FROM recentArtistSearches")
    suspend fun getEntriesCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<RecentArtistSearch>)

    @Query("SELECT id FROM recentArtistSearches WHERE timeadded = (SELECT MIN(timeadded) FROM recentArtistSearches) LIMIT 1")
    suspend fun getOldestSearchEntry(): Int

    @Query(
        "UPDATE recentArtistSearches SET artistName = :newArtistName, " +
                "timeAdded = :newTimeAdded WHERE id = :rowId"
    )
    suspend fun updateItem(rowId: Int, newArtistName: String, newTimeAdded: Long)
}