package com.example.skycast.home.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.R
import com.example.skycast.databinding.HourForecastItemBinding
import com.example.skycast.home.model.dto.HourlyWeather
import com.example.skycast.settings.model.UserSettingsDataSource.Companion.UNIT_CELSIUS
import com.example.skycast.settings.model.UserSettingsDataSource.Companion.UNIT_FAHRENHEIT
import com.example.skycast.settings.model.UserSettingsDataSource.Companion.UNIT_KELVIN
import java.text.SimpleDateFormat
import java.util.Locale

class HourlyListAdapter (val unit : String) : ListAdapter<HourlyWeather, HourViewHolder>(HourDiffUtil()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        val binding : HourForecastItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.hour_forecast_item, parent, false)
        return HourViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        holder.binding.weather = getItem(position)
        when(unit){
            UNIT_CELSIUS -> {
                holder.binding.unit = holder.binding.temp.context.getString(R.string.celcuis_unit)
            }
            UNIT_FAHRENHEIT ->{
                holder.binding.unit = holder.binding.temp.context.getString(R.string.fahrenheit_unit)
            }
            UNIT_KELVIN ->{
                holder.binding.unit = holder.binding.temp.context.getString(R.string.kelvin_unit)
            }
            else ->{
                holder.binding.unit = holder.binding.temp.context.getString(R.string.celcuis_unit)
            }
        }

        holder.binding.timeText.text = SimpleDateFormat("hh:mm aa" , Locale.getDefault()).format(getItem(position).dt * 1000L)
    }
}
class HourViewHolder(val binding : HourForecastItemBinding): RecyclerView.ViewHolder(binding.root)
class HourDiffUtil : DiffUtil.ItemCallback<HourlyWeather>(){
    override fun areItemsTheSame(oldItem: HourlyWeather, newItem: HourlyWeather): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: HourlyWeather, newItem: HourlyWeather): Boolean {
        return oldItem == newItem
    }

}