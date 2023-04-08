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

package com.touchtune.myapplication.adapters

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.touchtune.myapplication.UiState
import com.touchtune.myapplication.data.Album
import com.touchtune.myapplication.data.Artist
import com.touchtune.myapplication.data.RecentArtistSearch

//@BindingAdapter("visibleIff")
//fun changeVisibility(view: View, uiState: UiState?) {
//    Log.d("BindingAdapter ", "changeVisibility: " + view.toString() + " " + uiState)
//
//     when (uiState) {
//        is UiState.NoData -> {
////            Log.d("BindingAdapter", "NoData $uiState")
//            view.visibility = View.VISIBLE
//
//        }
//        is UiState.Loading -> {
////            Log.d("BindingAdapter", "Loading $uiState")
//            view.visibility = View.VISIBLE
//        }
//        else -> {
//            view.visibility = View.GONE
//
//        }
//    }
//}

@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }
}


@BindingAdapter("loadingState")
fun setUiStateForLoading(progressView: ProgressBar, uiState: UiState?) {
    progressView.visibility = when (uiState) {
        UiState.Loading -> {
            View.VISIBLE
            Log.d("BindingAdapter", "loading $uiState")
        }

        else -> View.GONE
    }
}

@BindingAdapter("emptyState")
fun setUiStateForEmptyView(view: View, uiState: UiState?) {
    view.visibility = when (uiState) {
        UiState.NoData -> {
            View.VISIBLE
            Log.d("BindingAdapter", "emptyState $uiState")
        }
        else -> View.GONE
    }
}

@BindingAdapter("errorState")
fun setUiStateForErrorView(view: View, uiState: UiState?) {
    view.visibility = when (uiState) {
        is Error -> View.VISIBLE
        else -> View.GONE
    }
}

@BindingAdapter("recentSearchState")
fun setUiStateForRecentSearchTitle(view: View, uiState: UiState?) {
    when (uiState) {
        is UiState.Success<*> -> {
            when (uiState.dataType) {
                UiState.DataType.RECENT_SEARCH -> {
                    view.visibility = View.VISIBLE
                }
                else -> {
                    view.visibility = View.GONE
                }
            }
        }
        else -> {
            view.visibility = View.GONE
        }
    }
}

@BindingAdapter(
    "dataState",
    "artistSearchAdapter",
    "recentSearchAdapter",
    "albumSearchAdapter",
    requireAll = false
)
fun setUiStateForData(
    view: View,
    uiState: UiState?,
    artistSearchAdapter: ArtistRecyclerViewAdapter?,
    recentSearchAdapter: RecentSearchRecyclerViewAdapter?,
    albumSearchAdapter: AlbumRecyclerViewAdapter?
) {
    when (uiState) {

        is UiState.Success<*> -> {
            when (uiState.dataType) {
                UiState.DataType.ARTIST_SEARCH -> {
                    (view as RecyclerView).adapter = artistSearchAdapter
                    artistSearchAdapter?.submitList(uiState.items as List<Artist>)
                    Log.d("BindingAdapter", "ARTIST_SEARCH " + uiState.items)

                }
                UiState.DataType.RECENT_SEARCH -> {
                    val list = uiState.items as List<RecentArtistSearch>
                    Log.d("BindingAdapter", "RECENT_SEARCH " + uiState.items)
                    (view as RecyclerView).adapter = recentSearchAdapter
                    recentSearchAdapter?.submitList(list.sortedByDescending {
                        it.timeAdded
                    })
                }
                UiState.DataType.ALBUM_SEARCH -> {
                    Log.d("BindingAdapter", "ALBUM_SEARCH" + uiState.items)
                    (view as RecyclerView).adapter = albumSearchAdapter
                    albumSearchAdapter?.submitList(uiState.items as List<Album>)
                }
            }
        }
        else -> {}
    }
}