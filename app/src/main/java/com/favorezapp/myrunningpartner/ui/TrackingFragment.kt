package com.favorezapp.myrunningpartner.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.favorezapp.myrunningpartner.Constants.ACTION_SERVICE_PAUSE
import com.favorezapp.myrunningpartner.Constants.ACTION_SERVICE_START_OR_RESUME
import com.favorezapp.myrunningpartner.R
import com.favorezapp.myrunningpartner.services.Polyline
import com.favorezapp.myrunningpartner.services.TrackingService
import com.favorezapp.myrunningpartner.ui.view_models.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

const val POLYLINE_WIDTH = 8f
const val POLYLINE_COLOR = Color.RED
const val MAP_ZOOM = 15f

@AndroidEntryPoint
class TrackingFragment: Fragment(R.layout.fragment_tracking) {
    private val mainViewModel: MainViewModel by viewModels()
    lateinit var mGoogleMap: GoogleMap
    lateinit var mMapView: MapView
    lateinit var mBtnStartOrStop: MaterialButton
    lateinit var mBtnFinishRun: MaterialButton

    private var mIsTracking = false
    private var mPolylines = mutableListOf<Polyline>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMapView = view.findViewById(R.id.map_view)
        mBtnStartOrStop = view.findViewById(R.id.btn_toggle_run)
        mBtnFinishRun = view.findViewById(R.id.btn_finish_run)
        mMapView.onCreate(savedInstanceState)

        mMapView.getMapAsync {
            mGoogleMap = it
            connectAllCoordinates()
        }

        view.findViewById<Button>(R.id.btn_toggle_run)
            .setOnClickListener {
                startOrStopService()
            }

        subscribeObservers()
    }

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
    }

    private fun startOrStopService() {
        if( mIsTracking )
            startServiceWithAction(ACTION_SERVICE_PAUSE)
        else
            startServiceWithAction(ACTION_SERVICE_START_OR_RESUME)
    }

    private fun showOrHideButtons() {
        if( !mIsTracking ) {
            mBtnStartOrStop.text = getString(R.string.start)
            mBtnFinishRun.visibility = View.VISIBLE
        } else {
            mBtnStartOrStop.text = getString(R.string.stop)
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

    private fun startServiceWithAction(action: String = ACTION_SERVICE_START_OR_RESUME) {
        val intentService = Intent(requireContext(), TrackingService::class.java)
            .apply {
                this.action = action
            }
        requireContext().startService( intentService )
    }
}