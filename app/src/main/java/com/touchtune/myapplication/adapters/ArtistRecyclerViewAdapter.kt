package com.touchtune.myapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.touchtune.myapplication.R
import com.touchtune.myapplication.data.Artist
import com.touchtune.myapplication.databinding.ListItemArtistBinding

class ArtistRecyclerViewAdapter(
    private val onItemClick: (artist: Artist) -> Unit
) : ListAdapter<Artist, ArtistRecyclerViewAdapter.ArtistViewHolder>(ArtistDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        return ArtistViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.list_item_artist,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.binding.setClickListener {
            onItemClick(getItem(position))
        }
    }

    class ArtistViewHolder(
        val binding: ListItemArtistBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Artist) {
            binding.apply {
                artist = item
                executePendingBindings()
            }
        }
    }
}

private class ArtistDiffCallback : DiffUtil.ItemCallback<Artist>() {

    override fun areItemsTheSame(
        oldItem: Artist,
        newItem: Artist
    ): Boolean {
        return oldItem.artistId == newItem.artistId
    }

    override fun areContentsTheSame(
        oldItem: Artist,
        newItem: Artist
    ): Boolean {
        return oldItem == newItem
    }
}