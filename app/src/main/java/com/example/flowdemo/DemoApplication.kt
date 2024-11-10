package com.example.flowdemo

import android.app.Application
import androidx.room.Room
import com.example.flowdemo.db.AppDatabase

class DemoApplication : Application() {

    companion object {
        lateinit var roomDB: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()

        roomDB = Room.databaseBuilder(
            context = this.applicationContext,
            klass = AppDatabase::class.java,
            name = "demo-db"
        ).build()
    }
}