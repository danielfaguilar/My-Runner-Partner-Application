package com.favorezapp.myrunningpartner.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.favorezapp.myrunningpartner.Constants
import com.favorezapp.myrunningpartner.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var mNavHostFragment: FragmentContainerView
    lateinit var mBottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))
        mNavHostFragment = findViewById(R.id.nav_host_fragment)
        mBottomNavigationView = findViewById(R.id.bottom_navigation_view)

        mBottomNavigationView.setupWithNavController(
            mNavHostFragment.findNavController()
        )

        mNavHostFragment.findNavController()
            .addOnDestinationChangedListener { _, destination, _ ->
                when(destination.id) {
                    R.id.settingsFragment, R.id.runFragment, R.id.statisticsFragment ->
                        mBottomNavigationView.visibility = View.VISIBLE;
                    else -> mBottomNavigationView.visibility = View.GONE
                }
            }

        navigateToTrackingFragmentIfNeeded(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?) {
        if( intent?.action == Constants.ACTION_NAVIGATE_TO_TRACKING_FRAGMENT )
            mNavHostFragment.findNavController().navigate(R.id.action_MainActivity_to_trackingFragment)
    }
}