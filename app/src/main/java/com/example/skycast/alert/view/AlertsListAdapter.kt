package com.example.skycast.alert.view

import android.content.Context
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.R
import com.example.skycast.alert.model.dto.AlertDTO
import com.example.skycast.databinding.AlertItemBinding
import com.example.skycast.databinding.DayForecastItemBinding
import com.example.skycast.home.model.dto.DailyWeather
import java.text.SimpleDateFormat
import java.util.Locale

class AlertsListAdapter (val onClick : (AlertDTO) -> Unit) : ListAdapter<AlertDTO, AlertViewHolder>(DailyDiffUtil()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val binding : AlertItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.alert_item, parent, false)
        return AlertViewHolder(binding)
    }
    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        holder.binding.alert = getItem(position)
        holder.binding.deleteBtn.setOnClickListener { onClick(getItem(holder.adapterPosition)) }
        holder.binding.cityName.text = latLngToCityName(getItem(position).latitude, getItem(position).longitude, holder.binding.cityName.context)
    }
    fun latLngToCityName( latitude : Double, longitude: Double, context: Context) : String?{
        try {
            val address = Geocoder(context).getFromLocation(latitude, longitude, 1)
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
}
class AlertViewHolder(val binding : AlertItemBinding): RecyclerView.ViewHolder(binding.root)
class DailyDiffUtil : DiffUtil.ItemCallback<AlertDTO>(){
    override fun areItemsTheSame(oldItem: AlertDTO, newItem: AlertDTO): Boolean {
        return (oldItem.latitude == newItem.latitude && oldItem.longitude == newItem.longitude && oldItem.start == newItem.start)
    }

    override fun areContentsTheSame(oldItem: AlertDTO, newItem: AlertDTO): Boolean {
        return oldItem == newItem
    }

}