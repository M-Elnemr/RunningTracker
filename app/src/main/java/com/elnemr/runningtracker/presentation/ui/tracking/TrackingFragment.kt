package com.elnemr.runningtracker.presentation.ui.tracking

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.elnemr.runningtracker.R
import com.elnemr.runningtracker.databinding.FragmentTrackingBinding
import com.elnemr.runningtracker.presentation.base.view.BaseFragment
import com.elnemr.runningtracker.presentation.services.TrackingService
import com.elnemr.runningtracker.presentation.util.Constants
import com.elnemr.runningtracker.presentation.util.LocationUtils
import com.elnemr.runningtracker.presentation.util.LocationUtils.addAllPolyLines
import com.elnemr.runningtracker.presentation.util.LocationUtils.addLatestPolyline
import com.elnemr.runningtracker.presentation.util.LocationUtils.moveCameraToUserLocation
import com.elnemr.runningtracker.presentation.util.polyLine
import com.elnemr.runningtracker.presentation.viewmodel.MainViewModel
import com.elnemr.runningtracker.presentation.viewmodel.state.MainViewModelState
import com.google.android.gms.maps.GoogleMap
import kotlinx.android.synthetic.main.fragment_tracking.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrackingFragment : BaseFragment(R.layout.fragment_tracking) {

    private var isTracking = false
    private var pathPoints = mutableListOf<polyLine>()

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: FragmentTrackingBinding
    private var map: GoogleMap? = null

    private var curTimeInMillis = 0L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrackingBinding.bind(view)

        binding.btnToggleRun.setOnClickListener {
            toggleRun()
        }
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync {
            map = it
            addAllPolyLines(pathPoints, map)
        }

        collectTrackingData()
    }

    private fun collectTrackingData() {
        CoroutineScope(Dispatchers.Main).launch {
            launch {
            TrackingService.isTracking.collect {
                updateTracking(it)
            }}

            launch {
                TrackingService.timeRunInMillis.collect{
                    curTimeInMillis = it
                    val formattedTime = LocationUtils.getFormattedStopWatchTime(curTimeInMillis, true)
                    binding.tvTimer.text = formattedTime
                }
            }

        }

        TrackingService.pathPoints.observe(viewLifecycleOwner) {
            pathPoints = it
            if (pathPoints.isNotEmpty()) {
                addLatestPolyline(pathPoints.last(), map)
                moveCameraToUserLocation(pathPoints.last(), map)
            }

        }
    }

    private fun toggleRun() {
        if (isTracking) sendCommandToService(Constants.ACTION_PAUSE_SERVICE)
        else sendCommandToService(Constants.ACTION_START_OR_RESUME_SERVICE)
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (isTracking) {
            binding.btnToggleRun.text = "Pause"
            binding.btnFinishRun.isVisible = true
        } else {
            binding.btnToggleRun.text = "Start"
            binding.btnFinishRun.isVisible = false
        }
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
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
            else -> {
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
}