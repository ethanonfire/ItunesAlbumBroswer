/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.touchtune.myapplication.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.touchtune.myapplication.utilities.DATABASE_NAME
import com.touchtune.myapplication.utilities.RECENT_SEARCH_FILE_NAME
import com.touchtune.myapplication.workers.RecentSearchDatabaseWorker
import com.touchtune.myapplication.workers.RecentSearchDatabaseWorker.Companion.KEY_FILENAME

/**
 * The Room database for this app
 */
@Database(entities = [RecentArtistSearch::class], version = 4)
abstract class AppDatabase : RoomDatabase() {

    abstract fun RecentSearchDao(): RecentSearchDao

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .addCallback(
                    object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            val request = OneTimeWorkRequestBuilder<RecentSearchDatabaseWorker>()
                                .setInputData(workDataOf(KEY_FILENAME to RECENT_SEARCH_FILE_NAME))
                                .build()
                            WorkManager.getInstance(context).enqueue(request)
                        }
                    }
                )
                .build()
        }
        const val MAX_ENTRIES = 5
    }
}
