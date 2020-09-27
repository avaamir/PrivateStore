package com.amir.ir.privatestore.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.databinding.ItemTrackOrderBinding
import com.amir.ir.privatestore.models.Order
import com.amir.ir.privatestore.models.enums.OrderStatus
import com.amir.ir.privatestore.utils.exhaustive

class TrackOrderAdapter(private val interaction: Interaction? = null) :
    ListAdapter<Order, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Order>() {

            override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
                return oldItem == newItem
            }

        }
    }

    private lateinit var layoutInflater: LayoutInflater


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (!::layoutInflater.isInitialized) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding = DataBindingUtil.inflate<ItemTrackOrderBinding>(
            layoutInflater,
            R.layout.item_track_order,
            parent,
            false
        )
        return TrackOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TrackOrderViewHolder -> {
                holder.bindOrder(currentList[position])
            }
        }
    }

    inner class TrackOrderViewHolder(private val mBinding: ItemTrackOrderBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bindOrder(item: Order) {
            mBinding.ivMore.setOnClickListener {
                interaction?.onMoreDetailsClicked(item)
            }


            when (item.status) {
                OrderStatus.Delivered -> mBinding.tvStatus.setTextColor(Color.parseColor("#00E676"))
                OrderStatus.Preparation -> mBinding.tvStatus.setTextColor(Color.parseColor("#D6D020"))
                OrderStatus.Failed -> mBinding.tvStatus.setTextColor(Color.parseColor("#b7342a"))
                OrderStatus.Delivering -> mBinding.tvStatus.setTextColor(Color.parseColor("#c600c6"))
            }.exhaustive()

            if (item.status == OrderStatus.Delivering) {
                mBinding.tvStatus.text = item._status
            } else {
                mBinding.tvStatus.text = item.status.strType
            }

            mBinding.order = item
            mBinding.executePendingBindings()
        }
    }

    interface Interaction {
        fun onMoreDetailsClicked(item: Order)
    }
}

