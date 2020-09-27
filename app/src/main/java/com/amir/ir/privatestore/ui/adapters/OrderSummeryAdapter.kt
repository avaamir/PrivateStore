package com.amir.ir.privatestore.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.databinding.ItemOrderSummeryBinding
import com.amir.ir.privatestore.models.CartItem

class OrderSummeryAdapter :
    ListAdapter<CartItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CartItem>() {

            override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
                return oldItem == newItem
            }

        }
    }

    private lateinit var layoutInflater: LayoutInflater


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (!::layoutInflater.isInitialized) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding = DataBindingUtil.inflate<ItemOrderSummeryBinding>(
            layoutInflater,
            R.layout.item_order_summery,
            parent,
            false
        )
        return OrderSummeryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is OrderSummeryViewHolder -> {
                holder.bindCartItem(currentList[position])
            }
        }
    }

    inner class OrderSummeryViewHolder(private val mBinding: ItemOrderSummeryBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bindCartItem(item: CartItem) {
            mBinding.cartItem = item
            mBinding.executePendingBindings()

            mBinding.tvItemCount.text = item.count.toString()

            if (item.hasDiscount) {
                mBinding.tvRedPrice.text = item.mainPrice
                mBinding.tvGreenPrice.text = item.discountPrice
                mBinding.gpDiscountPrice.visibility = View.VISIBLE
            } else {
                mBinding.tvRedPrice.text = item.discountPrice
                mBinding.tvGreenPrice.text = item.mainPrice
                mBinding.gpDiscountPrice.visibility = View.INVISIBLE
            }

        }
    }
}

