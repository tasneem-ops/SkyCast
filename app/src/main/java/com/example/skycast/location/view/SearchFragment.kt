package com.example.skycast.location.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.R
import com.example.skycast.databinding.FragmentSearchBinding
import com.example.skycast.home.model.db.WeatherDB
import com.example.skycast.location.viewmodel.LocationViewModel
import com.example.skycast.location.viewmodel.LocationViewModelFactory
import com.example.skycast.Response
import com.example.skycast.home.model.source.local.WeatherLocalDataSourceImpl
import com.example.skycast.settings.model.UserSettingsDataSource
import com.example.skycast.home.model.source.network.WeatherRemoteDataSourceImpl
import com.example.skycast.home.model.source.repository.WeatherRepositoryImpl
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
        viewModelFactory = LocationViewModelFactory(
            WeatherRepositoryImpl.getInstance(
            WeatherRemoteDataSourceImpl.getInstance(),
            WeatherLocalDataSourceImpl.getInstance(
                WeatherDB.getInstance(requireContext()).getDailyWeatherDao(),
                WeatherDB.getInstance(requireContext()).getHourlyWeatherDao())), UserSettingsDataSource.getInstance(requireContext()))
        viewModel = ViewModelProvider(this, viewModelFactory).get(LocationViewModel::class.java)
        initView()
        lifecycleScope.launch {
            sharedFlow.sample(1000)
                .collect{
                    viewModel.getSuggestions(it)
                    Log.i("TAG", "onViewCreated: Collect $it")
                }
        }
        lifecycleScope.launch{
            viewModel.respnseDataState.collectLatest { response ->
                when(response){
                    is Response.Failure ->{
                        Toast.makeText(requireContext(), "Failure", Toast.LENGTH_SHORT).show()
                    }
                    is Response.Success ->{
                        Log.i("TAG", "onViewCreated: Success ${response.data}")
                        searchAdapter.submitList(response.data)
                    }
                    else ->{

                    }
                }
            }
        }
        binding.searchText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.i("TAG", "onTextChanged: $text")
                lifecycleScope.launch {
                    sharedFlow.emit(text.toString())
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        })

    }

    private fun initView() {
        searchAdapter = SearchListAdapter{
            val action = SearchFragmentDirections
                .actionSearchFragmentToMapsFragment(arguments?.getInt("type") ?:0,it.lat?.toFloat() ?:0.0f, it.lon?.toFloat()?:0.0f)
            Navigation.findNavController(binding.searchRecyclerView).navigate(action)
        }
        binding.searchRecyclerView.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.VERTICAL
            }
        }
    }
    override fun onResume() {
        super.onResume()
        (activity  as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity  as AppCompatActivity).supportActionBar?.show()
    }
}