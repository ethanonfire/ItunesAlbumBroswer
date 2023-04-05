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

package com.touchtune.myapplication.data

/**
 * This data class defines a Mars photo which includes an ID, and the image URL.
 * The property names of this data class are used by Moshi to match the names of values in JSON.
 */
//raw cord not screen cord
data class Album(
//    val wrapperType: String?,
    val collectionType: String?,
//    val artistId: Long?,
    val collectionId: Long?,
//    val amgArtistId: Long?,
    val artistName: String?,
    val collectionName: String?,
    val collectionCensoredName: String?,
//    val artistViewUrl: String?,
//    val collectionViewUrl: String?,
//    val artworkUrl60: String?,
    val artworkUrl100: String?,
    val collectionPrice: Double?,
//    val collectionExplicitness: String?,
//    val trackCount: Int?,
    val copyright: String?,
//    val country: String?,
    val currency: String?,
    var releaseDate: String?,
    val primaryGenreName: String?
)
