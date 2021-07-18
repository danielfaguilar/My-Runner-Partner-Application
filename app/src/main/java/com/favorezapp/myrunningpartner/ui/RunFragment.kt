package com.favorezapp.myrunningpartner.ui

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.favorezapp.myrunningpartner.R
import com.favorezapp.myrunningpartner.permission.GeoLocationPermissionsChecker
import com.favorezapp.myrunningpartner.permission.GeoLocationPermissionsCheckerImpl
import com.favorezapp.myrunningpartner.ui.view_models.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

const val LOCATION_PERMISSIONS_REQUEST_CODE = 1;

@AndroidEntryPoint
class RunFragment: Fragment(R.layout.fragment_run), EasyPermissions.PermissionCallbacks{
    private val mainViewModel: MainViewModel by viewModels()
    lateinit var locationPermissionChecker: GeoLocationPermissionsChecker

    /**
     * Initialize the location permission checker with the host component context
     * @param context of the host Activity
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        locationPermissionChecker = GeoLocationPermissionsCheckerImpl(context)
    }

    /**
     * Set listener for the fab button and navigate to another fragment using
     * the nav controller
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)
        root?.findViewById<FloatingActionButton>(R.id.fab_add_run)
            ?.setOnClickListener {
                findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
            }
        return root
    }

    /**
     * Delegate the result provided by the android framework to Easy Permissions
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    /**
     * If permissions are not given check if are permanently denied, if so use the easy permissions dialog
     * to navigate to settings of the app
     */
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if( EasyPermissions.somePermissionPermanentlyDenied(this, perms) )
            AppSettingsDialog.Builder(this).build().show()
        else
            requestLocationPermissions()
    }

    // If permissions are given everything is fine so do nothing
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    /**
     * Request the permissions if needed based on the Build Version
     */
    private fun requestLocationPermissions() {
        if( locationPermissionChecker.arePermissionsGiven )
            return

        val rationaleMessage = requireContext().getString(R.string.rationale_request_location_permissions)

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q )
            EasyPermissions.requestPermissions(
                this,
                rationaleMessage,
                LOCATION_PERMISSIONS_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        else
            EasyPermissions.requestPermissions(
                this,
                rationaleMessage,
                LOCATION_PERMISSIONS_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
    }
}
