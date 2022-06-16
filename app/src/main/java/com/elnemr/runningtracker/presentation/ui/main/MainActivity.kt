package com.elnemr.runningtracker.presentation.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.elnemr.runningtracker.R
import com.elnemr.runningtracker.databinding.ActivityMainBinding
import com.elnemr.runningtracker.presentation.base.view.BaseActivity
import com.elnemr.runningtracker.presentation.util.Constants
import com.elnemr.runningtracker.presentation.util.RequestPermissionsHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RequestPermissionsHelper.registerPermission(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigationView.setupWithNavController(navHostFragment.findNavController())

        navigateToTrackingFragmentIsNeeded(intent)

        navHostFragment.findNavController()
            .addOnDestinationChangedListener { controller, destination, arguments ->
                when (destination.id) {
                    R.id.settingsFragment, R.id.runFragment, R.id.statisticsFragment -> binding.bottomNavigationView.isVisible =
                        true
                    else -> binding.bottomNavigationView.isVisible = false
                }
            }
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIsNeeded(intent)
    }

    private fun navigateToTrackingFragmentIsNeeded(intent: Intent?){
        intent?.let {
            if (it.action == Constants.ACTION_SHOW_TRACKING_FRAGMENT){
                navHostFragment.findNavController().navigate(R.id.action_global_trackingFragment)
            }
        }
    }
}