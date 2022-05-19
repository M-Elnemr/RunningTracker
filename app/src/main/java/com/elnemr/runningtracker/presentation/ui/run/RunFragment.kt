package com.elnemr.runningtracker.presentation.ui.run

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.elnemr.runningtracker.R
import com.elnemr.runningtracker.databinding.FragmentRunBinding
import com.elnemr.runningtracker.presentation.base.view.BaseFragment
import com.elnemr.runningtracker.presentation.util.Constants.REQUEST_CODE_LOCATION_PERMISSIONS
import com.elnemr.runningtracker.presentation.util.PermissionUtils
import com.elnemr.runningtracker.presentation.viewmodel.MainViewModel
import com.elnemr.runningtracker.presentation.viewmodel.state.MainViewModelState
import kotlinx.coroutines.flow.buffer
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class RunFragment : BaseFragment(R.layout.fragment_run), EasyPermissions.PermissionCallbacks {

    private lateinit var binding: FragmentRunBinding
    private val viewModel by viewModels<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRunBinding.bind(view)

        requestPermissions()
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }
    }

    private fun requestPermissions() {
        if (PermissionUtils.hasLocationPermissions(requireContext())) return
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) EasyPermissions.requestPermissions(
            this,
            "You need to Accept location permissions to be able to use this app.",
            REQUEST_CODE_LOCATION_PERMISSIONS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
        else EasyPermissions.requestPermissions(
            this,
            "You need to Accept location permissions to be able to use this app.",
            REQUEST_CODE_LOCATION_PERMISSIONS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
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
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms))
            AppSettingsDialog.Builder(this).build().show()
        else requestPermissions()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}