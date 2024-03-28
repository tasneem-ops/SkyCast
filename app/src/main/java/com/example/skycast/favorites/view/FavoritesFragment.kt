package com.example.skycast.favorites.view

import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.R
import com.example.skycast.databinding.FragmentFavoritesBinding
import com.example.skycast.favorites.model.dto.FavDTO
import com.example.skycast.favorites.viewmodel.FavoritesViewModel
import com.example.skycast.favorites.viewmodel.FavoritesViewModelFactory
import com.example.skycast.model.Response
import com.example.skycast.model.local.LocalDataSource
import com.example.skycast.model.network.RemoteDataSource
import com.example.skycast.model.repository.WeatherRepository
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment() {
    private lateinit var binding : FragmentFavoritesBinding
    private lateinit var viewModel: FavoritesViewModel
    private lateinit var viewModelFactory : FavoritesViewModelFactory
    private lateinit var favListAdapter : FavoritesListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_favorites, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelFactory = FavoritesViewModelFactory(WeatherRepository.getInstance(RemoteDataSource.getInstance(),
            LocalDataSource.getInstance(requireContext())))
        viewModel = ViewModelProvider(this, viewModelFactory).get(FavoritesViewModel::class.java)
        initRecylerView()
        val connectivityManager = requireContext().getSystemService(ConnectivityManager::class.java)
        val currentNetwork = connectivityManager.activeNetworkInfo
        if(currentNetwork?.isConnected == true){
            arguments?.let {
                if(! it.isEmpty){
                    val lat = it.getFloat("lat").toDouble()
                    val lng = it.getFloat("lng").toDouble()
                    val cityName = it.getString("city")
                    viewModel.addFav(FavDTO(cityName ?: "", lat, lng))
                }
            }
        }
        viewModel.getAllFav()
        lifecycleScope.launch {
            viewModel.respnseDataState.collectLatest { response ->
                when(response){
                    is Response.Success ->{
                        if(response.data.isEmpty()){
                            binding.empty.visibility = View.VISIBLE
                            binding.favRecyclerView.visibility = View.GONE
                        }
                        else{
                            binding.empty.visibility = View.GONE
                            binding.favRecyclerView.visibility = View.VISIBLE
                            favListAdapter.submitList(response.data)
                        }
                    }
                    is Response.Failure ->{
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                    }
                    is Response.Loading ->{

                    }
                }
            }
        }
        binding.addFavFab.setOnClickListener {
            val action = FavoritesFragmentDirections.actionFavoritesFragmentToMapsFragment(FAV_TYPE)
            Navigation.findNavController(it).navigate(action)
        }
    }

    private fun initRecylerView() {
        val onDeleteClick = {
                fav : FavDTO ->
            Snackbar.make(binding.favRecyclerView, "Delete Place?", Snackbar.LENGTH_SHORT)
                .setAction("Delete"){
                    viewModel.deleteFav(fav)
                }
                .show()
        }
        val onItemClick = { fav : FavDTO ->
            val action = FavoritesFragmentDirections
                .actionFavoritesFragmentToHomeFragment(fav.latitude.toFloat(), fav.latitude.toFloat())
            Navigation.findNavController(binding.favRecyclerView).navigate(action)
        }
        favListAdapter = FavoritesListAdapter (onDeleteClick, onItemClick)
        binding.favRecyclerView.apply {
            adapter = favListAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.VERTICAL
            }
        }
    }

    companion object{
        const val FAV_TYPE = 2
    }

}