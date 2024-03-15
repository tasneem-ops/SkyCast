package com.example.skycast.home.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.R
import com.example.skycast.databinding.DayForecastItemBinding
import com.example.skycast.model.dto.HourForecast

class DailyListAdapter () : ListAdapter<HourForecast, DailyViewHolder>(DailyDiffUtil()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val binding : DayForecastItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.day_forecast_item, parent, false)
        return DailyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        holder.binding.forecast = getItem(position)
        holder.binding.tempMaxText.text = getItem(position).tempMax?.toInt().toString() +"°"
        holder.binding.tempMinText.text = "/" + getItem(position).tempMin?.toInt().toString()+"°"
    }
}
class DailyViewHolder(val binding : DayForecastItemBinding): RecyclerView.ViewHolder(binding.root)
class DailyDiffUtil : DiffUtil.ItemCallback<HourForecast>(){
    override fun areItemsTheSame(oldItem: HourForecast, newItem: HourForecast): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: HourForecast, newItem: HourForecast): Boolean {
        return oldItem == newItem
    }

}