package com.simple.movielist

import android.app.Application
import com.simple.data.api.ApiClient
import com.simple.movielist.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        ApiClient.initialize(this)

        startKoin {
            androidContext(this@MyApplication)
            modules(appModule)
        }
    }
}
