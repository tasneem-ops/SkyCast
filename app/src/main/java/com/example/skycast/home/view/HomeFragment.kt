package com.example.skycast.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.skycast.R
import com.example.skycast.databinding.FragmentHomeBinding
import com.example.skycast.home.model.db.WeatherDB
import com.example.skycast.home.model.dto.WeatherResult
import com.example.skycast.home.viewmodel.WeatherViewModel
import com.example.skycast.home.viewmodel.WeatherViewModelFactory
import com.example.skycast.Response
import com.example.skycast.home.model.source.local.WeatherLocalDataSourceImpl
import com.example.skycast.settings.model.UserSettingsDataSource
import com.example.skycast.home.model.source.network.WeatherRemoteDataSourceImpl
import com.example.skycast.home.model.source.repository.WeatherRepositoryImpl
import com.example.skycast.settings.model.UserSettingsDataSource.Companion.UNIT_CELSIUS
import com.example.skycast.settings.model.UserSettingsDataSource.Companion.UNIT_FAHRENHEIT
import com.example.skycast.settings.model.UserSettingsDataSource.Companion.UNIT_KELVIN
import com.example.skycast.settings.model.UserSettingsDataSource.Companion.UNIT_MPH
import com.example.skycast.settings.model.UserSettingsDataSource.Companion.UNIT_MPS
import com.example.skycast.work.DailyCacheWorker
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

