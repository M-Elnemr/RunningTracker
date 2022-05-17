package com.elnemr.runningtracker.presentation.ui.setup

import android.os.Bundle
import android.view.View
import com.elnemr.runningtracker.R
import com.elnemr.runningtracker.databinding.FragmentRunBinding
import com.elnemr.runningtracker.databinding.FragmentSetupBinding
import com.elnemr.runningtracker.presentation.base.view.BaseFragment

class SetupFragment() : BaseFragment(R.layout.fragment_setup) {

    private lateinit var binding: FragmentSetupBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSetupBinding.bind(view)
    }

    override fun setUpViewModelStateObservers() {

    }

}