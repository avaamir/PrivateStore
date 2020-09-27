package com.amir.ir.privatestore.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.databinding.ActivityProductListBinding
import com.amir.ir.privatestore.models.Category
import com.amir.ir.privatestore.models.Product
import com.amir.ir.privatestore.models.enums.Field
import com.amir.ir.privatestore.models.enums.ListOrder
import com.amir.ir.privatestore.repository.apiservice.ApiService
import com.amir.ir.privatestore.ui.adapters.ProductGridAdapter
import com.amir.ir.privatestore.ui.adapters.ProductVerticalAdapter
import com.amir.ir.privatestore.ui.animations.closeReveal
import com.amir.ir.privatestore.ui.animations.crossfade
import com.amir.ir.privatestore.ui.animations.startReveal
import com.amir.ir.privatestore.ui.dialogs.NoNetworkDialog
import com.amir.ir.privatestore.ui.dialogs.SortDialog
import com.amir.ir.privatestore.ui.listeners.ProductInteraction
import com.amir.ir.privatestore.utils.*
import com.amir.ir.privatestore.viewmodels.ProductListActivityViewModel
import com.google.android.material.snackbar.Snackbar

class ProductListActivity : AppCompatActivity(), ProductInteraction,
    ApiService.InternetConnectionListener {

    private var snackbar: Snackbar? = null

    private lateinit var mGridAdapter: ProductGridAdapter
    private lateinit var mVerticalAdapter: ProductVerticalAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var gridLayoutManager: GridLayoutManager

    private lateinit var viewModel: ProductListActivityViewModel
    private lateinit var mBinding: ActivityProductListBinding

    private var isSearchShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        viewModel = ViewModelProvider(this).get(ProductListActivityViewModel::class.java)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_product_list)
        mBinding.viewModel = viewModel
        mBinding.lifecycleOwner = this

        val category =
            intent.getParcelableExtra<Category?>(Constants.INTENT_PRODUCT_LIST_ACTIVITY_CAT)
        val field: Field = intent.getParcelableExtra(Constants.INTENT_PRODUCT_LIST_ACTIVITY_FIELD)
        val listOrder: ListOrder? =
            intent.getParcelableExtra(Constants.INTENT_PRODUCT_LIST_ACTIVITY_ORDER_ID)

        val searchKey: String? =
            intent.getStringExtra(Constants.INTENT_PRODUCT_LIST_ACTIVITY_SEARCH_KEY)
        viewModel.searchKey = searchKey

        viewModel.setFieldAndCategory(
            field,
            category,
            listOrder ?: ListOrder.MOST_SELL
        )

        initViews()
        subscribeObservers()
    }


    override fun onBackPressed() {
        if (isSearchShown) {
            closeSearchBar()
            isSearchShown = false
        } else {
            viewModel.cancelJobs()
            super.onBackPressed()
        }
    }

    private fun subscribeObservers() {
        viewModel.serverErrorMsg.observe(this, Observer {
            val msg = it.peekContent()
            crossfade(null, mBinding.frameProgress)
            if (!msg.isBlank()) {
                toast(msg)
            } else {
                snackbar.let { _snack ->
                    if (_snack == null || !_snack.isShown) {
                        snackbar = snack(Constants.SERVER_ERROR, {
                            mBinding.frameProgress.visibility = View.VISIBLE
                            viewModel.nextPage()
                        })
                    }
                }
            }
        })

        viewModel.isAllPageLoaded.observe(this, EventObserver { isAllPageLoaded ->
            if (isAllPageLoaded) {
                crossfade(null, mBinding.frameProgress)
                if (viewModel.isListEmpty) {
                    mVerticalAdapter.submitList(ArrayList())
                    mGridAdapter.submitList(ArrayList())
                    crossfade(mBinding.animationView, mBinding.recyclerProducts, mBinding.frameProgress)
                } else {
                    toast(Constants.MSG_LIST_COMPLETED)
                    if (mBinding.animationView.visibility == View.VISIBLE) {
                        crossfade(mBinding.animationView, mBinding.recyclerProducts, mBinding.frameProgress)
                    }
                }
                snackbar.let {
                    if (it != null && it.isShown) {
                        snackbar?.dismiss()
                    }
                }
            }
        })

        viewModel.products.observe(this, Observer { products ->
            mVerticalAdapter.submitList(products)
            mGridAdapter.submitList(products)

            if (viewModel.isListEmpty) {
                crossfade(mBinding.animationView, mBinding.frameProgress, mBinding.recyclerProducts)
            } else {
                crossfade(mBinding.recyclerProducts, mBinding.animationView, mBinding.frameProgress)
            }
            snackbar.let {
                if (it != null && it.isShown) {
                    snackbar?.dismiss()
                }
            }
        })

        viewModel.onSortOrderChangedEvent.observe(
            this,
            EventObserver { listOrder ->  //vaghti sort avaz mishe
                mBinding.frameProgress.visibility = View.VISIBLE
                snackbar.let {
                    if (it != null && it.isShown) {
                        snackbar?.dismiss()
                    }
                }
                mGridAdapter.submitList(ArrayList())
                mVerticalAdapter.submitList(ArrayList())
                mBinding.tvCurrentArrangement.text = getOrderTitle(listOrder)
            })
    }

    private fun initViews() {
        if (viewModel.isFirstTime) {
            mBinding.frameProgress.visibility = View.VISIBLE
            mBinding.recyclerProducts.visibility = View.INVISIBLE
        }

        if (viewModel.searchKey.isNullOrBlank()) {
            mBinding.ivSearch.visibility = View.GONE
        }

        //init LayoutManger
        linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        gridLayoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)

        //init Adapters
        mVerticalAdapter = ProductVerticalAdapter(this)
        mGridAdapter = ProductGridAdapter(this)

        mBinding.recyclerProducts.setHasFixedSize(true)
        //set Default Recycler View state to Vertical
        if (!viewModel.isRecyclerGrid) {
            mBinding.recyclerProducts.layoutManager = linearLayoutManager
            mBinding.recyclerProducts.adapter = mVerticalAdapter
        } else {
            mBinding.recyclerProducts.layoutManager = gridLayoutManager
            mBinding.recyclerProducts.adapter = mGridAdapter
        }

        mBinding.ivBack.setOnClickListener {
            viewModel.cancelJobs()
            onBackPressed()
        }

        mBinding.ivSearch.setOnClickListener {
            toast("not implemented")
        }

        mBinding.ivCart.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    CartActivity::class.java
                )
            )
        }

        mBinding.frameSort.setOnClickListener {
            SortDialog(
                this,
                R.style.my_dialog_animation,
                viewModel.listOrder,
                viewModel.isJustInStock
            ) { listOrder, isJustInStock ->
                viewModel.setListOrderAndJustInStock(listOrder, isJustInStock)
            }.apply {
                if (viewModel.field == Field.MOST_SELL || viewModel.field == Field.NEW) {
                    donNotShowNewestField()
                }
            }.show()
        }

        mBinding.frameFilter.setOnClickListener {
            toast("not implemented")
        }


        mBinding.frameViewType.setOnClickListener {
            if (viewModel.isRecyclerGrid) {
                mBinding.recyclerProducts.adapter = mVerticalAdapter
                mBinding.recyclerProducts.layoutManager = linearLayoutManager
                mBinding.ivChangeViewType.setImageResource(R.drawable.ic_view_grid)
                viewModel.isRecyclerGrid = false
            } else {
                mBinding.recyclerProducts.adapter = mGridAdapter
                mBinding.recyclerProducts.layoutManager = gridLayoutManager
                mBinding.ivChangeViewType.setImageResource(R.drawable.ic_view_agenda)
                viewModel.isRecyclerGrid = true
            }
        }



        mBinding.ivSearch.setOnClickListener {
            isSearchShown = true
            mBinding.frameToolbarButtons.visibility = View.INVISIBLE
            mBinding.frameSearch.visibility = View.VISIBLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startReveal(mBinding.frameSearch) {
                    if (mBinding.etSearch.requestFocus()) {
                        showSoftKeyboard(mBinding.etSearch)
                    }
                }
            } else {
                if (mBinding.etSearch.requestFocus()) {
                    showSoftKeyboard(mBinding.etSearch)
                }
            }
        }

        mBinding.ivClose.setOnClickListener {  //TODO closeReveal kar nemikonad??
            closeSearchBar()
        }

        mBinding.etSearch.setOnEditorActionListener { _, actionId, event ->
            if (
                actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                event.action == KeyEvent.ACTION_DOWN ||
                event.action == KeyEvent.KEYCODE_ENTER
            ) {
                val searchKey = mBinding.etSearch.text.toString().trim()
                when {
                    searchKey.isBlank() -> {
                        toast(getString(R.string.error_search_empty))
                    }
                    searchKey.length < 3 -> {
                        toast(getString(R.string.error_search_length))
                    }
                    else -> {
                        viewModel.searchKey = searchKey
                        viewModel.resetList()
                        mBinding.frameProgress.visibility = View.VISIBLE
                    }
                }
            }
            false
        }

    }


    private fun closeSearchBar() {
        isSearchShown = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            closeReveal(mBinding.frameSearch) {
                mBinding.frameSearch.visibility = View.INVISIBLE
            }
        } else {
            mBinding.frameSearch.visibility = View.INVISIBLE
        }
        mBinding.frameToolbarButtons.visibility = View.VISIBLE
        hideSoftKeyboard()
        mBinding.etSearch.text.clear()
    }

    private fun getOrderTitle(listOrder: ListOrder): String {
        return when (listOrder) {
            ListOrder.UN_DEFINED -> throw Throwable("can not be un-defined at this point")
            ListOrder.PRICE_ASCENDING -> "قیمت از کم به زیاد"
            ListOrder.PRICE_DESCENDING -> "قیمت از زیاد به کم"
            ListOrder.NEW -> "جدیدترین"
            ListOrder.MOST_SELL -> "پرفورش ترین"
        }

    }

    override fun onListTop() {
        viewModel.prevPage()
    }

    override fun onListEnd() {
        if (!viewModel.isAllPageLoadedVal) {
            viewModel.nextPage()
            mBinding.frameProgress.visibility = View.VISIBLE
            snackbar.let {
                if (it != null && it.isShown) {
                    snackbar?.dismiss()
                }
            }
        }
    }

    override fun onItemClicked(item: Product) {
        val intent = Intent(this, ProductDetailsActivity::class.java)
        intent.putExtra(Constants.INTENT_PRODUCT_DETAILS_ACTIVITY_PRODUCT, item)
        startActivity(intent)
    }

    override fun onInternetUnavailable() {
        NoNetworkDialog(this, R.style.my_alert_dialog).show()
    }

}
