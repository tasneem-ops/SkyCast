package com.example.skycast.location.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.R
import com.example.skycast.alert.model.db.AlertsDB
import com.example.skycast.databinding.FragmentSearchBinding
import com.example.skycast.favorites.model.db.FavDB
import com.example.skycast.home.model.db.WeatherDB
import com.example.skycast.location.viewmodel.LocationViewModel
import com.example.skycast.location.viewmodel.LocationViewModelFactory
import com.example.skycast.model.Response
import com.example.skycast.model.local.LocalDataSource
import com.example.skycast.model.local.UserSettingsDataSource
import com.example.skycast.model.network.RemoteDataSource
import com.example.skycast.model.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    private lateinit var binding : FragmentSearchBinding
    private lateinit var viewModel: LocationViewModel
    private lateinit var viewModelFactory: LocationViewModelFactory
    val sharedFlow = MutableSharedFlow<String>()
    private lateinit var searchAdapter : SearchListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelFactory = LocationViewModelFactory(WeatherRepository.getInstance(RemoteDataSource.getInstance(),
            LocalDataSource.getInstance(
                WeatherDB.getInstance(requireContext()).getDailyWeatherDao(),
                WeatherDB.getInstance(requireContext()).getHourlyWeatherDao(),
                AlertsDB.getInstance(requireContext()).getAlertsDao(),
                FavDB.getInstance(requireContext()).getFavDao())), UserSettingsDataSource.getInstance(requireContext()))
        viewModel = ViewModelProvider(this, viewModelFactory).get(LocationViewModel::class.java)
        initView()
        lifecycleScope.launch {
            sharedFlow.sample(500)
                .collect{
                    viewModel.getSuggestions(it)
                }
        }
        lifecycleScope.launch{
            viewModel.respnseDataState.collectLatest { response ->
                when(response){
                    is Response.Failure ->{
                        Toast.makeText(requireContext(), "Failure", Toast.LENGTH_SHORT).show()
                    }
                    is Response.Success ->{
                        searchAdapter.submitList(response.data)
                    }
                    else ->{

                    }
                }
            }
        }
        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                lifecycleScope.launch {
                    query?.let { sharedFlow.emit(it) }
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                lifecycleScope.launch {
                    query?.let { sharedFlow.emit(it) }
                }
                return true
            }
        })
    }

    private fun initView() {
        searchAdapter = SearchListAdapter{
            val action = SearchFragmentDirections.actionSearchFragmentToMapsFragment()
            Navigation.findNavController(binding.searchRecyclerView).navigate(action)
        }
        binding.searchRecyclerView.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.VERTICAL
            }
        }
    }

}