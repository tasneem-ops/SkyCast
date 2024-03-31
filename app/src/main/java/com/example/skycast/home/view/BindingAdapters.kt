package com.example.skycast.home.view

import android.location.Geocoder
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.skycast.R
import java.text.SimpleDateFormat
import java.util.Locale

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

@BindingAdapter("dateTime")
fun setDateTimeText(view: TextView, dateTime : Long){
    view.text = SimpleDateFormat("EE dd/MM/yyyy", Locale.getDefault()).format(dateTime)
}
@BindingAdapter("twoLineDateTime")
fun setTwoLineDateTimeText(view: TextView, dateTime : Long){
    view.text = SimpleDateFormat("hh:mm\nEE dd/MM/yy", Locale.getDefault()).format(dateTime)
}
@BindingAdapter("dateTime")
fun setDateTimeText(view: TextView, dateTime : Int){
    view.text = SimpleDateFormat("EE dd/MM/yyyy", Locale.getDefault()).format(dateTime*1000L)
}

@BindingAdapter("temp", "addSlash")
fun setTempInt(view: TextView, temp : Double, addSlash : Boolean = false){
    if(addSlash){
        view.text = "\\" + Math.round(temp).toString() + "°"
    }
    else{
        view.text = Math.round(temp).toString() + "°"
    }
}
@BindingAdapter("temp")
fun setTempInt(view: TextView, temp : Double){
    view.text = Math.round(temp).toString() + "°"
}
@BindingAdapter("temp", "unit")
fun setTempInt(view: TextView, temp : Double, unit : String){
    view.text = Math.round(temp).toString() + "°" + unit
}
//@BindingAdapter("latitude", "longitude")
//fun latLngToCityName(view: TextView, latitude : Double, longitude: Double){
//    val address = Geocoder(view.context).getFromLocation(latitude, longitude, 1)
//    if(address == null)
//        return
//    if (address.isEmpty())
//        return
//    view.text = address.get(0)?.locality
//}