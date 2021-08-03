package com.favorezapp.myrunningpartner.util.preferences

interface Preferences {
    fun putUsername(username: String)
    fun putWeight(weight: Float)
    fun putFirstRun(isFirstRun: Boolean)

    fun getUserName(): String
    fun getWeight(): Float
    fun isFirstRun(): Boolean
}