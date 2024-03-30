package com.example.skycast.home.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.R
import com.example.skycast.databinding.DayForecastItemBinding
import com.example.skycast.home.model.dto.DailyWeather
import java.text.SimpleDateFormat
import java.util.Locale

class DailyListAdapter () : ListAdapter<DailyWeather, DailyViewHolder>(DailyDiffUtil()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val binding : DayForecastItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.day_forecast_item, parent, false)
        return DailyViewHolder(binding)
    }
    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        holder.binding.forecast = getItem(position)
        holder.binding.textView.text = SimpleDateFormat("EE dd/MM/yy", Locale.US).format(getItem(position).dt*1000L)
        if(position.equals(0)){
            holder.binding.textView.text = (holder.binding.textView.context).getString(R.string.today)
        }
        if(position.equals(1)){
            holder.binding.textView.text = (holder.binding.textView.context).getString(R.string.tomorrow)
        }
    }
}
class DailyViewHolder(val binding : DayForecastItemBinding): RecyclerView.ViewHolder(binding.root)
class DailyDiffUtil : DiffUtil.ItemCallback<DailyWeather>(){
    override fun areItemsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
        return oldItem == newItem
    }

}