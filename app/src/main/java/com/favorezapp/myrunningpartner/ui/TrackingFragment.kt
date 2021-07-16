package com.favorezapp.myrunningpartner.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.favorezapp.myrunningpartner.R
import com.favorezapp.myrunningpartner.ui.view_models.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingFragment: Fragment(R.layout.fragment_tracking) {
    private val mainViewModel: MainViewModel by viewModels()
}