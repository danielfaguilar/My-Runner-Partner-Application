package com.favorezapp.myrunningpartner.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.favorezapp.myrunningpartner.Constants
import com.favorezapp.myrunningpartner.R
import com.favorezapp.myrunningpartner.services.TrackingService
import com.favorezapp.myrunningpartner.ui.view_models.MainViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingFragment: Fragment(R.layout.fragment_tracking) {
    private val mainViewModel: MainViewModel by viewModels()
    lateinit var mGoogleMap: GoogleMap
    lateinit var mMapView: MapView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMapView = view.findViewById(R.id.map_view)
        mMapView.onCreate(savedInstanceState)

        mMapView.getMapAsync {
            mGoogleMap = it
        }

        view.findViewById<Button>(R.id.btn_toggle_run)
            .setOnClickListener {
                startServiceWithAction(Constants.ACTION_SERVICE_START_OR_RESUME)
            }
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

    private fun startServiceWithAction(action: String) {
        val intentService = Intent(requireContext(), TrackingService::class.java)
            .apply {
                this.action = action
            }
        requireContext().startService( intentService )
    }
}