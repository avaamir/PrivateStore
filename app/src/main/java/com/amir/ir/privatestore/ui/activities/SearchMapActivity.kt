package com.amir.ir.privatestore.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.databinding.ActivitySearchMapBinding
import com.amir.ir.privatestore.models.requests.SearchAddressItem
import com.amir.ir.privatestore.repository.apiservice.ApiService
import com.amir.ir.privatestore.repository.apiservice.MapService
import com.amir.ir.privatestore.ui.adapters.SearchAddressAdapter
import com.amir.ir.privatestore.ui.dialogs.NoNetworkDialog
import com.amir.ir.privatestore.utils.Constants
import com.amir.ir.privatestore.utils.OptimizedSearchTextWatcher
import com.amir.ir.privatestore.utils.putParcelableExtra
import com.amir.ir.privatestore.viewmodels.SearchMapActivityViewModel
import com.mapbox.mapboxsdk.geometry.LatLng

class SearchMapActivity : AppCompatActivity(), SearchAddressAdapter.Interaction,
    ApiService.InternetConnectionListener {

    private lateinit var viewModel: SearchMapActivityViewModel
    private lateinit var mBinding: ActivitySearchMapBinding
    private lateinit var mAdapter: SearchAddressAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        //setTheme(R.style.theme_activity_search_address)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_map)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_map)
        viewModel = ViewModelProvider(this).get(SearchMapActivityViewModel::class.java)

        val latLng: LatLng? =
            intent.getParcelableExtra(Constants.INTENT_SEARCH_MAP_ACTIVITY_LAT_LNG)

        viewModel.latLng = latLng

        initViews()
        subscribeObservers()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.nothing, R.anim.activity_finish_exit_bottom)
    }

    override fun onResume() {
        super.onResume()
        MapService.setInternetConnectionListener(this)
    }

    override fun onPause() {
        super.onPause()
        MapService.removeInternetConnectionListener(this)
    }

    private fun subscribeObservers() {
        viewModel.searchResponse.observe(this, Observer {
            mBinding.progressBar.visibility = View.GONE
            if (it != null) {
                if (!it.hasError) {
                    if (it.items.isNullOrEmpty()) {
                        mBinding.recyclerSearchAddress.visibility = View.GONE
                        mBinding.animationView.visibility = View.VISIBLE
                    } else {
                        mBinding.recyclerSearchAddress.visibility = View.VISIBLE
                        mBinding.animationView.visibility = View.GONE
                    }
                    mAdapter.submitList(it.items)
                } else {
                    mBinding.animationView.visibility = View.VISIBLE
                }
            } else {
                mBinding.animationView.visibility = View.VISIBLE
                //TODO what should i do, natije ee yaft nashod??
            }
        })
    }

    private fun initViews() {
        mAdapter = SearchAddressAdapter(this)
        mBinding.recyclerSearchAddress.adapter = mAdapter
        mBinding.recyclerSearchAddress.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        mBinding.etSearch.addTextChangedListener(object : OptimizedSearchTextWatcher() {
            override fun itsSearchTime(s: Editable) {
                if (s.length > 2) {
                    viewModel.search(s.toString())
                    mBinding.animationView.visibility = View.GONE
                    mBinding.recyclerSearchAddress.visibility = View.GONE
                    mBinding.progressBar.visibility = View.VISIBLE
                }
            }

        })

        mBinding.ivClose.setOnClickListener { mBinding.etSearch.text.clear() }


        mBinding.etSearch.requestFocus()

    }

    override fun onItemClicked(item: SearchAddressItem) {
        setResult(
            Activity.RESULT_OK,
            Intent().apply {
                putParcelableExtra(Constants.INTENT_SEARCH_MAP_ACTIVITY_RESULT_ADDRESS, item)
            }
        )
        finish()
        overridePendingTransition(R.anim.nothing, R.anim.activity_finish_exit_bottom)
    }

    override fun onInternetUnavailable() {
        NoNetworkDialog(this, R.style.my_alert_dialog).show()
    }


}
