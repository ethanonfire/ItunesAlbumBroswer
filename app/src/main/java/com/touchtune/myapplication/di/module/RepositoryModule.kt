package com.touchtune.myapplication.di.module

import com.touchtune.myapplication.data.DefaultRepository
import org.koin.dsl.module

val repoModule = module {
    single {
        DefaultRepository(get(), get())
    }
}