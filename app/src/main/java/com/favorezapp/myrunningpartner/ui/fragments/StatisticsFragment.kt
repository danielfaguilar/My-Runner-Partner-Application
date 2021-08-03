package com.favorezapp.myrunningpartner.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.favorezapp.myrunningpartner.R
import com.favorezapp.myrunningpartner.ui.view_models.StatisticsViewModel
import com.favorezapp.myrunningpartner.util.formatTime
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round

@AndroidEntryPoint
class StatisticsFragment: Fragment(R.layout.fragment_statistics) {
    private val mainViewModel: StatisticsViewModel by viewModels()

    lateinit var mTVTotalDistance: MaterialTextView
    lateinit var mTVTotalTime: MaterialTextView
    lateinit var mTVTotalCalories: MaterialTextView
    lateinit var mTVAvgSpeed: MaterialTextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)

        root?.apply {
            mTVTotalDistance = findViewById(R.id.tv_total_distance)
            mTVTotalTime = findViewById(R.id.tv_total_time)
            mTVTotalCalories = findViewById(R.id.tv_total_calories)
            mTVAvgSpeed = findViewById(R.id.mtv_avg_speed)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        mainViewModel.mAllTheTimeSpentRunning.observe(viewLifecycleOwner) {
            it?.let {
                val totalTimeSpentRunningFormatted = formatTime(it)
                mTVTotalTime.text = totalTimeSpentRunningFormatted
            }
        }
        mainViewModel.mAllTheDistanceTraveled.observe(viewLifecycleOwner) {
            it?.let {
                val totalDistanceTraveledInKm = it / 1000f
                val totalDistance = round(totalDistanceTraveledInKm * 10f) / 10f
                val totalDistanceString = "${totalDistance}km"
                mTVTotalDistance.text = totalDistanceString
            }
        }
        mainViewModel.mTotalAvgSpeed.observe(viewLifecycleOwner) {
            it?.let {
                val avgSpeed = round(it * 10f) / 10f
                val avgSpeedString = "${avgSpeed}km"
                mTVAvgSpeed.text = avgSpeedString
            }
        }
        mainViewModel.mTotalCaloriesBurned.observe(viewLifecycleOwner) {
            it?.let{
                val totalCaloriesBurned = "{$it}kcal"
                mTVTotalCalories.text = totalCaloriesBurned
            }
        }
    }
}