package com.amir.ir.privatestore.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.databinding.ItemColorPickBinding
import com.amir.ir.privatestore.models.Color


class PickColorAdapter(
    private val colors: ArrayList<Color>,
    private val onColorPick: OnColorPick? = null
) : RecyclerView.Adapter<PickColorAdapter.ColorPickViewHolder>() {

    private lateinit var lastColor: Color
    private var lastSelectedColorPosition = 0
    private lateinit var inflater: LayoutInflater

    init {
        if (colors.isNullOrEmpty()) {
            throw Throwable("Colors Array can not be null or empty!!")
        } else {
            for (i in colors.indices) {
                if (colors[i].isSelected) {
                    lastColor = colors[i]
                    lastSelectedColorPosition = i
                    onColorPick?.onPickColor(lastColor)
                    break
                }
            }

            if (!::lastColor.isInitialized) {
                lastColor = colors[0]
                lastColor.isSelected = true
                lastSelectedColorPosition = 0
                onColorPick?.onPickColor(lastColor)
            }
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ColorPickViewHolder {
        if (!::inflater.isInitialized) {
            inflater = LayoutInflater.from(parent.context)
        }
        return ColorPickViewHolder(
            DataBindingUtil.inflate(
                inflater,
                R.layout.item_color_pick,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ColorPickViewHolder, position: Int) {
        val color: Color = colors[position]
        holder.bind(color)
    }

    override fun getItemCount(): Int {
        return colors.size
    }

    inner class ColorPickViewHolder(val mBinding: ItemColorPickBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bind(color: Color) {
            mBinding.tvColorName.text = color.name
            mBinding.color = color
            mBinding.executePendingBindings()

            if (color.isSelected) {
                mBinding.frameColorPick.setBackgroundResource(R.drawable.shape_square_color_pick_selected)
            } else {
                mBinding.frameColorPick.setBackgroundResource(R.drawable.shape_square_color_pick_not_selected)
            }

            itemView.setOnClickListener {

                if (lastColor !== color) {
                    lastColor.isSelected = false
                    color.isSelected = true
                    lastColor = color
                    notifyItemChanged(lastSelectedColorPosition)
                    notifyItemChanged(adapterPosition)
                    lastSelectedColorPosition = adapterPosition
                    onColorPick?.onPickColor(color)
                }

            }

        }

    }

    interface OnColorPick {
        fun onPickColor(color: Color)
    }
}
