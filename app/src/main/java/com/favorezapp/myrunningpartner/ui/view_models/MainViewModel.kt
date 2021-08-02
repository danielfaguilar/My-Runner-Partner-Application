package com.favorezapp.myrunningpartner.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.favorezapp.myrunningpartner.db.Run
import com.favorezapp.myrunningpartner.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepo: MainRepository
): ViewModel() {
    val runsSortedByDate = mainRepo.getAllOrderedByDate()

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