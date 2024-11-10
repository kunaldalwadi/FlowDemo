package com.example.flowdemo.di

import android.content.Context
import androidx.room.Room
import com.example.flowdemo.db.AppDatabase
import com.example.flowdemo.db.PostDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    fun providePostDao(appDatabase: AppDatabase) : PostDao {
        return appDatabase.getPostDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext applicationContext: Context) : AppDatabase {
        return Room.databaseBuilder(
            context = applicationContext,
            klass = AppDatabase::class.java,
            name = "demo-db"
        ).build()
    }

}