package com.amir.ir.privatestore.ui.fragments.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.models.Order
import com.amir.ir.privatestore.ui.activities.TrackOrderActivity
import com.amir.ir.privatestore.ui.adapters.TrackOrderAdapter
import com.amir.ir.privatestore.utils.Constants
import com.amir.ir.privatestore.utils.putParcelableExtra
import com.amir.ir.privatestore.utils.toast
import com.amir.ir.privatestore.viewmodels.TrackOrdersFragmentViewModel
import kotlinx.android.synthetic.main.layout_track_order_fragment.*

class TrackOrdersFragment : Fragment(), TrackOrderAdapter.Interaction {
    private lateinit var viewModel: TrackOrdersFragmentViewModel
    private lateinit var adapter: TrackOrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(TrackOrdersFragmentViewModel::class.java)
        val view = inflater.inflate(R.layout.layout_track_order_fragment, container, false)

        initViews(view)
        subscribeObservers()

        return view
    }

    private fun subscribeObservers() {
        viewModel.orderList.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (!it.error) {
                    val orders = it.orders
                    if (!orders.isNullOrEmpty())
                        animation_view.visibility = View.GONE
                    else
                        animation_view.visibility = View.VISIBLE
                    adapter.submitList(it.orders)
                } else {
                    toast(it.errorMsg ?: Constants.SERVER_ERROR)
                }
            } else {
                toast(Constants.SERVER_ERROR)
            }
        })
    }

    private fun initViews(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )
        adapter = TrackOrderAdapter(this)
        recyclerView.adapter = adapter
    }

    override fun onMoreDetailsClicked(item: Order) {
        val intent = Intent(context, TrackOrderActivity::class.java)
        intent.putParcelableExtra(Constants.INTENT_TRACK_ORDER_ACTIVITY_ORDER, item)
        startActivity(intent)
        //todo override pending transition
    }
}