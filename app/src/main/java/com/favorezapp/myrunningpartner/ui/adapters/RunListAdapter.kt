package com.favorezapp.myrunningpartner.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.favorezapp.myrunningpartner.R
import com.favorezapp.myrunningpartner.db.Run
import com.favorezapp.myrunningpartner.util.formatTime
import com.google.android.material.textview.MaterialTextView
import java.text.SimpleDateFormat
import java.util.*

val DIFF_UTIL = object: DiffUtil.ItemCallback<Run>() {
    override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }
}

class RunListAdapter: ListAdapter<Run, RunListAdapter.RunViewHolder>(DIFF_UTIL) {

    inner class RunViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivRunImage: ImageView = itemView.findViewById(R.id.iv_run_image)
        val mtvDate: MaterialTextView = itemView.findViewById(R.id.mtv_date)
        val mtvTime: MaterialTextView = itemView.findViewById(R.id.mtv_time)
        val mtvDistance: MaterialTextView = itemView.findViewById(R.id.mtv_distance)
        val mtvCaloriesBurned: MaterialTextView = itemView.findViewById(R.id.mtv_calories)
        val mtvAvgSpeed: MaterialTextView = itemView.findViewById(R.id.mtv_avg_speed)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_run_1, parent, false)
        return RunViewHolder(view)
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = getItem(position)

        holder.apply {
            Glide.with(this.itemView).load(run.img).into(ivRunImage)

            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.timestamp
            }

            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            mtvDate.text = dateFormat.format(calendar.time)

            val avgSpeed = "${run.averageSpeed}km/h"
            mtvAvgSpeed.text = avgSpeed

            val distanceInKm = "${run.distance / 1000f}km"
            mtvDistance.text = distanceInKm

            mtvTime.text = formatTime(run.duration)

            val caloriesBurned = "${run.caloriesBurned}kcal"
            mtvCaloriesBurned.text = caloriesBurned
        }
    }
}