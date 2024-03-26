package com.example.skycast.location.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.R
import com.example.skycast.databinding.SearchItemBinding
import com.example.skycast.location.model.Place

class SearchListAdapter (val onClick : (Place) -> Unit) : ListAdapter<Place, SearchViewHolder>(SearchDiffUtil()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding : SearchItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.search_item, parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.binding.place = getItem(position)
        holder.binding.layout.setOnClickListener { onClick(getItem(position)) }
    }
}
class SearchViewHolder(val binding : SearchItemBinding): RecyclerView.ViewHolder(binding.root)
class SearchDiffUtil : DiffUtil.ItemCallback<Place>(){
    override fun areItemsTheSame(oldItem: Place, newItem: Place): Boolean {
        return (oldItem.lat == newItem.lat && oldItem.lon == newItem.lon)
    }

    override fun areContentsTheSame(oldItem: Place, newItem: Place): Boolean {
        return oldItem == newItem
    }

}