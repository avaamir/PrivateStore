package com.amir.ir.privatestore.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.databinding.ItemProductVerticalBinding
import com.amir.ir.privatestore.models.Product
import com.amir.ir.privatestore.ui.listeners.ProductInteraction

class ProductVerticalAdapter(private val interaction: ProductInteraction? = null) :
    ListAdapter<Product, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return newItem.pid == newItem.pid
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return newItem == oldItem
            }

        }
    }

    private lateinit var layoutInflater: LayoutInflater


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (!::layoutInflater.isInitialized) {
            layoutInflater = LayoutInflater.from(parent.context) //todo it may cause memory leak if context has been destroyed
        }
        val binding = DataBindingUtil.inflate<ItemProductVerticalBinding>(
            layoutInflater,
            R.layout.item_product_vertical,
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProductViewHolder -> {
                holder.bindProduct(currentList[position])
            }
        }

        if (position == currentList.size - 3) {
            interaction?.onListEnd()
        } else if (position == 3) {
            interaction?.onListTop()
        }


    }

    inner class ProductViewHolder(private val mBinding: ItemProductVerticalBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        private val colorRed: Int = ContextCompat.getColor(itemView.context, R.color.deep_red)
        private val colorGreen: Int = ContextCompat.getColor(itemView.context, R.color.green)


        fun bindProduct(product: Product) {
            mBinding.product = product
            mBinding.executePendingBindings()

            if (product.hasDiscount) {
                mBinding.gpDiscountPrice.visibility = View.VISIBLE
                mBinding.tvRedPrice.text = product.mainPrice
                mBinding.tvGreenPrice.text = product.discountPrice
            } else {
                mBinding.gpDiscountPrice.visibility = View.INVISIBLE
                mBinding.tvGreenPrice.text = product.mainPrice
            }

            itemView.setOnClickListener {
                interaction?.onItemClicked(product)
            }
        }
    }
}

