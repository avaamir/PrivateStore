package com.amir.ir.privatestore.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.models.requests.SearchAddressItem

class SearchAddressAdapter(private val interaction: Interaction? = null) :
    ListAdapter<SearchAddressItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SearchAddressItem>() {

            override fun areItemsTheSame(
                oldItem: SearchAddressItem,
                newItem: SearchAddressItem
            ): Boolean {
                return oldItem.address == newItem.address
            }

            override fun areContentsTheSame(
                oldItem: SearchAddressItem,
                newItem: SearchAddressItem
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    private lateinit var layoutInflater: LayoutInflater


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (!::layoutInflater.isInitialized) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val view = layoutInflater.inflate(
            R.layout.item_search_address,
            parent,
            false
        )
        return ItemSearchAddressViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemSearchAddressViewHolder -> {
                holder.bindSearchAddressItem(currentList[position])
            }
        }
    }

    inner class ItemSearchAddressViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        private val tvAddress: TextView = view.findViewById(R.id.tv_address)
        private val tvTitle: TextView = view.findViewById(R.id.tv_place_name)

        fun bindSearchAddressItem(item: SearchAddressItem) {
            itemView.setOnClickListener {
                interaction?.onItemClicked(item)
            }

            tvTitle.text = item.title
            tvAddress.text = item.address
        }
    }

    interface Interaction {
        fun onItemClicked(item: SearchAddressItem)
    }
}

