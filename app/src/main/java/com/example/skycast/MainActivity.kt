package com.example.skycast

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.skycast.databinding.ActivityMainBinding
import com.example.skycast.model.local.UserSettingsDataSource
import com.google.android.material.snackbar.Snackbar
import java.util.Locale


private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.materialToolbar)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        binding.navigationView.bringToFront()
        var toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.materialToolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        setupWithNavController(binding.navigationView, navController)
        binding.navigationView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.homeFragment ->{
                    navController.navigate(R.id.homeFragment)
                    if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        binding.drawerLayout.closeDrawer(GravityCompat.START)
                    }
                    true
                }
                R.id.alertFragment ->{
                    navController.navigate(R.id.alertFragment)
                    if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        binding.drawerLayout.closeDrawer(GravityCompat.START)
                    }
                    true
                }
                R.id.favoritesFragment ->{
                    navController.navigate(R.id.favoritesFragment)
                    if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        binding.drawerLayout.closeDrawer(GravityCompat.START)
                    }
                    true
                }
                R.id.settingsFragment ->{
                    navController.navigate(R.id.settingsFragment)
                    if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        binding.drawerLayout.closeDrawer(GravityCompat.START)
                    }
                    true
                }
                else ->{
                    true
                }
            }
        }
        val connectivityManager = getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }
    val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // network is available for use
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Snackbar.make(binding.navigationView,
                getString(R.string.back_online), Snackbar.LENGTH_SHORT)
                .setBackgroundTint(resources.getColor(R.color.green))
                .show()
        }

        // Network capabilities have changed for the network
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            val unmetered = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
        }

        // lost network connection
        override fun onLost(network: Network) {
            super.onLost(network)
            Snackbar.make(binding.navigationView,
                getString(R.string.you_are_currently_offline), Snackbar.LENGTH_SHORT)
                .show()
        }
    }
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(setAppLocale(newBase))
    }
    fun setAppLocale(context: Context?) : Context?{
        val settingsDataSource = context?.let { UserSettingsDataSource.getInstance(it) }
        val locale = Locale(settingsDataSource?.getPreferredLanguage()?: "en")
        Locale.setDefault(locale)
        val config = Configuration(context?.resources?.configuration)
        config.locale = locale
        return context?.createConfigurationContext(config)
    }

}