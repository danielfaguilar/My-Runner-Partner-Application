package com.favorezapp.myrunningpartner.ui.view_models

import androidx.lifecycle.ViewModel
import com.favorezapp.myrunningpartner.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val mainRepo: MainRepository
): ViewModel()