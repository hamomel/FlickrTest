package com.example.hamom.flickrtest

import android.app.Application
import com.example.hamom.flickrtest.di.appModule
import com.example.hamom.flickrtest.di.viewModelModule
import org.koin.android.ext.android.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin(this, listOf(appModule, viewModelModule))
    }
}