package com.example.skycast.alert.view

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.skycast.R
import com.example.skycast.alert.alarm_manager.AlarmScheduler
import com.example.skycast.alert.model.db.AlertsDB
import com.example.skycast.alert.model.dto.NotificationType
import com.example.skycast.alert.viewmodel.AlertViewModel
import com.example.skycast.alert.viewmodel.AlertViewModelFactory
import com.example.skycast.databinding.FragmentAddAlertBinding
import com.example.skycast.favorites.model.db.FavDB
import com.example.skycast.home.model.db.WeatherDB
import com.example.skycast.home.view.HomeFragment
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
            LocalDataSource.getInstance(
                WeatherDB.getInstance(requireContext()).getDailyWeatherDao(),
                WeatherDB.getInstance(requireContext()).getHourlyWeatherDao(),
                AlertsDB.getInstance(requireContext()).getAlertsDao(),
                FavDB.getInstance(requireContext()).getFavDao())), UserSettingsDataSource.getInstance(requireContext()))
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(AlertViewModel::class.java)
        binding.alert = viewModel.alert
        val latitude = arguments?.getFloat("lat")?.toDouble()
        val longitude = arguments?.getFloat("lng")?.toDouble()
        if(latitude != null && latitude != 0.0 && longitude != null && longitude != 0.0){
            viewModel.alert.latitude = latitude
            viewModel.alert.longitude = longitude
            viewModel.alert.cityName = latLngToCityName(latitude, longitude)
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
        binding.notificationTypeGroup.setOnCheckedChangeListener { radioGroup, i ->
            when(i){
                R.id.notifyRadio ->{
                    viewModel.alert.notificationType = NotificationType.NOTIFICATION
                    checkNotificationPermission()
                }
                R.id.alarmRadio ->{
                    viewModel.alert.notificationType = NotificationType.ALARM
                    checkOverlayPermission()
                }
            }
        }
        binding.switch1.setOnCheckedChangeListener{ _, isChecked->
            viewModel.alert.notificationEnabled = isChecked
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

    private fun checkNotificationPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        NOTIFICATION_PERMISSION)
                }
        }
    }

    private fun checkOverlayPermission() {
        if(! Settings.canDrawOverlays(requireContext())){
           val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
               Uri.parse("package:"+ requireContext().packageName))
            requireActivity().startActivity(intent)
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
    fun latLngToCityName( latitude : Double, longitude: Double) : String?{
        try {
            val address = Geocoder(requireContext()).getFromLocation(latitude, longitude, 1)
            if(address == null)
                return null
            if (address.isEmpty())
                return null
            if(address.get(0).locality == null){
                return address.get(0).adminArea + ", " + address.get(0)?.countryName
            }
            return address.get(0)?.locality + ", " + address.get(0)?.countryName
        } catch (e : Exception){
            return "UnKnown City"
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION) {
            if ( grantResults[0]== PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Log.i(TAG, "onRequestPermissionsResult: Accepted")
            }
            else{
                Log.i(TAG, "onRequestPermissionsResult: Rejected")
            }
        }
    }
    companion object{
        const val ALERT_TYPE = 3
        private const val NOTIFICATION_PERMISSION = 2558
    }
}