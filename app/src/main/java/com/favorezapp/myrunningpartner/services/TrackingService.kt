package com.favorezapp.myrunningpartner.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.favorezapp.myrunningpartner.Constants
import com.favorezapp.myrunningpartner.Constants.ACTION_NAVIGATE_TO_TRACKING_FRAGMENT
import com.favorezapp.myrunningpartner.Constants.ACTION_SERVICE_PAUSE
import com.favorezapp.myrunningpartner.Constants.ACTION_SERVICE_START_OR_RESUME
import com.favorezapp.myrunningpartner.Constants.ACTION_SERVICE_STOP
import com.favorezapp.myrunningpartner.Constants.NOTIFICATION_CHANNEL_ID
import com.favorezapp.myrunningpartner.Constants.NOTIFICATION_CHANNEL_NAME
import com.favorezapp.myrunningpartner.Constants.NOTIFICATION_ID
import com.favorezapp.myrunningpartner.R
import com.favorezapp.myrunningpartner.ui.MainActivity
import timber.log.Timber

class TrackingService: LifecycleService() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action) {
                ACTION_SERVICE_START_OR_RESUME -> { log("Started Or Resume") }
                ACTION_SERVICE_PAUSE -> { log("Pause") }
                ACTION_SERVICE_STOP -> { log("Stop") }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun log(msg: String){
        Timber.d(msg)
    }

    fun createForegroundService() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE)
                as NotificationManager

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O )
            createNotificationChannel(notificationManager)

        val notificationBuilder = NotificationCompat
            .Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_run_white)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("00:00:00")
            .setContentIntent(createPendingIntent())

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createPendingIntent(): PendingIntent =
        PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                action = ACTION_NAVIGATE_TO_TRACKING_FRAGMENT
            },
            FLAG_UPDATE_CURRENT
        )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        notificationManager: NotificationManager
    ) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}