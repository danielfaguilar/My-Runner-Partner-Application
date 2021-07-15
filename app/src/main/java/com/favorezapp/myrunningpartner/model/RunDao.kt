package com.favorezapp.myrunningpartner.model

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface RunDao {
    /**
     * Basic CRUD queries
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(run: Run)
    @Update
    suspend fun update(run: Run)
    @Delete
    suspend fun delete(run: Run)
    @Query("DELETE FROM run_table WHERE id IN (:runId)")
    suspend fun deleteById(runId: Long)
    @Query("DELETE FROM run_table")
    suspend fun deleteAll()
    @Query("SELECT * FROM run_table WHERE id IN (:runId)")
    suspend fun getById(runId: Long): Run
    @Query("SELECT * FROM run_table")
    fun getAll(): LiveData<List<Run>>

    /**
     * Retrieve all items of Run ordered by some attribute
     */
    @Query("SELECT * FROM run_table ORDER BY timestamp DESC")
    fun getAllOrderedByTimestamp(): LiveData<List<Run>>
    @Query("SELECT * FROM run_table ORDER BY duration DESC")
    fun getAllOrderedByDuration(): LiveData<List<Run>>
    @Query("SELECT * FROM run_table ORDER BY caloriesBurned DESC")
    fun getAllOrderedByCaloriesBurned(): LiveData<List<Run>>
    @Query("SELECT * FROM run_table ORDER BY averageSpeed DESC")
    fun getAllOrderedByAverageSpeed(): LiveData<List<Run>>
    @Query("SELECT * FROM run_table ORDER BY distance DESC")
    fun getAllOrderedByDistance(): LiveData<List<Run>>


    /**
     * For statistics purposes, retrieve information about
     * the sum or average of some attribute
     */
    @Query("SELECT SUM(duration) FROM run_table")
    fun getAllTheTimeSpentRunning(): LiveData<Long>
    @Query("SELECT SUM(caloriesBurned) FROM run_table")
    fun getTotalCaloriesBurned(): LiveData<Long>
    @Query("SELECT SUM(distance) FROM run_table")
    fun getAllTheDistanceTraveled(): LiveData<Long>
    @Query("SELECT AVG(averageSpeed) FROM run_table")
    fun getTotalAverageSpeed(): LiveData<Float>


}