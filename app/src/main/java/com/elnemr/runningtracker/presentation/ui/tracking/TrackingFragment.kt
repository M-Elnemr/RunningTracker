package com.elnemr.runningtracker.presentation.ui.tracking

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.elnemr.runningtracker.R
import com.elnemr.runningtracker.data.db.Run
import com.elnemr.runningtracker.databinding.FragmentTrackingBinding
import com.elnemr.runningtracker.presentation.base.view.BaseFragment
import com.elnemr.runningtracker.presentation.services.TrackingService
import com.elnemr.runningtracker.presentation.util.Constants
import com.elnemr.runningtracker.presentation.util.LocationUtils
import com.elnemr.runningtracker.presentation.util.LocationUtils.addAllPolyLines
import com.elnemr.runningtracker.presentation.util.LocationUtils.addLatestPolyline
import com.elnemr.runningtracker.presentation.util.LocationUtils.calculatePolylineLength
import com.elnemr.runningtracker.presentation.util.LocationUtils.moveCameraToUserLocation
import com.elnemr.runningtracker.presentation.util.LocationUtils.zoomToSeeWholeTrack
import com.elnemr.runningtracker.presentation.util.polyLine
import com.elnemr.runningtracker.presentation.util.showDialog
import com.elnemr.runningtracker.presentation.viewmodel.MainViewModel
import com.elnemr.runningtracker.presentation.viewmodel.state.MainViewModelState
import com.google.android.gms.maps.GoogleMap
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.round

class TrackingFragment : BaseFragment(R.layout.fragment_tracking) {

    private var isTracking = false
    private var pathPoints = mutableListOf<polyLine>()

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: FragmentTrackingBinding
    private var map: GoogleMap? = null

    private var curTimeInMillis = 0L

    private var menu: Menu? = null

    private val weight = 80f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

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

        binding.btnFinishRun.setOnClickListener {
            zoomToSeeWholeTrack(
                pathPoints,
                map,
                binding.mapView
            )
            endRunAndSaveToDb()
        }

        collectTrackingData()
    }

    private fun collectTrackingData() {
        CoroutineScope(Dispatchers.Main).launch {
            launch {
                TrackingService.isTracking.collect {
                    updateTracking(it)
                }
            }

            launch {
                TrackingService.timeRunInMillis.collect {
                    curTimeInMillis = it
                    val formattedTime =
                        LocationUtils.getFormattedStopWatchTime(curTimeInMillis, true)
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
        if (isTracking) {
            sendCommandToService(Constants.ACTION_PAUSE_SERVICE)
            menu?.getItem(0)?.isVisible = true
        } else sendCommandToService(Constants.ACTION_START_OR_RESUME_SERVICE)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.toobar_tracking_menu, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (curTimeInMillis > 0L) {
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_cancel_tracking -> showStoppingDialog()
        }
        return true
    }

    private fun showStoppingDialog() {
        showDialog(
            requireContext(),
            R.string.cancel_run,
            R.string.cancel_run_confirmation,
            R.drawable.ic_delete,
            ::stopRun
        )
    }

    private fun stopRun(dialog: DialogInterface? = null) {
        sendCommandToService(Constants.ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
        dialog?.cancel()
    }

    private fun endRunAndSaveToDb() {

        map?.snapshot {
            var distanceInMeter = 0
            for (polyline in pathPoints) {
                distanceInMeter += calculatePolylineLength(polyline).toInt()
            }

            // to have only one decimal 0.0
            val avgSpeed =
                round((distanceInMeter / 1000f) / (curTimeInMillis / 1000 / 60 / 60) * 10) / 10f

            val dateTimeStamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeter / 1000f) * weight).toInt()
            val run =
                Run(it, dateTimeStamp, avgSpeed, distanceInMeter, curTimeInMillis, caloriesBurned)

            viewModel.insertRun(run)
        }

    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (isTracking) {
            binding.btnToggleRun.text = "Pause"
            menu?.getItem(0)?.isVisible = true
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
            is MainViewModelState.OnRunInserted -> onRunInserted(state.successful)
            else -> {
            }
        }
    }

    private fun onRunInserted(successful: Boolean) {
        if (successful) Snackbar.make(
            requireActivity().findViewById(R.id.rootView),
            "Run Saved Successfully",
            Snackbar.LENGTH_LONG
        ).show()
        stopRun()
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