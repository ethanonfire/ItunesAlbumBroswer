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

package com.touchtune.myapplication.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.touchtune.myapplication.data.AppDatabase
import com.touchtune.myapplication.data.RecentArtistSearch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecentSearchDatabaseWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val filename = inputData.getString(KEY_FILENAME)

            if (filename != null) {
                applicationContext.assets.open(filename).use { inputStream ->
                    JsonReader(inputStream.reader()).use { jsonReader ->
                        val plantType = object : TypeToken<List<RecentArtistSearch>>() {}.type
                        val searchList: List<RecentArtistSearch> =
                            Gson().fromJson(jsonReader, plantType)
                        val database = AppDatabase.getInstance(applicationContext)
                        database.RecentSearchDao().insertAll(searchList)
                        Result.success()
                    }
                }
            } else {
                Log.e(TAG, "Error album database - no valid filename")
                Result.failure()
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Error album database", ex)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "AlbumDatabaseWorker"
        const val KEY_FILENAME = "ALBUM_DATA_FILENAME"
    }
}
