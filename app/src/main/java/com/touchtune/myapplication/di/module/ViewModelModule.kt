package com.touchtune.myapplication.di.module

import com.touchtune.myapplication.viewmodels.AlbumListViewModel
import com.touchtune.myapplication.viewmodels.ArtistSearchViewModel
import com.touchtune.myapplication.viewmodels.RecentSearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {
    viewModel { AlbumListViewModel(get()) }
    viewModel { RecentSearchViewModel(get()) }
    viewModel { ArtistSearchViewModel(get()) }
}