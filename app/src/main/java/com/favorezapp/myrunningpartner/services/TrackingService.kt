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
import com.favorezapp.myrunningpartner.util.Constants.ACTION_SERVICE_PAUSE
import com.favorezapp.myrunningpartner.util.Constants.ACTION_SERVICE_START_OR_RESUME
import com.favorezapp.myrunningpartner.util.Constants.ACTION_SERVICE_STOP
import com.favorezapp.myrunningpartner.util.Constants.NOTIFICATION_CHANNEL_ID
import com.favorezapp.myrunningpartner.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.favorezapp.myrunningpartner.util.Constants.NOTIFICATION_ID
import com.favorezapp.myrunningpartner.R
import com.favorezapp.myrunningpartner.permission.GeoLocationPermissionsCheckerImpl
import com.favorezapp.myrunningpartner.util.formatTime
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

typealias Polyline = MutableList<LatLng>

@AndroidEntryPoint
class TrackingService: LifecycleService() {
    private val geoLocationPermissionsChecker = GeoLocationPermissionsCheckerImpl(this)
    private val locationCallback = object: LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if( isTracking.value!! )
                addLocationToTheLastPolyline(result.lastLocation)
        }
    }
    private var mIsFirstRun = true
    private var mServiceKilled = false

    private val mTimeRunInSeconds = MutableLiveData<Long>()

    @Inject
    lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    @Inject
    lateinit var mBaseNotificationBuilder: NotificationCompat.Builder

    lateinit var mCurrentNotificationBuilder: NotificationCompat.Builder

    /*
    * The service exposes this properties to external
    * components
    * */
    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val polylines = MutableLiveData<MutableList<Polyline>>()
        val timeRunInMillis = MutableLiveData<Long>()
    }

    private var isTimerEnabled = false
    private var runningLap = 0L
    private var timeRun = 0L
    private var startedTimestamp = 0L
    private var lastSecondTimestamp = 0L

    private fun startTimer() {
        addEmptyPolyline()
        isTracking.postValue(true)
        startedTimestamp = System.currentTimeMillis()
        isTimerEnabled = true

        CoroutineScope(Dispatchers.Main).launch {
            while( isTracking.value!! ) {
                runningLap = System.currentTimeMillis() - startedTimestamp
                timeRunInMillis.postValue( timeRun + runningLap )
                if( timeRunInMillis.value!! >= lastSecondTimestamp + 1000 ) {
                    mTimeRunInSeconds.postValue( mTimeRunInSeconds.value!! + 1 )
                    lastSecondTimestamp += 1000
                }
                delay(50)
            }
            timeRun += runningLap
        }
    }

    private fun killService() {
        mServiceKilled = true
        mIsFirstRun = true
        pauseService()
        postInitialValuesToLiveData()
        stopForeground(true)
        stopSelf()

    }

    @SuppressLint("VisibleForTests")
    override fun onCreate() {
        super.onCreate()
        postInitialValuesToLiveData()

        mCurrentNotificationBuilder = mBaseNotificationBuilder

        isTracking.observe (this) {
            updateIsTracking(it)
            updateNotificationTrackingState(it)
        }
    }

    private fun updateNotificationTrackingState(isTracking: Boolean) {
        val notificationAction = getString(
            if (isTracking) R.string.action_pause
            else R.string.action_resume
        )

        val intent = Intent(this, TrackingService::class.java)
        val action = if( isTracking ) ACTION_SERVICE_PAUSE else ACTION_SERVICE_START_OR_RESUME
        intent.action = action
        val pendingIntent = PendingIntent.getService(this, if(isTracking) 1 else 2, intent, FLAG_UPDATE_CURRENT)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        mCurrentNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(mCurrentNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }

        mCurrentNotificationBuilder = mBaseNotificationBuilder
            .addAction(R.drawable.ic_run_white, notificationAction, pendingIntent)

        notificationManager.notify(NOTIFICATION_ID, mCurrentNotificationBuilder.build())
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
        mTimeRunInSeconds.postValue(0)
        timeRunInMillis.postValue(0)
    }

    private fun addEmptyPolyline() {
        polylines.value?.apply {
            add(mutableListOf())
            polylines.postValue(this)
        } ?: polylines.postValue(mutableListOf(mutableListOf()))
    }

    /**
     * Called whenever we have a change in isTracking live data
     */
    @SuppressLint("MissingPermission")
    private fun updateIsTracking(isTracking: Boolean) {
        if( isTracking ) {
            if( geoLocationPermissionsChecker.arePermissionsGiven ) {
                mFusedLocationProviderClient.requestLocationUpdates(
                    createLocationRequest(),
                    locationCallback,
                    Looper.getMainLooper()
                )
            } else
                Timber.d("Permissions not given")
        } else
            mFusedLocationProviderClient.removeLocationUpdates(locationCallback)
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
        isTimerEnabled = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action) {
                ACTION_SERVICE_START_OR_RESUME -> {
                    if(mIsFirstRun) {
                        log("Started")
                        startForegroundService()
                        mIsFirstRun = false
                    } else {
                        startTimer()
                        log("Resumed")
                    }
                }
                ACTION_SERVICE_PAUSE -> {
                    pauseService()
                    log("Pause")
                }
                ACTION_SERVICE_STOP -> {
                    log("Stop")
                    killService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun log(msg: String){
        Timber.d(msg)
    }

    private fun startForegroundService() {
        startTimer()
        isTracking.postValue(true)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE)
                as NotificationManager

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O )
            createNotificationChannel(notificationManager)

        startForeground(NOTIFICATION_ID, mBaseNotificationBuilder.build())

        mTimeRunInSeconds.observe(this) {
            if( !mServiceKilled ) {
                val notification = mCurrentNotificationBuilder
                    .setContentText(formatTime(it * 1000L))
                notificationManager.notify(NOTIFICATION_ID, notification.build())
            }
        }
    }

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