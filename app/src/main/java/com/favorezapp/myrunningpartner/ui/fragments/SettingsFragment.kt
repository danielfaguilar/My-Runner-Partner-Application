package com.favorezapp.myrunningpartner.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.favorezapp.myrunningpartner.R
import com.favorezapp.myrunningpartner.util.preferences.Preferences
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment: Fragment(R.layout.fragment_settings) {
    @Inject
    lateinit var preferences: Preferences

    lateinit var mEtName: TextInputEditText
    lateinit var mEtWeight: TextInputEditText
    lateinit var mBtnSaveChanges: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)

        root?.apply {
            mEtName = findViewById(R.id.et_name)
            mEtWeight = findViewById(R.id.et_weight)
            mBtnSaveChanges = findViewById(R.id.btn_save_changes)
            mBtnSaveChanges.setOnClickListener {
                val success = saveDataToSharedPrefs()
                val message = getString(if (success) R.string.success else R.string.failure)
                Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
            }
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadDataFromSharedPrefs()
    }

    private fun loadDataFromSharedPrefs() {
        mEtName.setText(preferences.getUserName())
        mEtWeight.setText(preferences.getWeight().toString())
    }

    private fun saveDataToSharedPrefs(): Boolean {
        val username = mEtName.text.toString()
        val weight = mEtWeight.text.toString()

        if( username.isEmpty() || weight.isEmpty() )
            return false

        preferences.apply {
            putUsername(username)
            putWeight(weight.toFloat())
        }

        return true
    }
}