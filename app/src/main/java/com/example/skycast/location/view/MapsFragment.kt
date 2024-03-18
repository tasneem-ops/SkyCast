package com.example.skycast.location.view

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.skycast.R
import com.google.android.gms.common.api.Status

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.Arrays

class MapsFragment : Fragment() {
    lateinit var autocompleteFragment: AutocompleteSupportFragment
    lateinit var map: GoogleMap
    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
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
        Places.initialize(requireContext(), "AIzaSyCXZIYUFCKw_t5ynDJIVyMGYKbKEngfD74")
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        autocompleteFragment = childFragmentManager.findFragmentById(R.id.autoComplete) as AutocompleteSupportFragment
        autocompleteFragment.setTypesFilter(Arrays.asList(TypeFilter.REGIONS.toString()))
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener{
            override fun onError(p0: Status) {
                Toast.makeText(requireContext(), "$p0", Toast.LENGTH_SHORT).show()
            }

            override fun onPlaceSelected(p0: Place) {
                map.addMarker(MarkerOptions().position(p0.latLng).title("Chosed Location"))
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(p0.latLng, 12f))

            }

        })
        mapFragment?.getMapAsync(callback)
    }
}