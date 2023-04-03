package com.touchtune.myapplication

import android.app.Application
import android.content.Context
import com.touchtune.myapplication.data.Repository

class MainApplication : Application() {

    val appRepository: Repository
        get() = ServiceLocator.provideAppRepository(this)

    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }

}