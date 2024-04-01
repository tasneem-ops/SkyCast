package com.example.skycast.settings.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.skycast.R
import com.example.skycast.databinding.FragmentSettingsBinding
import com.example.skycast.home.view.HomeFragment
import com.example.skycast.settings.model.UserSettingsDataSource
import com.example.skycast.settings.model.UserSettingsDataSource.Companion.ARABIC
import com.example.skycast.settings.model.UserSettingsDataSource.Companion.ENGLISH
import com.example.skycast.settings.model.UserSettingsDataSource.Companion.SOURCE_GPS
import com.example.skycast.settings.model.UserSettingsDataSource.Companion.SOURCE_MAP
import com.example.skycast.settings.model.UserSettingsDataSource.Companion.UNIT_CELSIUS
import com.example.skycast.settings.model.UserSettingsDataSource.Companion.UNIT_FAHRENHEIT
import com.example.skycast.settings.model.UserSettingsDataSource.Companion.UNIT_KELVIN
import com.example.skycast.settings.model.UserSettingsDataSource.Companion.UNIT_MPH
import com.example.skycast.settings.model.UserSettingsDataSource.Companion.UNIT_MPS
import com.example.skycast.settings.viewmodel.SettingsViewModel
import com.example.skycast.settings.viewmodel.SettingsViewModelFactory

class SettingsFragment : Fragment() {
    private lateinit var binding : FragmentSettingsBinding
    private lateinit var viewModel: SettingsViewModel
    private lateinit var viewModelFactory: SettingsViewModelFactory
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelFactory = SettingsViewModelFactory(UserSettingsDataSource.getInstance(requireContext()))
        viewModel = ViewModelProvider(this, viewModelFactory).get(SettingsViewModel::class.java)
        initUI()
        binding.languageGroup.setOnCheckedChangeListener { radioGroup, i ->
            when(i){
                R.id.arabicRadio -> {
                    if(viewModel.getPreferredLanguage() != ARABIC){
                        viewModel.setPreferredLanguage(ARABIC)
                        Log.i("TAG", "Recreate: ")
                        requireActivity().recreate()
                    }
                }
                R.id.englishRadio -> {
                    if(viewModel.getPreferredLanguage() != ENGLISH){
                        viewModel.setPreferredLanguage(ENGLISH)
                        Log.i("TAG", "Recreate: ")
                        requireActivity().recreate()
                    }
                }
        }

        }
        binding.locationGroup.setOnCheckedChangeListener { radioGroup, i ->
            when(i){
                R.id.mapRadio -> {
                    viewModel.setLocationSource(SOURCE_MAP)
                    val action = SettingsFragmentDirections.actionSettingsFragmentToMapsFragment(HomeFragment.HOME_TYPE)
                    Navigation.findNavController(binding.locationGroup).navigate(action)
                }
                R.id.gpsRadio -> {
                    viewModel.setLocationSource(SOURCE_GPS)
                    val action = SettingsFragmentDirections.actionSettingsFragmentToHomeFragment(update = true)
                    Navigation.findNavController(binding.locationGroup).navigate(action)
                }
            }
        }
        binding.speedGroup.setOnCheckedChangeListener { radioGroup, i ->
            when(i){
                R.id.mpsRadio -> {viewModel.setSpeedUnit(UNIT_MPS)}
                R.id.mphRadio -> {viewModel.setSpeedUnit(UNIT_MPH)}
            }
        }
        binding.tempGroup.setOnCheckedChangeListener { radioGroup, i ->
            when(i){
                R.id.celsiusRadio ->{viewModel.setTempUnit(UNIT_CELSIUS)}
                R.id.fahRadio ->{viewModel.setTempUnit(UNIT_FAHRENHEIT)}
                R.id.kelvinRadio ->{viewModel.setTempUnit(UNIT_KELVIN)}
            }
        }
        binding.switch1.setOnCheckedChangeListener{ _, isChecked->
            viewModel.setNotificationEnabled(isChecked)
        }
    }

    private fun initUI() {
        val language = viewModel.getPreferredLanguage()
        if(language == ARABIC)
            binding.languageGroup.check(R.id.arabicRadio)
        else
            binding.languageGroup.check(R.id.englishRadio)

        val speedUnit = viewModel.getSpeedUnit()
        if(speedUnit == UNIT_MPH)
            binding.speedGroup.check(R.id.mphRadio)
        else
            binding.speedGroup.check(R.id.mpsRadio)

        val locationSource = viewModel.getLocationSource()
        if(locationSource == SOURCE_MAP)
            binding.locationGroup.check(R.id.mapRadio)
        else
            binding.locationGroup.check(R.id.gpsRadio)

        val tempUnit = viewModel.getTempUnit()
        if(tempUnit == UNIT_KELVIN)
            binding.tempGroup.check(R.id.kelvinRadio)
        else if(tempUnit == UNIT_FAHRENHEIT)
            binding.tempGroup.check(R.id.fahRadio)
        else
            binding.tempGroup.check(R.id.celsiusRadio)
    }
}