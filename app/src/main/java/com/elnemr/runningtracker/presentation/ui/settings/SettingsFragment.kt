package com.elnemr.runningtracker.presentation.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import com.elnemr.runningtracker.R
import com.elnemr.runningtracker.databinding.FragmentSettingsBinding
import com.elnemr.runningtracker.presentation.base.view.BaseFragment
import com.elnemr.runningtracker.presentation.util.Constants
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @set:Inject
    var name = ""

    @set:Inject
    var weight = 80f

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingsBinding.bind(view)

        loadFieldsFromSharedPreferences()

        binding.btnApplyChanges.setOnClickListener {
            if (writePersonalDataToSharedPref())
                Snackbar.make(requireView(), "Saved Successfully", Snackbar.LENGTH_SHORT).show()
            else
                Snackbar.make(requireView(), "Please Enter All Fields", Snackbar.LENGTH_SHORT)
                    .show()
        }
    }

    private fun loadFieldsFromSharedPreferences() {
        binding.etName.setText(name)
        binding.etWeight.setText(weight.toString())
    }

    private fun writePersonalDataToSharedPref(): Boolean {
        val name = binding.etName.toString()
        val weight = binding.etWeight.toString()
        if (name.isEmpty() || weight.isEmpty()) return false

        sharedPreferences.edit()
            .putString(Constants.KEY_NAME, name)
            .putFloat(Constants.KEY_WEIGHT, weight.toFloat())
            .apply()

        return true
    }

    override fun setUpViewModelStateObservers() {

    }

}