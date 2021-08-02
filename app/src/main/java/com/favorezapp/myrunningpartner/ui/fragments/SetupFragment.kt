package com.favorezapp.myrunningpartner.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.favorezapp.myrunningpartner.R
import dagger.hilt.android.AndroidEntryPoint

class SetupFragment: Fragment(R.layout.fragment_setup) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root =  super.onCreateView(inflater, container, savedInstanceState)

        root?.findViewById<TextView>(R.id.tv_continue)?.setOnClickListener {
            findNavController().navigate(R.id.action_setupFragment_to_runFragment)
        }

        return root
    }
}