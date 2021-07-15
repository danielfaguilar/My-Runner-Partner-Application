package com.favorezapp.myrunningpartner.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Describe the run
 * TODO("Change timestamp property to Date type")
 * @param img represents the route taken by the person in the map
 */
@Entity(tableName = "run_table")
data class Run(
    var img: Bitmap? = null,
    var timestamp: Long = 0L,
    var averageSpeed: Float = 0F,
    var distance: Int,
    var duration: Long = 0L,
    var caloriesBurned: Int = 0
){
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}