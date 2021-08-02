package com.favorezapp.myrunningpartner.ui.fragments

import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.favorezapp.myrunningpartner.util.Constants.ACTION_SERVICE_PAUSE
import com.favorezapp.myrunningpartner.util.Constants.ACTION_SERVICE_START_OR_RESUME
import com.favorezapp.myrunningpartner.util.Constants.ACTION_SERVICE_STOP
import com.favorezapp.myrunningpartner.R
import com.favorezapp.myrunningpartner.db.Run
import com.favorezapp.myrunningpartner.services.Polyline
import com.favorezapp.myrunningpartner.services.TrackingService
import com.favorezapp.myrunningpartner.ui.view_models.MainViewModel
import com.favorezapp.myrunningpartner.util.formatTime
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.math.roundToInt

const val POLYLINE_WIDTH = 8f
const val POLYLINE_COLOR = Color.RED
const val MAP_ZOOM = 15f

@AndroidEntryPoint
class TrackingFragment: Fragment(R.layout.fragment_tracking) {
    // ViewModel provided by HiltViewModel
    private val mainViewModel: MainViewModel by viewModels()

    // Views
    lateinit var mBtnStartOrStop: MaterialButton
    lateinit var mBtnFinishRun: MaterialButton
    lateinit var mMapView: MapView
    lateinit var mTvTimer: MaterialTextView

    // Google map
    lateinit var mGoogleMap: GoogleMap

    // Observed from the service
    private var mPolylines = mutableListOf<Polyline>()
    private var mIsTracking = false
    private var mTimeRunInMillis = 0L

    // Dummy domain variable
    private var weight: Float = 0f

    // Menu with single item to cancel run
    private var menu: Menu? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind views
        mMapView = view.findViewById(R.id.map_view)
        mBtnStartOrStop = view.findViewById(R.id.btn_toggle_run)
        mBtnFinishRun = view.findViewById(R.id.btn_finish_run)
        mTvTimer = view.findViewById(R.id.tv_timer)

        // Map Lifecycle
        mMapView.onCreate(savedInstanceState)

        // Get the map and assign it to member variable
        // Subscribe observers and connect all coordinates if needed
        mMapView.getMapAsync {
            mGoogleMap = it
            subscribeObservers()
            connectAllCoordinates()
        }

        // Set the listeners
        view.findViewById<Button>(R.id.btn_toggle_run)
            .setOnClickListener {
                startOrStopService()
            }
        view.findViewById<Button>(R.id.btn_finish_run)
            .setOnClickListener {
                endAndSaveRunToDb()
            }
    }

    /**
     * Create the actual run and save it using
     * the repository
     * Also stop the service and navigate back
     */
    private fun endAndSaveRunToDb() {
        mGoogleMap.snapshot {
            val distanceInMeters = showTheFullPathAndCalculateDistance().toInt()
            val avgSpeed = ((distanceInMeters / 1000f) / (mTimeRunInMillis / 1000f / 60 / 60) * 10).roundToInt() / 10f
            val dateTimestamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()
            val run = Run(
                it,
                dateTimestamp,
                avgSpeed,
                distanceInMeters,
                mTimeRunInMillis,
                caloriesBurned
            )

            mainViewModel.insertRun(run)

            Snackbar.make(requireActivity().findViewById(R.id.root_view),
                R.string.run_saved, Snackbar.LENGTH_LONG)
                .show()

            stopServiceRunAndNavigateBack()
        }
    }

    /**
     * Calculate the distance for the given polyline
     */
    private fun calculateDistanceFor(polyline: Polyline): Float {
        var distance = 0f

        for (i in 1 until polyline.size) {
            val p1 = polyline[ i - 1 ]
            val p2 = polyline[ i ]

            val floatArray = FloatArray(1)
            Location.distanceBetween(
                p1.latitude, p1.longitude,
                p2.latitude, p2.longitude,
                floatArray
            )

            distance += floatArray[0]
        }

        return distance
    }

    /**
     * Stop the service and navigate back
     */
    private fun stopServiceRunAndNavigateBack() {
        startOrStopServiceWithAction(ACTION_SERVICE_STOP)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }

    /**
     * Subscribe the observers
     */
    private fun subscribeObservers() {
        TrackingService.isTracking.observe(viewLifecycleOwner) {
            mIsTracking = it
            showOrHideButtons()
        }
        TrackingService.polylines.observe(viewLifecycleOwner) {
            mPolylines = it
            connectLastTwoCoordinates()
            moveCameraToTheUser()
        }
        TrackingService.timeRunInMillis.observe(viewLifecycleOwner) {
            mTimeRunInMillis = it
            val formattedTime = formatTime(mTimeRunInMillis, true)
            mTvTimer.text = formattedTime
        }
    }

    /**
     * Start or stop service based on the truth value
     * of the mIsTracking variable
     */
    private fun startOrStopService() {
        if( mIsTracking ) {
            menu?.getItem(0)?.isVisible = true
            startOrStopServiceWithAction(ACTION_SERVICE_PAUSE)
        }
        else
            startOrStopServiceWithAction(ACTION_SERVICE_START_OR_RESUME)
    }

    /**
     * Show or hide buttons
     */
    private fun showOrHideButtons() {
        if( !mIsTracking ) {
            mBtnStartOrStop.text = getString(R.string.start)
            mBtnFinishRun.visibility = View.VISIBLE
        } else {
            mBtnStartOrStop.text = getString(R.string.stop)
            menu?.getItem(0)?.isVisible = true
            mBtnFinishRun.visibility = View.GONE
        }
    }

    private fun moveCameraToTheUser() {
        if( mPolylines.isNotEmpty() && mPolylines.last().isNotEmpty() ) {
            mGoogleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    mPolylines.last().last(), MAP_ZOOM
                )
            )
        }
    }

    private fun connectAllCoordinates() {
        for (polyline in mPolylines) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll( polyline )
            mGoogleMap.addPolyline(polylineOptions)
        }
    }

    private fun connectLastTwoCoordinates() {
        if( mPolylines.isEmpty() || mPolylines.last().size < 2 )
            return

        val preLastPoint = mPolylines.last()[ mPolylines.last().size - 2]
        val lastPoint = mPolylines.last().last()

        val polylineOptions = PolylineOptions()
            .color(POLYLINE_COLOR)
            .width(POLYLINE_WIDTH)
            .add( preLastPoint )
            .add(lastPoint)

        mGoogleMap.addPolyline(polylineOptions)
    }

    private fun startOrStopServiceWithAction(action: String = ACTION_SERVICE_START_OR_RESUME) {
        val intentService = Intent(requireContext(), TrackingService::class.java)
            .apply {
                this.action = action
            }
        requireContext().startService( intentService )
    }

    private fun showTheFullPathAndCalculateDistance(): Float {
        var distance = 0f
        val bounds = LatLngBounds.Builder()

        for( polyline in mPolylines ) {
            distance += calculateDistanceFor( polyline )
            for (latLng in polyline) {
                bounds.include(latLng)
            }
        }

        mGoogleMap.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mMapView.width, mMapView.height,
                (mMapView.height * 0.05).toInt()
            )
        )

        return distance
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }
    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }
    override fun onStop() {
        super.onStop()
        mMapView.onStop()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        mMapView.onDestroy()
    }
    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }
}