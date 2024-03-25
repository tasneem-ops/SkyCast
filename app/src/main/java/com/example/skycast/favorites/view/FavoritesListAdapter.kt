package com.example.skycast.favorites.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.R
import com.example.skycast.databinding.AlertItemBinding
import com.example.skycast.databinding.FavItemBinding
import com.example.skycast.favorites.model.dto.FavDTO

class FavoritesListAdapter (val onDeleteClick : (FavDTO) -> Unit, val onItemClick : (FavDTO) -> Unit) : ListAdapter<FavDTO, FavViewHolder>(FavDiffUtil()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val binding : FavItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.fav_item, parent, false)
        return FavViewHolder(binding)
    }
    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        holder.binding.fav = getItem(position)
        holder.binding.imageView3.setOnClickListener { onDeleteClick(getItem(position)) }
        holder.binding.layout.setOnClickListener { onItemClick(getItem(position)) }
    }
}
class FavViewHolder(val binding : FavItemBinding): RecyclerView.ViewHolder(binding.root)
class FavDiffUtil : DiffUtil.ItemCallback<FavDTO>(){
    override fun areItemsTheSame(oldItem: FavDTO, newItem: FavDTO): Boolean {
        return (oldItem.latitude == newItem.latitude && oldItem.longitude == newItem.longitude)
    }

    override fun areContentsTheSame(oldItem: FavDTO, newItem: FavDTO): Boolean {
        return oldItem == newItem
    }

}