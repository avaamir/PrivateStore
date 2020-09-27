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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.databinding.LayoutBookmarkFragmentBinding
import com.amir.ir.privatestore.models.Favorite
import com.amir.ir.privatestore.ui.activities.ProductDetailsActivity
import com.amir.ir.privatestore.ui.adapters.FavoriteAdapter
import com.amir.ir.privatestore.utils.Constants
import com.amir.ir.privatestore.utils.EventObserver
import com.amir.ir.privatestore.utils.toast
import com.amir.ir.privatestore.viewmodels.FavoriteFragmentViewModel

class FavoriteFragment : Fragment(), FavoriteAdapter.Interaction {

    private lateinit var viewModel: FavoriteFragmentViewModel
    private lateinit var mBinding: LayoutBookmarkFragmentBinding
    private lateinit var mAdapter: FavoriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.layout_bookmark_fragment, container, false)
        viewModel = ViewModelProvider(this).get(FavoriteFragmentViewModel::class.java)

        initViews()
        subscribeObservers()

        return mBinding.root
    }

    private fun subscribeObservers() {
        viewModel.getFavoriteServerResponseMessage.observe(viewLifecycleOwner, EventObserver {
            toast(if (it == "") Constants.SERVER_ERROR else it)
        })

        viewModel.favoriteDeleteResponse.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (it.error) {
                    toast(it.errorMsg ?: Constants.SERVER_ERROR)
                }
            } else {
                toast(Constants.SERVER_ERROR)
            }
        })

        viewModel.taggedProducts.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty())
                mBinding.animationView.visibility = View.GONE
            else
                mBinding.animationView.visibility = View.VISIBLE
            mAdapter.submitList(it)
        })
    }

    private fun initViews() {
        mBinding.recyclerView.layoutManager =
            GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
        mAdapter = FavoriteAdapter(this)
        mBinding.recyclerView.adapter = mAdapter
    }

    override fun onItemClicked(item: Favorite) {
        val intent = Intent(context, ProductDetailsActivity::class.java)
        intent.putExtra(Constants.INTENT_PRODUCT_DETAILS_ACTIVITY_PID, item.pid)
        startActivity(intent)
    }

    override fun onDeleteClicked(item: Favorite) {
        viewModel.delete(item)
    }
}