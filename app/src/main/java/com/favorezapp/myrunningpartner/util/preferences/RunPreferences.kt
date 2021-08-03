package com.favorezapp.myrunningpartner.util.preferences

import android.content.Context
import android.content.SharedPreferences
import com.favorezapp.myrunningpartner.util.preferences.PreferencesConstants.FIRST_RUN
import com.favorezapp.myrunningpartner.util.preferences.PreferencesConstants.USER_NAME
import com.favorezapp.myrunningpartner.util.preferences.PreferencesConstants.WEIGHT
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RunPreferences @Inject constructor(
    @ApplicationContext context: Context
): Preferences {

    companion object {
        const val SHARED_PREFERENCES_NAME = "RunPreferences"
    }

    private val preferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun putUsername(username: String) {
        edit { putString(USER_NAME ,username) }
    }

    override fun putWeight(weight: Float) {
        edit{ putFloat(WEIGHT, weight) }
    }

    override fun putFirstRun(isFirstRun: Boolean) {
        edit { putBoolean(FIRST_RUN, isFirstRun) }
    }

    override fun getUserName() =
        preferences.getString(USER_NAME, "") ?: ""

    override fun getWeight() =
        preferences.getFloat(WEIGHT, 0f)

    override fun isFirstRun() =
        preferences.getBoolean(FIRST_RUN, true)

    private inline fun edit( block: SharedPreferences.Editor.() -> Unit ) {
        with(preferences.edit()) {
            block()
            commit()
        }
    }
}