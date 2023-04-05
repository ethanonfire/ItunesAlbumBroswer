/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.touchtune.myapplication.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.touchtune.myapplication.MainApplication
import com.touchtune.myapplication.data.AlbumResponse
import com.touchtune.myapplication.data.ArtistResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL = "https://itunes.apple.com/"

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object.
 */

val client: OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(ConnectivityInterceptor(MainApplication.appContext))
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(client)
    .baseUrl(BASE_URL)
    .build()

/**
 * A public interface that exposes the [getWaypoints] method
 */
interface ItunesApiService {

    //https://itunes.apple.com/search?term=jack+johnson

    @GET("search")
    suspend fun getAlbumsByArtistName(
        @Query("term") artistName: String,
        @Query("entity") entity: String = "album",
        @Query("allArtistTerm") allArtistTerm: Boolean = true
    ): AlbumResponse

    //  https://itunes.apple.com/search?term=julio+iglesias&entity=musicArtist&attribute=allArtistTerm

    @GET("search")
    suspend fun getArtistsByName(
        @Query("term") artistName: String,
        @Query("entity") entity: String = "musicArtist",
        @Query("allArtistTerm") allArtistTerm: Boolean = true
    ): ArtistResponse

    // https://itunes.apple.com/lookup?id=159260351&entity=album&limit=25.
    @GET("lookup")
    suspend fun getAlbumsByArtistId(
        @Query("id") id: Long,
        @Query("entity") entity: String = "album",
    ): AlbumResponse
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object ITuneApi {
    val retrofitService: ItunesApiService by lazy { retrofit.create(ItunesApiService::class.java) }
}
