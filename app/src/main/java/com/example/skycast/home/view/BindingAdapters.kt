package com.example.skycast.home.view

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.skycast.R

//@BindingAdapter("description")
//fun showWeatherStatusImage(view : ImageView, description : String?){
//    val statusIconMap : HashMap<String, Int> = hashMapOf("clear sky" to R.drawable.sunny, "few clouds" to R.drawable.few_clouds,
//        "mist" to R.drawable.mist, "scattered clouds" to R.drawable.scattered_clouds, "broken clouds" to R.drawable.many_clouds,
//        "shower rain" to R.drawable.rain, "rain" to R.drawable.light_rain, "thunderstorm" to R.drawable.thunderstorm, "snow" to R.drawable.snow)
//
//    view.setImageResource(statusIconMap.get(description) ?: R.drawable.few_clouds)
//}

@BindingAdapter("weatherIcon")
fun showWeatherStatusImage(view: ImageView, weatherIcon : Int){
    val iconId = when{
        weatherIcon < 300 ->{R.drawable.thunderstorm}
        ((weatherIcon >= 300 && weatherIcon < 500) || (weatherIcon >=520 && weatherIcon <= 531)) -> {
            R.drawable.rain
        }
        weatherIcon >=500 && weatherIcon < 505 -> {
            R.drawable.light_rain
        }
        (weatherIcon == 511) || (weatherIcon >=600 && weatherIcon <700)->{
            R.drawable.snow
        }
        weatherIcon >=700 && weatherIcon < 800 ->{
            R.drawable.mist
        }
        weatherIcon == 800 ->{
            R.drawable.sunny
        }
        weatherIcon == 801 ->{
            R.drawable.few_clouds
        }
        weatherIcon == 802 ->{
            R.drawable.scattered_clouds
        }
        weatherIcon == 803 || weatherIcon == 804 ->{
            R.drawable.many_clouds
        }

        else ->{
            R.drawable.few_clouds
        }
    }
    view.setImageResource(iconId)
}