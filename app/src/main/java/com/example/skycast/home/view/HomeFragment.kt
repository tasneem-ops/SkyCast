package com.example.skycast.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skycast.R
import com.example.skycast.databinding.FragmentHomeBinding
import com.example.skycast.home.model.dto.WeatherResult
import com.example.skycast.home.viewmodel.WeatherViewModel
import com.example.skycast.home.viewmodel.WeatherViewModelFactory
import com.example.skycast.model.Response
import com.example.skycast.model.local.LocalDataSource
import com.example.skycast.model.local.UserSettingsDataSource
import com.example.skycast.model.network.RemoteDataSource
import com.example.skycast.model.repository.Repository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var hourListAdapter: HourlyListAdapter
    private lateinit var dayListAdapter: DailyListAdapter
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var connectionStatus = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val weatherViewModelFactory = WeatherViewModelFactory(Repository(RemoteDataSource.getInstance(), LocalDataSource.getInstance(requireContext())),
            UserSettingsDataSource.getInstance(requireContext()))
        weatherViewModel = ViewModelProvider(this, weatherViewModelFactory).get(WeatherViewModel::class.java)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        initRecyclerView()
        //Step 1: Here We First See if user has a saved location already,
        // Step2: If he has already get data for this location
        //Step 3: Else see his preferences and get data based on that -- if gps get location & if needs permissions request it
        //Step 4: if map ask him to open map and choose position
        //Step 5 :if he hasn't got any preferences show dialog and on chosen go to step 3
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
        // For simplicity just assume he prefers gps
        getDataFromGPS()
    }
    fun getForecastData(latLng: LatLng, connectionStatus : Boolean, freshData : Boolean){
        context?.getString(R.string.apiKey)?.let {
            weatherViewModel.getWeatherForecast(latLng, it,
                true, freshData = true)
        }
    }
    private fun getDataFromGPS(){
        if(checkPermissions()){
            getLocation()
        }
        else{
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                My_LOCATION_PERMISSION_ID)
        }
    }

    private fun initRecyclerView() {
        hourListAdapter = HourlyListAdapter("C")
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
            if ( grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getLocation()
            }
        }
    }
    fun showLoading(){
        binding.loading.visibility = View.VISIBLE
        binding.layout.visibility = View.GONE
    }
    fun showError(){
        binding.loading.visibility = View.VISIBLE
        binding.layout.visibility = View.GONE
    }
    fun showData(data : WeatherResult){
        binding.loading.visibility = View.GONE
        binding.layout.visibility = View.VISIBLE
        binding.currentWeather = data.current
        data.current?.let {
            binding.timeText.text = SimpleDateFormat("HH:mm aa" , Locale.US).format(data.current!!.dt * 1000L)
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
                val latLng = LatLng(it.latitude, it.longitude)
                getForecastData(latLng, connectionStatus, true)
            }
    }
    companion object{
        private const val My_LOCATION_PERMISSION_ID = 5005
    }
}