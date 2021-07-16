package com.favorezapp.myrunningpartner.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.favorezapp.myrunningpartner.R
import com.favorezapp.myrunningpartner.ui.view_models.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment: Fragment(R.layout.fragment_statistics) {
    private val mainViewModel: StatisticsViewModel by viewModels()
}