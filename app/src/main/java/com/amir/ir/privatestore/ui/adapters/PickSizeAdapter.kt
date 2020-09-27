package com.amir.ir.privatestore.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.databinding.ItemSizePickBinding
import com.amir.ir.privatestore.models.Size

class PickSizeAdapter(
    private val sizes: ArrayList<Size>,
    private val onSizePick: OnSizePick? = null
) : RecyclerView.Adapter<PickSizeAdapter.PickSizeViewHolder>() {

    private lateinit var lastSize: Size
    private var lastSelectedSizePosition = 0
    private lateinit var inflater: LayoutInflater

    init {
        if (sizes.isNullOrEmpty()) {
            throw Throwable("Sizes Array can not be null or empty!!")
        } else {
            sizes.forEachIndexed loop@{ index, size ->
                if (size.isSelected) {
                    lastSize = sizes[index]
                    lastSize.isSelected = true
                    lastSelectedSizePosition = index
                    onSizePick?.onPickSize(lastSize)
                    return@loop
                }
            }

            if (!::lastSize.isInitialized) {
                lastSize = sizes[0]
                lastSize.isSelected = true
                lastSelectedSizePosition = 0
                onSizePick?.onPickSize(lastSize)
            }
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PickSizeViewHolder {
        if (!::inflater.isInitialized) {
            inflater = LayoutInflater.from(parent.context)
        }
        return PickSizeViewHolder(
            DataBindingUtil.inflate(
                inflater,
                R.layout.item_size_pick,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PickSizeViewHolder, position: Int) {
        val size: Size = sizes[position]
        holder.bind(size)
    }

    override fun getItemCount(): Int {
        return sizes.size
    }

    inner class PickSizeViewHolder(val mBinding: ItemSizePickBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bind(size: Size) {
            mBinding.size = size
            mBinding.executePendingBindings()

            if (size.isSelected) {
                mBinding.frameSizePick.setBackgroundResource(R.drawable.shape_square_color_pick_selected)
            } else {
                mBinding.frameSizePick.setBackgroundResource(R.drawable.shape_square_color_pick_not_selected)
            }

            itemView.setOnClickListener {

                if (lastSize !== size) {
                    lastSize.isSelected = false
                    size.isSelected = true
                    lastSize = size
                    notifyItemChanged(lastSelectedSizePosition)
                    notifyItemChanged(adapterPosition)
                    lastSelectedSizePosition = adapterPosition
                    onSizePick?.onPickSize(size)
                }

            }

        }

    }

    interface OnSizePick {
        fun onPickSize(size: Size)
    }
}
