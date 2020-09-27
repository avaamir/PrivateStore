package com.amir.ir.privatestore.ui.fragments.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.databinding.LayoutAddressFragmentBinding
import com.amir.ir.privatestore.models.Address
import com.amir.ir.privatestore.ui.activities.AddAddressActivity
import com.amir.ir.privatestore.ui.activities.MapsActivity
import com.amir.ir.privatestore.ui.adapters.AddressAdapter
import com.amir.ir.privatestore.ui.adapters.AddressAdapter.AddressInteractions
import com.amir.ir.privatestore.utils.Constants
import com.amir.ir.privatestore.utils.EventObserver
import com.amir.ir.privatestore.utils.alert
import com.amir.ir.privatestore.utils.toast
import com.amir.ir.privatestore.viewmodels.AddressFragmentViewModel

class AddressFragment : Fragment(), AddressInteractions {
    private lateinit var adapter: AddressAdapter
    private lateinit var viewModel: AddressFragmentViewModel
    private lateinit var mBinding: LayoutAddressFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(AddressFragmentViewModel::class.java)
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.layout_address_fragment,
            container,
            false
        )
        initViews()
        observeViewModel()
        return mBinding.root
    }

    private fun observeViewModel() {
        viewModel.addresses.observe(viewLifecycleOwner, Observer { addresses ->
            if (!addresses.isNullOrEmpty())
                mBinding.animationView.visibility = View.GONE
            else
                mBinding.animationView.visibility = View.VISIBLE
            adapter.submitList(addresses)
        })

        viewModel.deleteAddressEvent.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                "done" -> {
                    toast("حذف شد")
                }
                "" -> {
                    toast(Constants.SERVER_ERROR)
                    println("debug: address toast")
                }
                else -> {
                    toast(it)
                }
            }
        })
    }

    private fun initViews() {
        adapter = AddressAdapter(false, this)
        mBinding.recyclerView.adapter = adapter
        mBinding.recyclerView.setHasFixedSize(true)
        mBinding.recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        (mBinding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    override fun onShowOnMap(address: Address) {
        val intent = Intent(context, MapsActivity::class.java)
        intent.putExtra(Constants.INTENT_MAP_ACTIVITY_ADDRESS, address)
        startActivity(intent)
    }

    override fun onAddressSelected(address: Address?) {

    }

    override fun onEditClicked(address: Address) {
        val intent = Intent(context, AddAddressActivity::class.java)
        intent.putExtra(Constants.INTENT_ADD_ADDRESS_ACTIVITY_ADDRESS, address)
        startActivity(intent)
    }

    override fun onDeleteClicked(address: Address) {
        alert("حذف آدرس", "آدرس پاک خواهد شد ادامه میدهید؟", "حذف", "انصراف", null) {
            viewModel.deleteAddress(address)
        }
    }
}