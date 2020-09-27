package com.amir.ir.privatestore.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.databinding.ItemFavoriteBinding
import com.amir.ir.privatestore.models.Favorite
import kotlinx.android.synthetic.main.item_favorite.view.*

class FavoriteAdapter(private val interaction: Interaction? = null) :
    ListAdapter<Favorite, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Favorite>() {

            override fun areItemsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
                return oldItem.pid == newItem.pid
            }

            override fun areContentsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
                return oldItem == newItem
            }

        }
    }

    private lateinit var layoutInflater: LayoutInflater


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (!::layoutInflater.isInitialized) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding = DataBindingUtil.inflate<ItemFavoriteBinding>(
            layoutInflater,
            R.layout.item_favorite,
            parent,
            false
        )
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FavoriteViewHolder -> {
                holder.bindFavorite(currentList[position])
            }
        }
    }

    inner class FavoriteViewHolder(private val mBinding: ItemFavoriteBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bindFavorite(item: Favorite) {
            itemView.setOnClickListener {
                interaction?.onItemClicked(item)
            }

            itemView.btnDelete.setOnClickListener {
                interaction?.onDeleteClicked(item)
            }

            mBinding.favorite = item
            mBinding.executePendingBindings()
        }
    }

    interface Interaction {
        fun onItemClicked(item: Favorite)
        fun onDeleteClicked(item: Favorite)
    }
}

