package com.ferdinand.pdftestapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/*
* The application class which we use for DI and initialising Timber logging
* */
@HiltAndroidApp
class PdfTestApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initTimber()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}