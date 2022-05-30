package com.elnemr.runningtracker.presentation.ui.statistics

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.elnemr.runningtracker.R
import com.elnemr.runningtracker.databinding.FragmentStatisticsBinding
import com.elnemr.runningtracker.presentation.base.view.BaseFragment
import com.elnemr.runningtracker.presentation.util.LocationUtils
import com.elnemr.runningtracker.presentation.viewmodel.StatisticsViewModel
import com.elnemr.runningtracker.presentation.viewmodel.state.StatisticsViewModelState
import kotlinx.coroutines.flow.buffer
import kotlin.math.round

class StatisticsFragment : BaseFragment(R.layout.fragment_statistics) {

    private lateinit var binding: FragmentStatisticsBinding
    private val viewModel by viewModels<StatisticsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStatisticsBinding.bind(view)

        viewModel.getAllStatistics()
    }

    override fun setUpViewModelStateObservers() {
        lifecycleScope.launchWhenCreated {
            viewModel.getStateFlow().buffer().collect {
                onStateChanged(it)
            }
        }
    }

    private fun onStateChanged(state: StatisticsViewModelState) = when (state) {
        is StatisticsViewModelState.OnTotalCaloriesFetched -> binding.tvTotalCalories.text =
            "${state.result}kcal"
        is StatisticsViewModelState.OnTotalDistanceFetched -> {
            val km = state.result / 1000f
            val totalDistance = round(km * 10f) / 10f
            binding.tvTotalDistance.text = "${totalDistance}km"
        }
        is StatisticsViewModelState.OnTotalTimeInMillisFetched -> binding.tvTotalTime.text =
            LocationUtils.getFormattedStopWatchTime(state.result)
        is StatisticsViewModelState.OnTotalTotalAvgSpeedFetched -> {
            val avgSpeed = round(state.result * 10f) / 10f
            binding.tvAverageSpeed.text = "${avgSpeed}km/h"
        }
    }
}