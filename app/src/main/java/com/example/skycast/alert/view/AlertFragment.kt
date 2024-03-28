package com.example.skycast.alert.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.R
import com.example.skycast.alert.alarm_manager.AlarmScheduler
import com.example.skycast.alert.model.dto.AlertDTO
import com.example.skycast.alert.viewmodel.AlertViewModel
import com.example.skycast.alert.viewmodel.AlertViewModelFactory
import com.example.skycast.databinding.FragmentAlertBinding
import com.example.skycast.model.Response
import com.example.skycast.model.local.LocalDataSource
import com.example.skycast.model.local.UserSettingsDataSource
import com.example.skycast.model.network.RemoteDataSource
import com.example.skycast.model.repository.WeatherRepository
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AlertFragment : Fragment() {
    lateinit var binding : FragmentAlertBinding
    lateinit var alertsListAdapter : AlertsListAdapter
    lateinit var viewModel: AlertViewModel
    lateinit var viewModelFactory: AlertViewModelFactory
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_alert, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelFactory = AlertViewModelFactory((WeatherRepository.getInstance(RemoteDataSource.getInstance(),
            LocalDataSource.getInstance(requireContext()))), UserSettingsDataSource.getInstance(requireContext()))
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(AlertViewModel::class.java)
        initRecyclerView()
        binding.addAlertFab.setOnClickListener {
            val action = AlertFragmentDirections.actionAlertFragmentToAddAlertFragment()
            Navigation.findNavController(it).navigate(action)
        }
        viewModel.getAlerts()
        lifecycleScope.launch {
            viewModel.respnseDataState.collectLatest { response ->
                when(response){
                    is Response.Success ->{
                        showData(response.data)
                    }
                    is Response.Failure ->{
                        showError()
                    }
                    is Response.Loading ->{
                        showLoading()
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        alertsListAdapter = AlertsListAdapter{ alert ->
            Snackbar.make(binding.alertsRecyclerView, "Delete Alert?", Snackbar.LENGTH_SHORT)
                .setAction("Delete"){
                    viewModel.deleteAlert(alert)
                    AlarmScheduler(requireContext()).cancel(alert)
                }
                .show()

        }
        binding.alertsRecyclerView.apply {
            adapter = alertsListAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.VERTICAL
            }
        }
    }
    private fun showLoading(){
        binding.loading.visibility = View.VISIBLE
        binding.alertsRecyclerView.visibility = View.GONE
        binding.error.visibility = View.GONE
        binding.empty.visibility = View.GONE
    }
    private fun showError(){
        binding.loading.visibility = View.GONE
        binding.alertsRecyclerView.visibility = View.GONE
        binding.error.visibility = View.VISIBLE
        binding.empty.visibility = View.GONE
    }
    private fun showData(data : List<AlertDTO>){
        if(! data.isEmpty()){
            binding.loading.visibility = View.GONE
            binding.alertsRecyclerView.visibility = View.VISIBLE
            binding.error.visibility = View.GONE
            binding.empty.visibility = View.GONE
            alertsListAdapter.submitList(data)
        }
        else{
            binding.loading.visibility = View.GONE
            binding.alertsRecyclerView.visibility = View.GONE
            binding.error.visibility = View.GONE
            binding.empty.visibility = View.VISIBLE
        }
    }
}