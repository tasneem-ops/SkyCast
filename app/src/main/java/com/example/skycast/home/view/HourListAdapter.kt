package com.example.skycast.home.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.R
import com.example.skycast.databinding.HourForecastItemBinding
import com.example.skycast.model.dto.HourForecast

class HourListAdapter () : ListAdapter<HourForecast, HourViewHolder>(HourDiffUtil()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        val binding : HourForecastItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.hour_forecast_item, parent, false)
        return HourViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        holder.binding.hour = getItem(position)
        holder.binding.temp.text = getItem(position).temp?.toInt().toString() +"°C"
    }
}
class HourViewHolder(val binding : HourForecastItemBinding): RecyclerView.ViewHolder(binding.root)
class HourDiffUtil : DiffUtil.ItemCallback<HourForecast>(){
    override fun areItemsTheSame(oldItem: HourForecast, newItem: HourForecast): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: HourForecast, newItem: HourForecast): Boolean {
        return oldItem == newItem
    }

}