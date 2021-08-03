package com.favorezapp.myrunningpartner.util

import android.content.Context
import android.view.View
import android.widget.TextView
import com.favorezapp.myrunningpartner.R
import com.favorezapp.myrunningpartner.db.Run
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*

class CustomMarkerView(
    private val runs: List<Run>,
    context: Context,
    resId: Int,

    ): MarkerView(context, resId){

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if( e == null ) return
        val runIdx = e.x.toInt()
        val run = runs[ runIdx ]

        val calendar = Calendar.getInstance().apply {
            timeInMillis = run.timestamp
        }

        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        findViewById<TextView>(R.id.tv_date).text = dateFormat.format(calendar.time)

        val avgSpeed = "${run.averageSpeed}km/h"
        findViewById<TextView>(R.id.tv_average_speed).text = avgSpeed

        val distanceInKm = "${run.distance / 1000f}km"
        findViewById<TextView>(R.id.tv_distance).text = distanceInKm

        findViewById<TextView>(R.id.tv_duration).text = formatTime(run.duration)

        val caloriesBurned = "${run.caloriesBurned}kcal"
        findViewById<TextView>(R.id.tv_calories_burned).text = caloriesBurned
    }
}