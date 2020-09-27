package com.amir.ir.privatestore.ui.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.databinding.ItemCategoryBinding
import com.amir.ir.privatestore.databinding.ItemCategoryPreLollipopBinding
import com.amir.ir.privatestore.models.Category
import com.amir.ir.privatestore.ui.listeners.Interaction

class CategoryAdapter(private val interaction: Interaction<Category>? = null) :
    ListAdapter<Category, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Category>() {

            override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
                return oldItem == newItem
            }

        }
    }

    private lateinit var layoutInflater: LayoutInflater


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (!::layoutInflater.isInitialized) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                DataBindingUtil.inflate<ItemCategoryBinding>(
                    layoutInflater,
                    R.layout.item_category,
                    parent,
                    false
                )
            } else {
                DataBindingUtil.inflate<ItemCategoryPreLollipopBinding>(
                    layoutInflater,
                    R.layout.item_category_pre_lollipop,
                    parent,
                    false
                )
            }
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CategoryViewHolder -> {
                holder.bindCategory(currentList[position])
            }
        }
    }

    inner class CategoryViewHolder(private val mBinding: ViewBinding) :
        RecyclerView.ViewHolder(mBinding.root) {


        fun bindCategory(item: Category) {
            itemView.setOnClickListener {
                interaction?.onItemClicked(item)
            }

            if (mBinding is ItemCategoryBinding) {
                mBinding.category = item
                mBinding.executePendingBindings()
            } else if (mBinding is ItemCategoryPreLollipopBinding) {
                mBinding.category = item
                mBinding.executePendingBindings()
            }
        }
    }
}

