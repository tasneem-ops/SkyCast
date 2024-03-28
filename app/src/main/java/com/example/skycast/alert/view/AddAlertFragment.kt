package com.example.skycast.alert.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.example.skycast.alert.alarm_manager.AlarmScheduler
import com.example.skycast.alert.viewmodel.AlertViewModel
import com.example.skycast.alert.viewmodel.AlertViewModelFactory
import com.example.skycast.databinding.FragmentAddAlertBinding
import com.example.skycast.model.local.LocalDataSource
import com.example.skycast.model.local.UserSettingsDataSource
import com.example.skycast.model.network.RemoteDataSource
import com.example.skycast.model.repository.WeatherRepository
import java.util.Calendar

private const val TAG = "AddAlertFragment"
class AddAlertFragment : Fragment() {
    private lateinit var binding : FragmentAddAlertBinding
    lateinit var viewModel: AlertViewModel
    lateinit var viewModelFactory: AlertViewModelFactory
    var date = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_alert, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelFactory = AlertViewModelFactory(WeatherRepository.getInstance(RemoteDataSource.getInstance(),
            LocalDataSource.getInstance(requireContext())), UserSettingsDataSource.getInstance(requireContext()))
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(AlertViewModel::class.java)
        val latitude = arguments?.getFloat("lat")?.toDouble()
        val longitude = arguments?.getFloat("lng")?.toDouble()
        if(latitude != null && latitude != 0.0 && longitude != null && longitude != 0.0){
            viewModel.alert.latitude = latitude
            viewModel.alert.longitude = longitude
        }
        binding.mapBtn.setOnClickListener {
            val action = AddAlertFragmentDirections.actionAddAlertFragmentToMapsFragment(ALERT_TYPE)
            Navigation.findNavController(it).navigate(action)
        }
        binding.startTimeBtn.setOnClickListener {
            showDateTimePicker(true)
        }
        binding.endTimeBtn.setOnClickListener {
            showDateTimePicker(false)
        }
        binding.saveBtn.setOnClickListener {
            viewModel.addAlert()
            runCatching {
                AlarmScheduler(requireContext()).schedule(viewModel.alert, requireContext().getString(R.string.apiKey))
            }
                .onSuccess {
                    Log.i(TAG, "onViewCreated: onSuccess")
                }
                .onFailure {
                    Log.i(TAG, "onViewCreated: onFailure")
                }
            val action = AddAlertFragmentDirections.actionAddAlertFragmentToAlertFragment()
            Navigation.findNavController(it).navigate(action)

        }
    }

    fun showDateTimePicker(start : Boolean){
        val currentDate = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, day ->
            TimePickerDialog(requireContext(), { _, hour, minute ->
                date.set(year, month, day, hour, minute)
                if(start){
                    viewModel.alert.start = date.timeInMillis
                }
                else{
                    viewModel.alert.end = date.timeInMillis
                }
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show()
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show()
    }
    companion object{
        const val ALERT_TYPE = 3
    }
}