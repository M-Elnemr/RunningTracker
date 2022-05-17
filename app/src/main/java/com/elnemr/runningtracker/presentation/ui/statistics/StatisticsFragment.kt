package com.elnemr.runningtracker.presentation.ui.statistics

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.elnemr.runningtracker.R
import com.elnemr.runningtracker.databinding.FragmentStatisticsBinding
import com.elnemr.runningtracker.presentation.base.view.BaseFragment
import com.elnemr.runningtracker.presentation.viewmodel.StatisticsViewModel
import com.elnemr.runningtracker.presentation.viewmodel.state.StatisticsViewModelState
import kotlinx.coroutines.flow.buffer

class StatisticsFragment() : BaseFragment(R.layout.fragment_statistics) {

    private lateinit var binding: FragmentStatisticsBinding
    private val viewModel by viewModels<StatisticsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStatisticsBinding.bind(view)
    }

    override fun setUpViewModelStateObservers() {
        lifecycleScope.launchWhenCreated {
            viewModel.getStateFlow().buffer().collect {
                onStateChanged(it)
            }
        }
    }

    private fun onStateChanged(state: StatisticsViewModelState) {
        when (state) {
            else -> {}
        }
    }
}