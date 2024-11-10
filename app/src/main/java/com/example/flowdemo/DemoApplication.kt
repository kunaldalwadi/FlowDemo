package com.example.flowdemo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}