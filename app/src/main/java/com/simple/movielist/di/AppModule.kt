package com.simple.movielist.di

import com.simple.data.local.DataStoreManager
import org.koin.dsl.module

val appModule = module {
    single { DataStoreManager(get()) }
}