private const val TAG = "HomeFragment"
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var hourListAdapter: HourlyListAdapter
    private lateinit var dayListAdapter: DailyListAdapter
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var connectionStatus = true
    private var latLng : LatLng? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val weatherViewModelFactory = WeatherViewModelFactory(
            WeatherRepositoryImpl.getInstance(
            WeatherRemoteDataSourceImpl.getInstance(),
            WeatherLocalDataSourceImpl.getInstance(WeatherDB.getInstance(requireContext()).getDailyWeatherDao(),
                WeatherDB.getInstance(requireContext()).getHourlyWeatherDao())),
            UserSettingsDataSource.getInstance(requireContext()))
        weatherViewModel = ViewModelProvider(this, weatherViewModelFactory).get(WeatherViewModel::class.java)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        initRecyclerView()
        updateUI()
        if(arguments == null || arguments?.isEmpty == true ||
            arguments?.getFloat("lat") == 0.0f || arguments?.getFloat("lng") == 0.0f){
            Log.i(TAG, "onViewCreated: ")
            if(requireArguments().getBoolean("update") == true){
                getDataFromGPS()
            }
            if(weatherViewModel.isLocationSaved()){
                Log.i(TAG, "onViewCreated: Location Saved")
                weatherViewModel.getWeatherForecastForSavedLocation(getString(R.string.apiKey))
                val location = weatherViewModel.getSavedLocation()
                binding.cityText.text = latLngToCityName(location.latitude, location.longitude)
            }
            else{
                val locationSource = weatherViewModel.getLocationPreferences()
                when(locationSource){
                    SOURCE_GPS ->{ getDataFromGPS() }
                    SOURCE_MAP ->{
                        val action = HomeFragmentDirections.actionHomeFragmentToMapsFragment(HOME_TYPE)
                        Navigation.findNavController(binding.hourlyForecastText).navigate(action)
                    }
                    else ->{
                        showInitialSetupDialog(R.string.choose_location_source)
                    }
                }
            }
        }
        else{
            val latitude = requireArguments().getFloat("lat").toDouble()
            val longitude = requireArguments().getFloat("lng").toDouble()
            latLng = LatLng(latitude, longitude)
            if (requireArguments().getBoolean("cache") == true){
                weatherViewModel.getWeatherForecast(LatLng(latitude, longitude), getString(R.string.apiKey),
                    connectionStatus = connectionStatus, freshData = true)
                weatherViewModel.updateLocationAndCache(LatLng(latitude, longitude), getString(R.string.apiKey))
            }
            else{
                weatherViewModel.getWeatherForecast(LatLng(latitude, longitude), getString(R.string.apiKey),
                    connectionStatus = connectionStatus, freshData = true)
            }
        }
        requestDailyCache()
    }

    private fun updateUI() {
        when(weatherViewModel.getTempUnit()){
            UNIT_CELSIUS -> binding.currentTempUnit.text = getString(R.string.celcuis_unit)
            UNIT_FAHRENHEIT -> binding.currentTempUnit.text = getString(R.string.fahrenheit_unit)
            UNIT_KELVIN -> binding.currentTempUnit.text = getString(R.string.kelvin_unit)
        }
        when(weatherViewModel.getSpeedUnit()){
            UNIT_MPH -> binding.speedUnit.text = getString(R.string.mile_per_hour)
            UNIT_MPS -> binding.speedUnit.text = getString(R.string.meter_per_second)
        }
    }

    private fun requestDailyCache() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val myWorkRequest: PeriodicWorkRequest = PeriodicWorkRequestBuilder<DailyCacheWorker>(
            1, TimeUnit.DAYS, 1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(requireContext())
            .enqueueUniquePeriodicWork("1234",ExistingPeriodicWorkPolicy.KEEP,myWorkRequest)
    }

    private fun showInitialSetupDialog(titleResource : Int) {
        var selected = 0
        Log.i(TAG, "showInitialSetupDialog: ")
        val builder: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(titleResource))
            .setPositiveButton(getString(R.string.save)) { _, which ->
                takeAction(selected)
            }
            .setNegativeButton(getString(R.string.cancel)) { _, which ->
                takeAction(selected)
            }
            .setSingleChoiceItems(
                arrayOf(getString(R.string.gps),
                    getString(R.string.map)),
                0
            ) { _, which ->
                selected = which
                Log.i(TAG, "showInitialSetupDialog: ${selected}")
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun takeAction(selected : Int){
        Log.i(TAG, "takeAction: $selected")
        when(selected){
            0 ->{
                weatherViewModel.setLocationPreferences(SOURCE_GPS)
                getDataFromGPS()
            }
            1 ->{
                weatherViewModel.setLocationPreferences(SOURCE_MAP)
                val action = HomeFragmentDirections.actionHomeFragmentToMapsFragment(HOME_TYPE)
                Navigation.findNavController(binding.hourlyForecastText).navigate(action)
            }
            else ->{
                showInitialSetupDialog(R.string.please_choose_location_source)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            weatherViewModel.respnseDataState.collectLatest { response ->
                when(response){
                    is Response.Success ->{
                        showData(response.data)
                    }
                    is Response.Failure ->{
                        showError()
                    }
                    else->{
                        showLoading()
                    }
                }
            }
        }
    }
    fun getForecastData(latLng: LatLng, connectionStatus : Boolean, freshData : Boolean){
        context?.getString(R.string.apiKey)?.let {
            weatherViewModel.getWeatherForecast(latLng, it,
                true, freshData = true)
            weatherViewModel.updateLocationAndCache(latLng, it)
        }

    }
    private fun getDataFromGPS(){
        Log.i(TAG, "getDataFromGPS: ")
        if(checkPermissions()){
            checkDeviceLocationSettings()
        }
        else{
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                My_LOCATION_PERMISSION_ID)
        }
    }

    private fun initRecyclerView() {
        hourListAdapter = HourlyListAdapter(weatherViewModel.getTempUnit())
        dayListAdapter = DailyListAdapter()

        binding.hourRecyclerView.apply {
            adapter = hourListAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.HORIZONTAL
            }
        }
        binding.dayRecyclerView.apply {
            adapter = dayListAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.VERTICAL
            }
            isNestedScrollingEnabled = true
        }
    }
    fun checkPermissions(): Boolean{
        var result = false
        if ((ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
            ||
            (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ==  PackageManager.PERMISSION_GRANTED))
        {
            result  = true
        }
        return result
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == My_LOCATION_PERMISSION_ID ) {
            if ( grantResults[0]==PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Log.i(TAG, "onRequestPermissionsResult: Accepted")
                checkDeviceLocationSettings()
            }
            else{
                Log.i(TAG, "onRequestPermissionsResult: Rejected")
                showPermissionsNotGranted()
            }
        }
    }

    private fun showPermissionsNotGranted() {
        val builder: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.permission_not_granted))
            .setMessage(getString(R.string.app_need_location_permission_to_get_weather_data))
            .setPositiveButton(getString(R.string.ok)) { _, which ->
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    My_LOCATION_PERMISSION_ID)
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showLoading(){
        binding.loading.visibility = View.VISIBLE
        binding.layout.visibility = View.GONE
        binding.error.visibility = View.GONE
    }
    private fun showError(){
        binding.error.visibility = View.VISIBLE
        binding.loading.visibility = View.GONE
        binding.layout.visibility = View.GONE
    }
    private fun showData(data : WeatherResult){
        binding.loading.visibility = View.GONE
        binding.error.visibility = View.GONE
        binding.layout.visibility = View.VISIBLE
        binding.currentWeather = data.current
        data.current?.let {
            binding.timeText.text = SimpleDateFormat("hh:mm aa" , Locale.getDefault()).format(data.current!!.dt * 1000L)
            latLng?.let {
                binding.cityText.text = latLngToCityName(it.latitude, it.longitude)
            }
            binding.weatherDescText.text = it.weatherDescription.capitalizeWords()

        }
        hourListAdapter.submitList(data.hourly)
        dayListAdapter.submitList(data.daily)
    }
    @SuppressLint("MissingPermission")
    fun getLocation(){
        val cancellationToken = object : CancellationToken(){
            override fun onCanceledRequested(p0: OnTokenCanceledListener)= CancellationTokenSource().token

            override fun isCancellationRequested() = false
        }
        fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationToken)
            .addOnSuccessListener {
                latLng = LatLng(it.latitude, it.longitude)
                getForecastData(latLng!!, connectionStatus, true)
            }
    }
    @SuppressLint("MissingPermission")
    private fun checkDeviceLocationSettings(resolve:Boolean = true) {
        val locationRequest = LocationRequest()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())
        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve){
                try {
                    startIntentSenderForResult(exception.resolution.intentSender,
                        REQUEST_TURN_DEVICE_LOCATION_ON, null, 0,0,0,null)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(TAG, "Error getting location settings resolution: " + sendEx.message)
                }
            } else {
                Snackbar.make(
                    binding.dayRecyclerView,
                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettings()
                }.show()
            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if ( it.isSuccessful ) {
                getLocation()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON){
            if (resultCode == Activity.RESULT_OK){
                getLocation()
            }
        }
    }
    fun String.capitalizeWords(delimiter: String = " "): String {
        return split(delimiter).joinToString(delimiter) { word ->
            val smallCaseWord = word.lowercase()
            smallCaseWord.replaceFirstChar(Char::titlecaseChar)
        }
    }
    fun latLngToCityName( latitude : Double, longitude: Double) : String?{
        try {
            val address = activity?.let { Geocoder(it, Locale.getDefault()).getFromLocation(latitude, longitude, 1) }
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
    companion object{
        private const val My_LOCATION_PERMISSION_ID = 5005
        const val SOURCE_MAP = "MAP"
        const val SOURCE_GPS = "GPS"
        const val HOME_TYPE = 1
        private const val REQUEST_TURN_DEVICE_LOCATION_ON: Int = 12345

    }
}