package com.favorezapp.myrunningpartner.permission

/**
 * Abstraction which allows to manage the permission check
 */
interface GeoLocationPermissionsChecker {
    /**
     * @return True if the permission has been provided
     */
    val arePermissionsGiven: Boolean
}
