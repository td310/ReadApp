package com.example.readapp

import android.app.Application
import com.example.readapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ReadApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ReadApp)
            modules(appModule)
        }
    }
}