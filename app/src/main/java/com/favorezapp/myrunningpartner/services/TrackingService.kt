package com.favorezapp.myrunningpartner.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.favorezapp.myrunningpartner.Constants.ACTION_NAVIGATE_TO_TRACKING_FRAGMENT
import com.favorezapp.myrunningpartner.Constants.ACTION_SERVICE_PAUSE
import com.favorezapp.myrunningpartner.Constants.ACTION_SERVICE_START_OR_RESUME
import com.favorezapp.myrunningpartner.Constants.ACTION_SERVICE_STOP
import com.favorezapp.myrunningpartner.Constants.NOTIFICATION_CHANNEL_ID
import com.favorezapp.myrunningpartner.Constants.NOTIFICATION_CHANNEL_NAME
import com.favorezapp.myrunningpartner.Constants.NOTIFICATION_ID
import com.favorezapp.myrunningpartner.R
import com.favorezapp.myrunningpartner.permission.GeoLocationPermissionsCheckerImpl
import com.favorezapp.myrunningpartner.ui.MainActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import timber.log.Timber

typealias Polyline = MutableList<LatLng>

class TrackingService: LifecycleService() {
    private var isFirstRun = true
    private val geoLocationPermissionsChecker =
        GeoLocationPermissionsCheckerImpl(this)
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val locationCallback = object: LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if( isTracking.value != null && isTracking.value == true)
                addLocationToTheLastPolyline(result.lastLocation)
        }
    }

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val polylines = MutableLiveData<MutableList<Polyline>>()
    }

    @SuppressLint("VisibleForTests")
    override fun onCreate() {
        super.onCreate()
        postInitialValuesToLiveData()

        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe (this) {
            updateIsTracking(it)
        }
    }

    private fun addLocationToTheLastPolyline(location: Location?) {
        location?.let { loc ->
            val position = LatLng(loc.latitude, loc.longitude)
            polylines.value?.last()?.add( position )
        }
        polylines.postValue(polylines.value)
    }

    /**
     * Initialize the isTraking  and polylines of the companion
     * object
     */
    private fun postInitialValuesToLiveData() {
        isTracking.postValue(false)
        polylines.postValue(mutableListOf())
    }

    private fun addEmptyPolyline() {
        if( polylines.value == null ) {
            polylines.postValue(mutableListOf(mutableListOf()))
        } else {
            polylines.value!!.add(mutableListOf())
            polylines.postValue(polylines.value)
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateIsTracking(isTracking: Boolean) {
        if( isTracking ) {
            if( geoLocationPermissionsChecker.arePermissionsGiven ) {
                fusedLocationProviderClient.requestLocationUpdates(
                    createLocationRequest(),
                    locationCallback,
                    Looper.getMainLooper()
                )
            } else
                Timber.d("Permissions not given")
        } else
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun createLocationRequest(
        interval: Long = 5000L,
        fastestInterval: Long = 2000L,
        priority: Int = LocationRequest.PRIORITY_HIGH_ACCURACY
    ): LocationRequest =
        LocationRequest.create()
            .setPriority(priority)
            .setFastestInterval(fastestInterval)
            .setInterval(interval)


    private fun pauseService() {
        isTracking.postValue(false)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action) {
                ACTION_SERVICE_START_OR_RESUME -> {
                    if(isFirstRun) {
                        log("Started")
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        startForegroundService()
                        log("Resumed")
                    }
                }
                ACTION_SERVICE_PAUSE -> {
                    pauseService()
                    log("Pause")
                }
                ACTION_SERVICE_STOP -> { log("Stop") }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun log(msg: String){
        Timber.d(msg)
    }

    private fun startForegroundService() {
        addEmptyPolyline()
        isTracking.postValue(true)

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