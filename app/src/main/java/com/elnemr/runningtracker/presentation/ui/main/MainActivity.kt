package com.elnemr.runningtracker.presentation.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.elnemr.runningtracker.databinding.ActivityMainBinding
import com.elnemr.runningtracker.presentation.base.view.BaseActivity
import com.elnemr.runningtracker.presentation.viewmodel.MainViewModel
import com.elnemr.runningtracker.presentation.viewmodel.state.MainViewModelState
import kotlinx.coroutines.flow.buffer

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun setUpViewModelStateObservers() {
        lifecycleScope.launchWhenCreated {
            viewModel.getStateFlow().buffer().collect{
                onStateChanged(it)
            }
        }
    }

    private fun onStateChanged(state: MainViewModelState) {
        when(state){
            else -> {}
        }
    }
}