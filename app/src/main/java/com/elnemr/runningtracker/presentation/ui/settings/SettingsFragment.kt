package com.elnemr.runningtracker.presentation.ui.settings

import android.os.Bundle
import android.view.View
import com.elnemr.runningtracker.R
import com.elnemr.runningtracker.databinding.FragmentRunBinding
import com.elnemr.runningtracker.databinding.FragmentSettingsBinding
import com.elnemr.runningtracker.presentation.base.view.BaseFragment

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingsBinding.bind(view)
    }

    override fun setUpViewModelStateObservers() {

    }

}