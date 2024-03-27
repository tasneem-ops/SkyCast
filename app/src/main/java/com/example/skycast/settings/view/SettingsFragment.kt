package com.example.skycast.settings.view

import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.skycast.MainActivity
import com.example.skycast.R
import com.example.skycast.databinding.FragmentSettingsBinding
import com.example.skycast.model.local.UserSettingsDataSource
import com.example.skycast.model.local.UserSettingsDataSource.Companion.ARABIC
import com.example.skycast.model.local.UserSettingsDataSource.Companion.ENGLISH
import com.example.skycast.model.local.UserSettingsDataSource.Companion.SOURCE_GPS
import com.example.skycast.model.local.UserSettingsDataSource.Companion.SOURCE_MAP
import com.example.skycast.model.local.UserSettingsDataSource.Companion.UNIT_CELSIUS
import com.example.skycast.model.local.UserSettingsDataSource.Companion.UNIT_FAHRENHEIT
import com.example.skycast.model.local.UserSettingsDataSource.Companion.UNIT_KELVIN
import com.example.skycast.model.local.UserSettingsDataSource.Companion.UNIT_MPH
import com.example.skycast.model.local.UserSettingsDataSource.Companion.UNIT_MPS
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
                }
                R.id.gpsRadio -> {
                    viewModel.setLocationSource(SOURCE_GPS)
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
    }
}