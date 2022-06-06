package com.elnemr.runningtracker.presentation.ui.statistics

import android.graphics.Color.*
import android.os.Bundle
import android.view.View
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.elnemr.runningtracker.R
import com.elnemr.runningtracker.databinding.FragmentStatisticsBinding
import com.elnemr.runningtracker.presentation.base.view.BaseFragment
import com.elnemr.runningtracker.presentation.util.CustomMarkerView
import com.elnemr.runningtracker.presentation.util.LocationUtils
import com.elnemr.runningtracker.presentation.viewmodel.StatisticsViewModel
import com.elnemr.runningtracker.presentation.viewmodel.state.StatisticsViewModelState
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.android.synthetic.main.fragment_statistics.*
import kotlinx.coroutines.flow.buffer
import kotlin.math.round

class StatisticsFragment : BaseFragment(R.layout.fragment_statistics) {

    private lateinit var binding: FragmentStatisticsBinding
    private val viewModel by viewModels<StatisticsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStatisticsBinding.bind(view)

        viewModel.getAllStatistics()
        setUpBarChart()
    }

    override fun setUpViewModelStateObservers() {
        lifecycleScope.launchWhenCreated {
            viewModel.getStateFlow().buffer().collect {
                onStateChanged(it)
            }
        }
    }

    private fun setUpBarChart(){
        binding.barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = WHITE
            textColor = WHITE
            setDrawGridLines(false)
        }

        binding.barChart.axisLeft.apply {
                axisLineColor = WHITE
                textColor = WHITE
                setDrawGridLines(false)
        }
        binding.barChart.axisRight.apply {
                axisLineColor = WHITE
                textColor = WHITE
                setDrawGridLines(false)
        }

        binding.barChart.apply {
            description.text = "Avg Speed OVer Time"
            legend.isEnabled = false
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
        is StatisticsViewModelState.OnRunsFetched ->state.runs.let {
            val allAvgSpeed = it.indices.map{ i -> BarEntry(i.toFloat(), it[i].avgSpeedInKMH) }
            val barDataSet = BarDataSet(allAvgSpeed, "Avg Speed Over Time").apply {
                valueTextColor = WHITE
                color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
            }

            binding.barChart.data = BarData(barDataSet)
            binding.barChart.marker = CustomMarkerView(it.reversed(), requireContext(), R.layout.marker_view)
            binding.barChart.invalidate()
        }
    }
}