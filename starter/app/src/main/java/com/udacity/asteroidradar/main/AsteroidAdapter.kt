package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.databinding.ItemListBinding

class AsteroidAdapter(val clickListener: ClickListener) : ListAdapter<Asteroid, AsteroidAdapter.ViewHolder>(DiffItem()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val asteroid = getItem(position)
        holder.bind(asteroid)
        holder.itemView.setOnClickListener {
            clickListener.onClick(asteroid)
        }
    }

    class ViewHolder(val binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(asteroid: Asteroid) {
            binding.asteroid = asteroid
        }
        companion object {
            fun from(parent: ViewGroup) : ViewHolder{
                val inflater = LayoutInflater.from(parent.context)
                val view = ItemListBinding.inflate(inflater, parent, false)
                return ViewHolder(view)
            }
        }

    }

    class DiffItem : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem == newItem
        }

    }

    class ClickListener(val function : (Asteroid) -> Unit) {
        fun onClick(asteroid: Asteroid) {
            function(asteroid)
        }
    }
}