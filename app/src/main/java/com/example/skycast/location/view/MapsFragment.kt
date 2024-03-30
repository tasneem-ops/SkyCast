package com.example.skycast.location.view

import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import com.example.skycast.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MapsFragment : Fragment() {
    lateinit var map : GoogleMap
    var chosenLatLng = LatLng(30.033333, 31.233334)
    lateinit var cityNameText : TextView
    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        val cairo = LatLng(30.033333, 31.233334)
        googleMap.addMarker(MarkerOptions().position(cairo).title("Marker in Cairo"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cairo, 10f))
        map.setOnMapLongClickListener{
            map.clear()
            map.addMarker(MarkerOptions().position(it))
            chosenLatLng = it
            cityNameText.text = latLngToCityName(it.latitude, it.longitude)
        }
        val latitude = arguments?.getFloat("lat")?.toDouble()
        val longitude = arguments?.getFloat("lon")?.toDouble()
        if(latitude != null && latitude != 0.0 && longitude != null && longitude != 0.0){
            chosenLatLng = LatLng(latitude, longitude)
            cityNameText.text = latLngToCityName(chosenLatLng.latitude, chosenLatLng.longitude)
            map.clear()
            map.addMarker(MarkerOptions().position(chosenLatLng))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(chosenLatLng, 6f))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        cityNameText = view.findViewById(R.id.cityName)
        val saveButton = view.findViewById<FloatingActionButton>(R.id.map_save)
        val searchBar = view.findViewById<CardView>(R.id.search_bar)
        saveButton.setOnClickListener {
            when (arguments?.getInt("type")) {
                HOME_TYPE -> {
                    val action = MapsFragmentDirections.actionMapsFragmentToHomeFragment(
                        chosenLatLng.latitude.toFloat(),
                        chosenLatLng.longitude.toFloat()
                    )
                    Navigation.findNavController(it).navigate(action)
                }

                FAV_TYPE -> {
                    val action = MapsFragmentDirections.actionMapsFragmentToFavoritesFragment(
                        chosenLatLng.latitude.toFloat(),
                        chosenLatLng.longitude.toFloat(),
                        latLngToCityName(chosenLatLng.latitude, chosenLatLng.longitude)
                    )
                    Navigation.findNavController(it).navigate(action)
                }

                ALERT_TYPE -> {
                    val action = MapsFragmentDirections.actionMapsFragmentToAddAlertFragment(
                        chosenLatLng.latitude.toFloat(),
                        chosenLatLng.longitude.toFloat()
                    )
                    Navigation.findNavController(it).navigate(action)
                }
                SEARCH_TYPE ->{

                }
                else ->{

                }

            }
        }
        searchBar.setOnClickListener{
            val action = MapsFragmentDirections.actionMapsFragmentToSearchFragment(arguments?.getInt("type") ?:0)
            Navigation.findNavController(it).navigate(action)
        }

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

    override fun onResume() {
        super.onResume()
        (activity  as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity  as AppCompatActivity).supportActionBar?.show()
    }
    companion object{
        const val SEARCH_TYPE = 4
        const val ALERT_TYPE = 3
        const val FAV_TYPE = 2
        const val HOME_TYPE = 1
    }
}