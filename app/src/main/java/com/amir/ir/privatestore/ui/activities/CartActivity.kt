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
import com.amir.ir.privatestore.UserConfigs
import com.amir.ir.privatestore.databinding.ActivityCartBinding
import com.amir.ir.privatestore.models.CartItem
import com.amir.ir.privatestore.models.Product
import com.amir.ir.privatestore.repository.apiservice.ApiService
import com.amir.ir.privatestore.ui.adapters.CartAdapter
import com.amir.ir.privatestore.ui.bindingadapters.priceToStr
import com.amir.ir.privatestore.ui.dialogs.NoNetworkDialog
import com.amir.ir.privatestore.utils.*
import com.amir.ir.privatestore.viewmodels.CartActivityViewModel

class CartActivity : AppCompatActivity(), CartAdapter.Interaction, ApiService.InternetConnectionListener {

    private val finishReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Constants.ACTION_ON_PAYMENT_SUCCESS_FINISH_ALL) {
                finish()
            }
        }
    }

    private lateinit var mBinding: ActivityCartBinding
    private lateinit var viewModel: CartActivityViewModel

    private lateinit var mAdapter: CartAdapter


    private var isFirstTime = true
    private var product: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        viewModel = ViewModelProvider(this).get(CartActivityViewModel::class.java)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_cart)

        println("debug: fuck me " + intent.getParcelableExtra(Constants.INTENT_CART_ACTIVITY_PRODUCT))
        product = intent.getParcelableExtra(Constants.INTENT_CART_ACTIVITY_PRODUCT)

        initViews()
        subscribeObservers()

        LocalBroadcastManager.getInstance(this).registerReceiver(
            finishReceiver,
            IntentFilter(Constants.ACTION_ON_PAYMENT_SUCCESS_FINISH_ALL)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(finishReceiver)
    }

    override fun onBackPressed() {
        viewModel.cancelJobs()
        super.onBackPressed()
    }

    private fun initViews() {
        mAdapter = CartAdapter(this)
        mBinding.recyclerView.adapter = mAdapter
        mBinding.recyclerView.layoutManager = LinearLayoutManager(
            this,
            RecyclerView.VERTICAL,
            false
        )
        (mBinding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        mBinding.ivBack.setOnClickListener { this.onBackPressed() }
        mBinding.frameClearCart.setOnClickListener {
            if ((viewModel.cartItems.value?.size ?: 0) > 0) {
                alert(
                    "سبد خرید",
                    "سبد خرید شما خالی خواهد شد ادامه میدهید؟",
                    "بله",
                    "خیر",
                    null
                ) {
                    viewModel.clearCart()
                }
            }
        }
        mBinding.btnContinue.setOnClickListener {
            if (!viewModel.isCartEmpty) {
                if (UserConfigs.isLoggedIn) {
                    if (viewModel.checkCartQuantity()) {
                        startActivity(Intent(this, AddressActivity::class.java))
                    } else {
                        mBinding.btnContinue.showProgressBar(true)
                    }
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            } else {
                toast("سبد شما خالیست")
            }
        }
    }

    private fun subscribeObservers() {
        viewModel.checkCartItemsResponseMessage.observe(this, Observer {
            mBinding.btnContinue.showProgressBar(false)
            if (it != null) {
                if (it.isEmpty()) { //chizi update nashod
                    startActivity(Intent(this, AddressActivity::class.java))
                } else { //list update shod
                    toast(it, true)
                }
            } else {
                snack(Constants.SERVER_ERROR) {
                    viewModel.checkCartQuantity()
                }
            }
        })


        viewModel.cartItems.observe(this, Observer { cartItems ->
            if (cartItems.isEmpty()) {
                mBinding.tvTotalFinalAllItemsPrice.text = "0"
                mAdapter.submitList(cartItems)
                mBinding.frameEmptyCart.visibility = View.VISIBLE
            } else {
                mBinding.frameEmptyCart.visibility = View.GONE
                mAdapter.submitList(cartItems)
                var sum = 0
                for (cartItem in cartItems) {
                    sum += if (cartItem.hasDiscount) {
                        cartItem.count * cartItem.discountPrice!!.toInt()
                    } else {
                        cartItem.count * cartItem.mainPrice.toInt()
                    }
                }
                mBinding.tvTotalFinalAllItemsPrice.text = priceToStr(sum)
            }

            if (isFirstTime) { //age chizi be sabad ezafe shod scroll mikone be positionesh
                println("debug: cartItems on FirstRun: ${cartItems.size}")
                isFirstTime = false
                product?.let { product ->
                    val position = cartItems.indexOf(cartItems.find { cartItem ->
                        cartItem.pid == product.pid
                    })
                    mBinding.recyclerView.scrollToPosition(position)
                }
            }
        })


        viewModel.uiMessages.observe(this, EventObserver {
            toast(it)
        })
    }

    override fun onDeleteClicked(item: CartItem) {
        alert(
            "حذف از سبد",
            "محصول از سبد شما حذف خواهد شد. ادامه میدهید؟",
            "بله، حذف شود",
            "خیر"
        ) {
            viewModel.delete(item)
        }
    }

    override fun onDecreaseClicked(item: CartItem) {
        viewModel.decrease(item)
    }

    override fun onIncreaseClicked(item: CartItem) {
        viewModel.increase(item)
    }

    override fun onProductImageClicked(item: CartItem) {
        val intent = Intent(this, ProductDetailsActivity::class.java)
        intent.putExtra(Constants.INTENT_PRODUCT_DETAILS_ACTIVITY_PID, item.pid)
        startActivity(intent)
    }

    override fun onInternetUnavailable() {
        NoNetworkDialog(this, R.style.my_alert_dialog).show()
    }
}
