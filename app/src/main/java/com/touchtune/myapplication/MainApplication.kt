package com.touchtune.myapplication

import android.app.Application
import android.content.Context
import com.touchtune.myapplication.di.module.appModule
import com.touchtune.myapplication.di.module.repoModule
import com.touchtune.myapplication.di.module.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {

//    val appRepository: Repository
//        get() = ServiceLocator.provideAppRepository(this)

    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this

        startKoin {
            androidContext(this@MainApplication)
            modules(listOf(appModule, repoModule, viewModelModule))
        }
    }
}