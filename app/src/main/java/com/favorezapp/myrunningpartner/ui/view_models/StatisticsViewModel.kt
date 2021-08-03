package com.favorezapp.myrunningpartner.ui.view_models

import androidx.lifecycle.ViewModel
import com.favorezapp.myrunningpartner.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    mainRepo: MainRepository
): ViewModel() {
    val mAllTheTimeSpentRunning = mainRepo.getAllTheTimeSpentRunning()
    val mAllTheDistanceTraveled = mainRepo.getAllTheDistanceTraveled()
    val mTotalAvgSpeed = mainRepo.getTotalAverageSpeed()
    val mTotalCaloriesBurned = mainRepo.getTotalCaloriesBurned()
    val runsSortedByDate = mainRepo.getAllOrderedByDate()
}