package com.favorezapp.myrunningpartner.di

import android.content.Context
import androidx.room.Room
import com.favorezapp.myrunningpartner.model.DB_NAME
import com.favorezapp.myrunningpartner.model.RunDao
import com.favorezapp.myrunningpartner.model.RunDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {
    @Provides
    @Singleton
    fun provideRunDatabase(@ApplicationContext context: Context): RunDatabase =
        Room.databaseBuilder(context, RunDatabase::class.java, DB_NAME).build()

    @Provides
    @Singleton
    fun provideRunDao(database: RunDatabase): RunDao =
        database.runDao()
}