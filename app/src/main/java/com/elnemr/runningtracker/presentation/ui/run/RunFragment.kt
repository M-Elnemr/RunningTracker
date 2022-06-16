package com.elnemr.runningtracker.presentation.ui.run

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.elnemr.runningtracker.R
import com.elnemr.runningtracker.data.db.Run
import com.elnemr.runningtracker.databinding.FragmentRunBinding
import com.elnemr.runningtracker.presentation.adapter.base.BaseAdapter
import com.elnemr.runningtracker.presentation.adapter.base.OnItemClickInterface
import com.elnemr.runningtracker.presentation.adapter.run.RunAdapter
import com.elnemr.runningtracker.presentation.base.view.BaseFragment
import com.elnemr.runningtracker.presentation.util.*
import com.elnemr.runningtracker.presentation.util.RequestPermissionsHelper.checkIfPermissionGranted
import com.elnemr.runningtracker.presentation.viewmodel.MainViewModel
import com.elnemr.runningtracker.presentation.viewmodel.state.MainViewModelState
import kotlinx.coroutines.flow.buffer

class RunFragment : BaseFragment(R.layout.fragment_run), OnItemClickInterface {
    private val adapter: BaseAdapter<Run> = RunAdapter(this)
    private lateinit var binding: FragmentRunBinding
    private val viewModel by viewModels<MainViewModel>()

    private var mSortedBy = Constants.SORT_LIST[0]
    private var runs: List<Run> = listOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRunBinding.bind(view)

        initSpinner()
        initAdapter()
        fetchData(mSortedBy)

        binding.fab.setOnClickListener {
            if (LocationUtils.hasLocationPermissions(requireContext()))
                findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
            else requestPermissions()
        }

        RequestPermissionsHelper.permissionResult.observe(viewLifecycleOwner) {
            it.entries.forEach {
                if (it.value && it.key == Manifest.permission.ACCESS_FINE_LOCATION &&
                    Build.VERSION.SDK_INT > Build.VERSION_CODES.Q &&
                    !checkIfPermissionGranted(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        requireContext()
                    )
                )
                    requestPermissions()
            }
        }
    }

    private fun initAdapter() {
        binding.rvRuns.adapter = adapter
        binding.rvRuns.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun initSpinner() = binding.spFilter.apply {
        setItems(Constants.SORT_LIST)
        atIndex(Constants.SORT_LIST.indexOf(mSortedBy))
        setOnItemClickListener { adapterView, view, position, l ->
            if (mSortedBy != Constants.SORT_LIST[position]) {
                mSortedBy = Constants.SORT_LIST[position]
                fetchData(mSortedBy)
            }
        }
    }

    private fun fetchData(sortBy: String) {
        var sort = Constants.SORT_BY.TIMESTAMP

        when (sortBy) {
            "Running Time" -> sort = Constants.SORT_BY.TIME_IN_MILLIS
            "Distance" -> sort = Constants.SORT_BY.DISTANCE_IN_METER
            "Average Speed" -> sort = Constants.SORT_BY.AVG_SPEED
            "Calories Burned" -> sort = Constants.SORT_BY.CALORIES_BURNED
        }
        viewModel.fetchRuns(sort)
    }

    private fun requestPermissions() {
        if (!checkIfPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION, requireContext())) {
            RequestPermissionsHelper.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        } else {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q && !checkIfPermissionGranted(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    requireContext()
                )
            ) {
                RequestPermissionsHelper.requestPermissions(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
            }

        }

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
            is MainViewModelState.OnRunsFetched -> {
                runs = state.runs
                loadDataToAdapter()
            }
            is MainViewModelState.OnRunInserted -> {
            }
        }
    }

    private fun loadDataToAdapter() =
        adapter.setDataList(runs)

    override fun onDetailsClicked(data: Any, view: View?) {}

    override fun onPause() {
        super.onPause()
        binding.spFilter.setText("", false)
    }

    override fun onResume() {
        super.onResume()
        initSpinner()
    }
}