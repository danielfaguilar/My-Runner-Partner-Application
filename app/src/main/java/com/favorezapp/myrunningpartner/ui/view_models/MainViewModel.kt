package com.favorezapp.myrunningpartner.ui.view_models

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.favorezapp.myrunningpartner.db.Run
import com.favorezapp.myrunningpartner.repositories.MainRepository
import com.favorezapp.myrunningpartner.util.RunSortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepo: MainRepository
): ViewModel() {
    private val runsSortedByDate = mainRepo.getAllOrderedByDate()
    private val runsSortedByDistance = mainRepo.getAllOrderedByDistance()
    private val runsSortedByCaloriesBurned = mainRepo.getAllOrderedByCaloriesBurned()
    private val runsSortedByAverageSpeed = mainRepo.getAllOrderedByAverageSpeed()
    private val runsSortedByDuration = mainRepo.getAllOrderedByDuration()

    val runsMediator = MediatorLiveData<List<Run>>()
    var sortType = RunSortType.DATE

    init {
        addLiveDataSourcesTo( runsMediator )
    }

    private fun addLiveDataSourcesTo(runs: MediatorLiveData<List<Run>>) {
        runs.addSource(runsSortedByDate) {
            if( sortType == RunSortType.DATE ) runsMediator.value = it
        }
        runs.addSource(runsSortedByDistance) {
            if( sortType == RunSortType.DISTANCE ) runsMediator.value = it
        }
        runs.addSource(runsSortedByCaloriesBurned) {
            if( sortType == RunSortType.CALORIES_BURNED ) runsMediator.value = it
        }
        runs.addSource(runsSortedByAverageSpeed) {
            if( sortType == RunSortType.AVG_SPEED ) runsMediator.value = it
        }
        runs.addSource(runsSortedByDuration) {
            if( sortType == RunSortType.DURATION ) runsMediator.value = it
        }
    }
    fun sortRuns(sortType: RunSortType) = when( sortType ) {
        RunSortType.DATE -> runsSortedByDate.value.let { runsMediator.value = it }
        RunSortType.DISTANCE -> runsSortedByDistance.value.let { runsMediator.value = it }
        RunSortType.DURATION-> runsSortedByDuration.value.let { runsMediator.value = it }
        RunSortType.AVG_SPEED -> runsSortedByAverageSpeed.value.let { runsMediator.value = it }
        RunSortType.CALORIES_BURNED -> runsSortedByCaloriesBurned.value.let { runsMediator.value = it }
    }.also { this.sortType = sortType }

    fun insertRun(run: Run) = viewModelScope
        .launch {
            mainRepo.insert(run)
        }
    fun deleteAllRuns() {
        viewModelScope.launch {
            mainRepo.deleteAllRuns()
        }
    }


}