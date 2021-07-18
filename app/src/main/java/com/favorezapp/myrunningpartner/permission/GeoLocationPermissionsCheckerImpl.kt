package com.favorezapp.myrunningpartner.permission

import android.Manifest
import android.content.Context
import android.os.Build
import pub.devrel.easypermissions.EasyPermissions

class GeoLocationPermissionsCheckerImpl(val context: Context)
    : GeoLocationPermissionsChecker {
    override val arePermissionsGiven: Boolean
        get() {
            return if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q )
                EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                )
            else
                EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
        }
}