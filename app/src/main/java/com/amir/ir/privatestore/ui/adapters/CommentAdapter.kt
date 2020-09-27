package com.amir.ir.privatestore.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.databinding.ItemCommentBinding
import com.amir.ir.privatestore.models.Comment

class CommentAdapter(private val interaction: Interaction? = null) :
    ListAdapter<Comment, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Comment>() {

            override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return oldItem == newItem
            }

        }
    }

    private lateinit var layoutInflater: LayoutInflater


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (!::layoutInflater.isInitialized) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding = DataBindingUtil.inflate<ItemCommentBinding>(
            layoutInflater,
            R.layout.item_comment,
            parent,
            false
        )
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CommentViewHolder -> {
                holder.bindComment(currentList[position])
            }
        }

        if (position == currentList.size - 3) {
            interaction?.onListEnd()
        }
    }

    inner class CommentViewHolder(private val mBinding: ItemCommentBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bindComment(item: Comment) {
            itemView.setOnClickListener {
                interaction?.onItemClicked(item)
            }

            mBinding.comment = item
            mBinding.executePendingBindings()

            mBinding.ratingBar.rating = item.rate
        }
    }

    interface Interaction {
        fun onItemClicked(item: Comment)
        fun onListEnd()
    }
}

