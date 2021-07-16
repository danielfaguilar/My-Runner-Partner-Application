package com.favorezapp.myrunningpartner.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.favorezapp.myrunningpartner.ui.view_models.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunFragment: Fragment() {
    private val mainViewModel: MainViewModel by viewModels()
}