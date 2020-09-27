package com.amir.ir.privatestore.ui.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.databinding.ActivityAddressBinding
import com.amir.ir.privatestore.models.Address
import com.amir.ir.privatestore.repository.apiservice.ApiService
import com.amir.ir.privatestore.ui.adapters.AddressAdapter
import com.amir.ir.privatestore.ui.dialogs.NoNetworkDialog
import com.amir.ir.privatestore.utils.*
import com.amir.ir.privatestore.viewmodels.AddressActivityViewModel

class AddressActivity : AppCompatActivity(), AddressAdapter.AddressInteractions,
    ApiService.OnUnauthorizedListener, ApiService.InternetConnectionListener {

    private val finishReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Constants.ACTION_ON_PAYMENT_SUCCESS_FINISH_ALL) {
                finish()
            }
        }
    }

    private lateinit var adapter: AddressAdapter
    private lateinit var viewModel: AddressActivityViewModel
    private lateinit var mBinding: ActivityAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)

        viewModel = ViewModelProvider(this).get(AddressActivityViewModel::class.java)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_address)

        initViews()
        subscribeObserves()

        LocalBroadcastManager.getInstance(this).registerReceiver(
            finishReceiver,
            IntentFilter(Constants.ACTION_ON_PAYMENT_SUCCESS_FINISH_ALL)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(finishReceiver)
    }

    private fun subscribeObserves() {
        viewModel.addresses.observe(this, Observer { addresses ->
            if (!addresses.isNullOrEmpty())
                mBinding.animationView.visibility = View.GONE
            else
                mBinding.animationView.visibility = View.VISIBLE
            adapter.submitList(addresses)
        })

        viewModel.deleteAddressEvent.observe(this, EventObserver {
            when (it) {
                "done" -> {
                    toast("حذف شد")
                }
                "" -> {
                    toast(Constants.SERVER_ERROR)
                }
                else -> {
                    toast(it)
                }
            }
        })
    }

    private fun initViews() {
        adapter = AddressAdapter(true, this)
        mBinding.recyclerView.adapter = adapter
        mBinding.recyclerView.setHasFixedSize(true)
        mBinding.recyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        (mBinding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        mBinding.btnAddAddress.setOnClickListener {
            startActivity(Intent(this, AddAddressActivity::class.java))
        }

        mBinding.btnContinue.setOnClickListener {
            if (viewModel.selectedAddress != null) {
                val intent = Intent(this, OrderDetailsActivity::class.java)
                intent.putExtra(
                    Constants.INTENT_ORDER_DETAILS_ACTIVITY_ADDRESS,
                    viewModel.selectedAddress
                )
                startActivity(intent)
            } else {
                toast("آدرس انتخاب نشده است")
            }
        }

        mBinding.ivBack.setOnClickListener { onBackPressed() }
    }

    override fun onShowOnMap(address: Address) {
        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra(Constants.INTENT_MAP_ACTIVITY_ADDRESS, address)
        startActivity(intent)
    }

    override fun onAddressSelected(address: Address?) {
        //TODO in nabayad injuri bashe age address select shode null bashe inja ham bayad null bashe
        //address?.let {
            println("debugx: UI_LEVEL = ${address?.id} -> ${address?.isSelected}")
            viewModel.selectedAddress = address
        //}
    }

    override fun onEditClicked(address: Address) {
        val intent = Intent(this, AddAddressActivity::class.java)
        intent.putExtra(Constants.INTENT_ADD_ADDRESS_ACTIVITY_ADDRESS, address)
        println("debug: onEditClicked: $address")
        startActivity(intent)
    }

    override fun onDeleteClicked(address: Address) {
        alert("حذف آدرس", "آدرس پاک خواهد شد ادامه میدهید؟", "حذف", "انصراف", null) {
            viewModel.deleteAddress(address)
        }
    }

    override fun onUnauthorizedAction(event: Event<Unit>) {
        toast(Constants.UNAUTHORIZED_MSG, true)
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onInternetUnavailable() {
        NoNetworkDialog(this, R.style.my_alert_dialog).show()
    }

}
