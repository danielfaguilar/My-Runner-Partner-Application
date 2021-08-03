package com.favorezapp.myrunningpartner.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.favorezapp.myrunningpartner.R
import com.favorezapp.myrunningpartner.util.preferences.Preferences
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment: Fragment(R.layout.fragment_setup) {
    lateinit var mEtName: TextInputEditText
    lateinit var mEtWeight: TextInputEditText
    lateinit var mTvContinue: TextView

    @Inject
    lateinit var preferences: Preferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if( !preferences.isFirstRun() ) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment, true)
                .build()

            findNavController()
                .navigate(
                    R.id.action_setupFragment_to_runFragment,
                    savedInstanceState,
                    navOptions
                )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root =  super.onCreateView(inflater, container, savedInstanceState)

        root?.apply {
            findViewById<TextView>(R.id.tv_continue)?.setOnClickListener {
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            }

            mEtName = findViewById(R.id.et_name)
            mEtWeight = findViewById(R.id.et_weight)
            mTvContinue = findViewById(R.id.tv_continue)
            mTvContinue.setOnClickListener {
                val success = saveDataToSharedPreferences()
                if( success )
                    findNavController().navigate(R.id.action_setupFragment_to_runFragment)
                else
                    Snackbar.make(requireView(), getString(R.string.all_fields_required), Snackbar.LENGTH_LONG).show()
            }
        }

        return root
    }

    private fun saveDataToSharedPreferences(): Boolean {
        val name = mEtName.text.toString()
        val weight = mEtWeight.text.toString()

        if( name.isEmpty() && weight.isEmpty() )
            return false

        preferences.putUsername(name)
        preferences.putWeight(weight.toFloat())
        preferences.putFirstRun(false)

        // TODO(Set some success message to the toolbar of the host activity)

        return true
    }
}