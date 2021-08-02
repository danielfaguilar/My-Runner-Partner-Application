package com.favorezapp.myrunningpartner.di

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.favorezapp.myrunningpartner.util.Constants
import com.favorezapp.myrunningpartner.R
import com.favorezapp.myrunningpartner.ui.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {
    @ServiceScoped
    @Provides
    fun providesPendingIntent(
        @ApplicationContext context: Context
    ):PendingIntent { return PendingIntent.getActivity(
        context, 0,
        Intent(context, MainActivity::class.java).apply {
            action = Constants.ACTION_NAVIGATE_TO_TRACKING_FRAGMENT
        },
        PendingIntent.FLAG_UPDATE_CURRENT)
    }
    @ServiceScoped
    @Provides
    fun providesBaseNotificationBuilder(
        @ApplicationContext context: Context,
        pendingIntent: PendingIntent
    ) = NotificationCompat
            .Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_run_white)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText("00:00:00")
            .setContentIntent(pendingIntent)

    @SuppressLint("VisibleForTests")
    @ServiceScoped
    @Provides
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ) = FusedLocationProviderClient( context )

}