package com.amir.ir.privatestore.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.databinding.ItemCartItemBinding
import com.amir.ir.privatestore.models.CartItem
import com.amir.ir.privatestore.ui.bindingadapters.priceToStr

class CartAdapter(private val interaction: Interaction? = null) :
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
        val binding = DataBindingUtil.inflate<ItemCartItemBinding>(
            layoutInflater,
            R.layout.item_cart_item,
            parent,
            false
        )
        return CartItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CartItemViewHolder -> {
                holder.bindCartItem(currentList[position])
            }
        }
    }

    inner class CartItemViewHolder(private val mBinding: ItemCartItemBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bindCartItem(item: CartItem) {
            mBinding.cartItem = item
            mBinding.executePendingBindings()

            if(item.count == 0) {
                mBinding.tvOutOfStock.visibility = View.VISIBLE
                mBinding.frameCount.visibility = View.GONE
            } else {
                mBinding.tvOutOfStock.visibility = View.GONE
                mBinding.frameCount.visibility = View.VISIBLE
            }
            
            if (item.hasDiscount) {
                mBinding.tvDiscountAmount.text =
                    priceToStr(item.count * (item.mainPrice.toInt() - item.discountPrice!!.toInt()))
                mBinding.tvProductPrice.text = priceToStr(item.discountPrice)
            } else {
                mBinding.tvDiscountAmount.text = "0"
                mBinding.tvProductPrice.text = priceToStr(item.mainPrice)
            }

            mBinding.tvTotalFinalPrice.text = if (item.hasDiscount) {
                priceToStr(item.count * item.discountPrice!!.toInt())
            } else {
                priceToStr(item.count * item.mainPrice.toInt())
            }

            if (item.colorName.isNullOrBlank()) {
                mBinding.tvColorName.visibility = View.VISIBLE  //todo behare kole radif hazf beshse
                mBinding.ivColor.visibility = View.GONE
                mBinding.frameColorPick.setBackgroundResource(R.color.gray50)
            } else {
                mBinding.tvColorName.visibility = View.GONE  //todo behare kole radif hazf beshse
                mBinding.ivColor.visibility = View.VISIBLE
                mBinding.frameColorPick.setBackgroundResource(R.drawable.shape_square_color_pick_not_selected)
            }

            mBinding.ivProductPic.setOnClickListener {
                interaction?.onProductImageClicked(item)
            }

            mBinding.ivInc.setOnClickListener {
                interaction?.onIncreaseClicked(item.copy()) //agar copy ro nafresim va khodesh ro befrestim vaghti increase rokhe mide, ehtemalan dar ui tasmim bar in mishe ke yek dune be cartItem ezafe beshe va list update beshe, ama chun currentList mojud dar adapter va ham ui be yek refrence eshare dashtan vaghti mikhaym update konim ui update nemishe chun tuyeDiff callback sharayete areItemsContentTheSame hamishe true hast, vali age scroll konim dorost mishe ghaedatan
            }

            mBinding.ivDecrement.setOnClickListener {
                interaction?.onDecreaseClicked(item.copy())
            }

            mBinding.tvDelete.setOnClickListener {
                interaction?.onDeleteClicked(item)
            }
        }

    }

    interface Interaction {
        fun onDeleteClicked(item: CartItem)
        fun onDecreaseClicked(item: CartItem)
        fun onIncreaseClicked(item: CartItem)
        fun onProductImageClicked(item: CartItem)
    }
}

