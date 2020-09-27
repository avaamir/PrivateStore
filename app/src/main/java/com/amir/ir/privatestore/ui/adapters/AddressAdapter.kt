package com.amir.ir.privatestore.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.databinding.ItemAddressVerticalBinding
import com.amir.ir.privatestore.models.Address
import com.amir.ir.privatestore.ui.customs.BaseSelectableAdapter

class AddressAdapter(
    private val isSelectable: Boolean = false,
    private val onItemClickListener: AddressInteractions? = null
) :
    BaseSelectableAdapter<Address>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Address>() {

            override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
                return newItem.id == oldItem.id
            }

            override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
                return (oldItem.isSelected == newItem.isSelected) && oldItem == newItem //kotlin dar auto generated functions (dar in ja manzur be equals hast(==)) faghat field haee ro dar nazar migire ke dar primary constructor hstand va isSelected dar primary constructor nist va bayad dasti ezafe beshe
            }

        }
    }

    private lateinit var layoutInflater: LayoutInflater


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (!::layoutInflater.isInitialized) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding = DataBindingUtil.inflate<ItemAddressVerticalBinding>(
            layoutInflater,
            R.layout.item_address_vertical,
            parent,
            false
        )
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddressViewHolder -> {
                holder.bindAddress(currentList[position])
            }
        }
    }

    override fun onItemSelected(t: Address?, position: Int) {
        onItemClickListener?.onAddressSelected(t)
    }

    inner class AddressViewHolder(private val mBinding: ItemAddressVerticalBinding) : RecyclerView.ViewHolder(mBinding.root) {
        init {
            if (isSelectable) {
                mBinding.frameSelect.visibility = View.VISIBLE
            } else {
                mBinding.frameSelect.visibility = View.INVISIBLE
            }
        }

        fun bindAddress(address: Address) {

            mBinding.address = address
            mBinding.executePendingBindings()

            mBinding.btnShowOnMap.setOnClickListener {
                onItemClickListener?.onShowOnMap(address)
            }

            mBinding.tvDelete.setOnClickListener {
                onItemClickListener?.onDeleteClicked(address)
            }

            mBinding.tvEdit.setOnClickListener {
                onItemClickListener?.onEditClicked(address)
            }
            //TODO ye juri bebaresh tu superClass(BaseSelectableAdapter) ke ketabkhune khubi she
            //------------------------------------------------
            /*update ui here on select and deSelect*/
            if (isSelectable) {
                mBinding.radioButton.isChecked = address.isSelected

                mBinding.radioButton.setOnClickListener {
                    super@AddressAdapter.onItemClicked(absoluteAdapterPosition)
                }
                mBinding.root.setOnClickListener {
                    super@AddressAdapter.onItemClicked(absoluteAdapterPosition)
                }
            }
            //----------------------------------------------------
        }
    }

    override var Address.isItemSelected: Boolean
        get() {
            return this.isSelected
        }
        set(value) { this.isSelected = value}

    interface AddressInteractions {
        fun onShowOnMap(address: Address)
        fun onAddressSelected(address: Address?)
        fun onEditClicked(address: Address)
        fun onDeleteClicked(address: Address)
    }

}

