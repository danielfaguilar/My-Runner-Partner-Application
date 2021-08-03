package com.favorezapp.myrunningpartner.di

import com.favorezapp.myrunningpartner.util.preferences.Preferences
import com.favorezapp.myrunningpartner.util.preferences.RunPreferences
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferencesModule {
    @Binds
    abstract fun bindsRunPreferences(preferences: RunPreferences): Preferences
    @Provides
    fun providesWeight(preferences: Preferences) = preferences.getWeight()
}