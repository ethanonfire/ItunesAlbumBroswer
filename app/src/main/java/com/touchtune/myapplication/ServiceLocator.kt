/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.touchtune.myapplication

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.touchtune.myapplication.api.ITuneApi
import com.touchtune.myapplication.data.*

/**
 * A Service Locator for the [DefaultRepository]
 */
object ServiceLocator {

    private var database: AppDatabase? = null

    @Volatile
    var appRepository: Repository? = null
        @VisibleForTesting set

    fun provideAppRepository(context: Context): Repository {
        synchronized(this) {
            return appRepository ?: createAppRepository(context)
        }
    }

    private fun createAppRepository(context: Context): Repository {
        val newRepo = DefaultRepository(
            createAlbumRemoteDataSource(),
            createRecentSearchLocalDataSource(context)
        )
        appRepository = newRepo
        return newRepo
    }

    private fun createAlbumRemoteDataSource(): AlbumDataSource {
        return AlbumRemoteDataSource(ITuneApi.retrofitService)
    }

    private fun createRecentSearchLocalDataSource(context: Context): RecentSearchDataSource {
        val database = database ?: AppDatabase.getInstance(context)
        return RecentSearchLocalDataSource(database.RecentSearchDao())
    }
}
