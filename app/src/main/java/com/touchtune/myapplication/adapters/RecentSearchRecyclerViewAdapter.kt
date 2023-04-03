package com.touchtune.myapplication.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.touchtune.myapplication.R
import com.touchtune.myapplication.data.RecentArtistSearch
import com.touchtune.myapplication.databinding.ListItemRecentsearchBinding

class RecentSearchRecyclerViewAdapter(
    private val onItemClick: (search: RecentArtistSearch) -> Unit
) : ListAdapter<RecentArtistSearch, RecentSearchRecyclerViewAdapter.RecentSearchViewHolder>(
    RecentSearchDiffCallback()
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecentSearchViewHolder {
        return RecentSearchViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.list_item_recentsearch,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: RecentSearchViewHolder,
        position: Int
    ) {
        Log.d("RecentSearchRecyclerViewAdapter", "position: " + position)
        holder.bind(getItem(position))
        holder.binding.setClickListener {
            onItemClick(getItem(position))
        }
    }

    class RecentSearchViewHolder(
        val binding: ListItemRecentsearchBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RecentArtistSearch) {
            binding.apply {
                recentArtistSearch = item
                executePendingBindings()
            }
        }

    }


}

private class RecentSearchDiffCallback : DiffUtil.ItemCallback<RecentArtistSearch>() {

    override fun areItemsTheSame(
        oldItem: RecentArtistSearch,
        newItem: RecentArtistSearch
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: RecentArtistSearch,
        newItem: RecentArtistSearch
    ): Boolean {
        return oldItem == newItem
    }
}