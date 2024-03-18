package com.example.skycast.home.view

import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.skycast.R
import com.example.skycast.network.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.location.Location
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.database.LocalDataSource
import com.example.skycast.database.LocalDataSourceImpl
import com.example.skycast.databinding.FragmentHomeBinding
import com.example.skycast.home.viewmodel.ResponseStatus
import com.example.skycast.home.viewmodel.WeatherViewModel
import com.example.skycast.home.viewmodel.WeatherViewModelFactory
import com.example.skycast.model.repository.Repository
import com.example.skycast.network.RemoteDataSourceImpl
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {
    lateinit var binding : FragmentHomeBinding
    private lateinit var weatherViewModelFactory : WeatherViewModelFactory
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var hourListAdapter: HourListAdapter
    private lateinit var dayListAdapter: DailyListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weatherViewModelFactory = WeatherViewModelFactory(Repository.getInstance(LocalDataSourceImpl.getInstance(requireContext()),
            RemoteDataSourceImpl.getInstance()))
        weatherViewModel = ViewModelProvider(this, weatherViewModelFactory).get(WeatherViewModel::class.java)
        initRecyclerView()
        weatherViewModel.getWeatherForecast(30.001270, 31.432470, true)
        lifecycleScope.launch {
            weatherViewModel.responseDataState.collectLatest { result ->
                when(result){
                    is ResponseStatus.Success ->{
                        Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                        hourListAdapter.submitList(result.data.todayForecast)
                        dayListAdapter.submitList(result.data.dailyForecast)
                        binding.currentWeather = result.data.currentWeather
                        showDateTimeAndCity(result.data.currentWeather.dt, 30.001270, 31.432470,
                            result.data.currentWeather.cityName, result.data.currentWeather.country)
                        binding.currentTempText.text = (result.data.currentWeather.temp)?.toInt().toString()
//                        binding.imageView2.visibility = View.GONE
//                        binding.recyclerView.visibility = View.VISIBLE
//                        listAdapter.submitList(result.data)
                    }
                    is ResponseStatus.Failure ->{
                        Toast.makeText(requireContext(), "Failure", Toast.LENGTH_SHORT).show()
//                        binding.recyclerView.visibility = View.GONE
//                        binding.imageView2.visibility = View.VISIBLE
//                        binding.imageView2.setImageResource(R.drawable.cloud)
                    }
                    is ResponseStatus.Loading ->{
//                        Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
//                        binding.recyclerView.visibility = View.GONE
//                        binding.imageView2.visibility = View.VISIBLE
//                        binding.imageView2.setImageResource(R.drawable.loading)
                    }
                }

            }
        }
    }

    private fun showDateTimeAndCity(dt: Int, lat: Double, lng: Double, city : String?, country : String?) {
        val date = Date()
        val dateFormat = SimpleDateFormat("MMMM dd", Locale.getDefault())
        val formattedDate = dateFormat.format(date)

        val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val dayOfWeek = dayOfWeekFormat.format(date)
        binding.dateText.text = "$dayOfWeek, $formattedDate"

        val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
        binding.timeText.text = timeFormat.format(date)

        val address = Geocoder(requireContext()).getFromLocation(lat, lng, 1)
        binding.cityText.text = address?.get(0)?.locality ?: "$city, $country"

    }

    private fun initRecyclerView() {
        hourListAdapter = HourListAdapter()
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

}