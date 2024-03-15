package com.example.skycast.home.view

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.skycast.R
import java.util.HashMap

@BindingAdapter("description")
fun showWeatherStatusImage(view : ImageView, description : String?){
    val statusIconMap : HashMap<String, Int> = hashMapOf("clear sky" to R.drawable.i01d, "few clouds" to R.drawable.i02d,
        "mist" to R.drawable.i50d, "scattered clouds" to R.drawable.i03d, "broken clouds" to R.drawable.i04d,
        "shower rain" to R.drawable.i09d, "rain" to R.drawable.i10d, "thunderstorm" to R.drawable.i11d, "snow" to R.drawable.i13d)

    view.setImageResource(statusIconMap.get(description) ?: R.drawable.i03d)
}