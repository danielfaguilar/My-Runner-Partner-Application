package com.favorezapp.myrunningpartner.repositories

import androidx.lifecycle.LiveData
import com.favorezapp.myrunningpartner.db.Run
import com.favorezapp.myrunningpartner.db.RunDao
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val runDao: RunDao
) {
    suspend fun insert(run: Run) = runDao.insert(run)
    suspend fun delete(run: Run) = runDao.delete(run)
    suspend fun update(run: Run) = runDao.update(run)
    suspend fun getById(id: Long) = runDao.getById(id)

    fun getAllOrderedByDate(): LiveData<List<Run>> =
        runDao.getAllOrderedByDate()
    fun getAllOrderedByDistance(): LiveData<List<Run>> =
        runDao.getAllOrderedByDistance()
    fun getAllOrderedByDuration(): LiveData<List<Run>> =
        runDao.getAllOrderedByDuration()
    fun getAllOrderedByAverageSpeed(): LiveData<List<Run>> =
        runDao.getAllOrderedByAverageSpeed()
    fun getAllOrderedByCaloriesBurned(): LiveData<List<Run>> =
        runDao.getAllOrderedByCaloriesBurned()

    fun getAllTheTimeSpentRunning(): LiveData<Long> =
        runDao.getAllTheTimeSpentRunning()
    fun getAllTheDistanceTraveled(): LiveData<Long> =
        runDao.getAllTheDistanceTraveled()
    fun getTotalCaloriesBurned(): LiveData<Long> =
        runDao.getTotalCaloriesBurned()
    fun getTotalAverageSpeed(): LiveData<Float> =
        runDao.getTotalAverageSpeed()
}