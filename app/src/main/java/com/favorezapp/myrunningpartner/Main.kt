package com.favorezapp.myrunningpartner

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class Main: Application() {
    override fun onCreate() {
        super.onCreate()
        // Setup timber logging tool for debug
        Timber.plant(Timber.DebugTree())
    }
}