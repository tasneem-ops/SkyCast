package com.example.skycast.location.view

import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
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
    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        map = googleMap
        val cairo = LatLng(30.033333, 31.233334)
        googleMap.addMarker(MarkerOptions().position(cairo).title("Marker in Cairo"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cairo, 10f))
        map.setOnMapLongClickListener{
            map.clear()
            map.addMarker(MarkerOptions().position(it))
            chosenLatLng = it
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
        activity?.let {
            (it as AppCompatActivity).supportActionBar?.hide()
        }
        val button = view.findViewById<FloatingActionButton>(R.id.map_save)
        button.setOnClickListener {
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
                else ->{

                }

            }
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
    companion object{
        const val ALERT_TYPE = 3
        const val FAV_TYPE = 2
        const val HOME_TYPE = 1
    }
}