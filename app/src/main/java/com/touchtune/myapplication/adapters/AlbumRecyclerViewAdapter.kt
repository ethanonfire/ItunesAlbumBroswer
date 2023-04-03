package com.touchtune.myapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.touchtune.myapplication.R
import com.touchtune.myapplication.data.Album
import com.touchtune.myapplication.databinding.ListItemAlbumBinding

class AlbumRecyclerViewAdapter(
    private val onItemClick: (album: Album) -> Unit
) : ListAdapter<Album, AlbumRecyclerViewAdapter.AlbumViewHolder>(AlbumListDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {

        return AlbumViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.list_item_album,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {

        holder.bind(getItem(position))
        holder.binding.setClickListener {
            onItemClick(getItem(position))
        }
    }

    class AlbumViewHolder(
        val binding: ListItemAlbumBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Album) {
            binding.apply {
                album = item
                executePendingBindings()
            }
        }
    }
}

private class AlbumListDiffCallback : DiffUtil.ItemCallback<Album>() {

    override fun areItemsTheSame(
        oldItem: Album,
        newItem: Album
    ): Boolean {
        return oldItem.collectionId == newItem.collectionId
    }

    override fun areContentsTheSame(
        oldItem: Album,
        newItem: Album
    ): Boolean {
        return oldItem == newItem
    }
}