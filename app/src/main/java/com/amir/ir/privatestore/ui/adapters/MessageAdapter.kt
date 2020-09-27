package com.amir.ir.privatestore.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.databinding.ItemMessageBinding
import com.amir.ir.privatestore.models.Message

class MessageAdapter(private val interaction: Interaction? = null) :
    ListAdapter<Message, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Message>() {

            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
                return newItem.id == oldItem.id
            }

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
                return newItem == oldItem
            }

        }
    }

    private lateinit var layoutInflater: LayoutInflater


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (!::layoutInflater.isInitialized) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding = DataBindingUtil.inflate<ItemMessageBinding>(
            layoutInflater,
            R.layout.item_message,
            parent,
            false
        )
        return MessageHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MessageHolder -> {
                holder.bindMessage(currentList[position])
            }
        }
    }

    inner class MessageHolder(private val mBinding: ItemMessageBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bindMessage(message: Message) {
            itemView.setOnClickListener {
                interaction?.onItemClicked(message)
            }

            mBinding.message = message
            mBinding.executePendingBindings()
        }
    }

    interface Interaction {
        fun onItemClicked(item: Message)
    }
}

