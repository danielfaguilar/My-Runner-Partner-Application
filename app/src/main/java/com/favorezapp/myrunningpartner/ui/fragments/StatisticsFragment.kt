package com.favorezapp.myrunningpartner.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.favorezapp.myrunningpartner.R
import com.favorezapp.myrunningpartner.ui.view_models.StatisticsViewModel
import com.favorezapp.myrunningpartner.util.CustomMarkerView
import com.favorezapp.myrunningpartner.util.formatTime
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
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
    lateinit var mBarChart: BarChart

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
            mBarChart = findViewById(R.id.bar_chart)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        setupChart()
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

    private fun setupChart() {
        mBarChart.xAxis.apply {
            XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        mBarChart.axisLeft.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        mBarChart.axisRight.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        mBarChart.apply {
            description.text = "Avg speed over time"
            legend.isEnabled = false
        }

        mainViewModel.runsSortedByDate.observe(viewLifecycleOwner) {
            it?.let {
                val allAvgSpeeds = it.indices.map { i -> BarEntry(i.toFloat(), it[i].averageSpeed) }
                val dataSet = BarDataSet(allAvgSpeeds, "Avg Speed Over Time").apply {
                    valueTextColor = Color.WHITE
                    color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
                }

                mBarChart.marker = CustomMarkerView(it.reversed(), requireContext(), R.layout.marker_view)
                mBarChart.data = BarData(dataSet)
                mBarChart.invalidate()
            }
        }
    }
}