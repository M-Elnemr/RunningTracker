package com.elnemr.runningtracker.presentation.ui.tracking

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.elnemr.runningtracker.R
import com.elnemr.runningtracker.databinding.FragmentRunBinding
import com.elnemr.runningtracker.databinding.FragmentSetupBinding
import com.elnemr.runningtracker.databinding.FragmentTrackingBinding
import com.elnemr.runningtracker.presentation.base.view.BaseFragment
import com.elnemr.runningtracker.presentation.viewmodel.MainViewModel
import com.elnemr.runningtracker.presentation.viewmodel.state.MainViewModelState
import kotlinx.coroutines.flow.buffer

class TrackingFragment() : BaseFragment(R.layout.fragment_tracking) {

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: FragmentTrackingBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrackingBinding.bind(view)
    }

    override fun setUpViewModelStateObservers() {
        lifecycleScope.launchWhenCreated {
            viewModel.getStateFlow().buffer().collect {
                onStateChanged(it)
            }
        }
    }

    private fun onStateChanged(state: MainViewModelState) {
        when (state) {
            else -> {}
        }
    }}