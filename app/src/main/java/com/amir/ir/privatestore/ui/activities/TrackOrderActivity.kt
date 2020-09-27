package com.amir.ir.privatestore.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.databinding.ActivityTrackOrderBinding
import com.amir.ir.privatestore.models.Order
import com.amir.ir.privatestore.repository.apiservice.ApiService
import com.amir.ir.privatestore.ui.adapters.OrderSummeryAdapter
import com.amir.ir.privatestore.ui.dialogs.NoNetworkDialog
import com.amir.ir.privatestore.utils.Constants
import com.amir.ir.privatestore.utils.Event
import com.amir.ir.privatestore.utils.snack
import com.amir.ir.privatestore.utils.toast
import com.amir.ir.privatestore.viewmodels.TrackOrderActivityViewModel

class TrackOrderActivity : AppCompatActivity(), ApiService.InternetConnectionListener, ApiService.OnUnauthorizedListener {


    private lateinit var viewModel: TrackOrderActivityViewModel
    private lateinit var mBinding: ActivityTrackOrderBinding
    private lateinit var mAdapter: OrderSummeryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_order)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_track_order)
        viewModel = ViewModelProvider(this).get(TrackOrderActivityViewModel::class.java)
        mBinding.lifecycleOwner = this
        mBinding.viewModel = viewModel

        val order = intent.getParcelableExtra<Order>(Constants.INTENT_TRACK_ORDER_ACTIVITY_ORDER)
        viewModel.setOrder(order)

        mBinding.mainProgressBar.visibility = View.VISIBLE
        mBinding.frameMain.visibility = View.INVISIBLE

        initViews()
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.orderPageResponse.observe(this, Observer {
            if (it == null) { //Server Error
                snack(Constants.SERVER_ERROR) {
                    mBinding.mainProgressBar.visibility = View.VISIBLE
                    mBinding.frameMain.visibility = View.INVISIBLE
                    viewModel.tryAgain()
                }
            } else { //Data is Ready
                mBinding.mainProgressBar.visibility = View.GONE
                mBinding.frameMain.visibility = View.VISIBLE
                mAdapter.submitList(it.ordersListItem)
                mBinding.tvPaymentStatus.setTextColor(
                    if (it.paymentStatus == "پرداخت نشده")
                        ContextCompat.getColor(this, R.color.deep_red)
                    else
                        ContextCompat.getColor(this, R.color.green)
                )
            }
        })
    }

    private fun initViews() {
        mAdapter = OrderSummeryAdapter()
        mBinding.recyclerViewOrderSummery.adapter = mAdapter
        mBinding.recyclerViewOrderSummery.layoutManager = LinearLayoutManager(
            this,
            RecyclerView.HORIZONTAL,
            true
        )

        mBinding.ivBack.setOnClickListener {
            onBackPressed()
        }

    }

    override fun onInternetUnavailable() {
        NoNetworkDialog(this, R.style.my_alert_dialog).show()
    }

    override fun onUnauthorizedAction(event: Event<Unit>) {
        toast(Constants.UNAUTHORIZED_MSG)
        startActivity(Intent(this, LoginActivity::class.java))
    }

}
