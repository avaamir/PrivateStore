package com.amir.ir.privatestore.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.TypedValue
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.databinding.ActivityOrderDetailsBinding
import com.amir.ir.privatestore.models.Address
import com.amir.ir.privatestore.models.DeliveryMethod
import com.amir.ir.privatestore.models.enums.DiscountType
import com.amir.ir.privatestore.repository.apiservice.ApiService
import com.amir.ir.privatestore.ui.adapters.MySimpleSpinnerAdapter
import com.amir.ir.privatestore.ui.adapters.OrderSummeryAdapter
import com.amir.ir.privatestore.ui.animations.bounce
import com.amir.ir.privatestore.ui.bindingadapters.priceToStr
import com.amir.ir.privatestore.ui.dialogs.NoNetworkDialog
import com.amir.ir.privatestore.ui.dialogs.PaymentErrorDialog
import com.amir.ir.privatestore.utils.*
import com.amir.ir.privatestore.viewmodels.OrderDetailsActivityViewModel
import com.google.android.material.snackbar.Snackbar

class OrderDetailsActivity : AppCompatActivity(), ApiService.InternetConnectionListener,
    ApiService.OnUnauthorizedListener {

    private var discountSnack: Snackbar? = null

    private lateinit var viewModel: OrderDetailsActivityViewModel
    private lateinit var mBinding: ActivityOrderDetailsBinding
    private lateinit var mAdapter: OrderSummeryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)

        viewModel = ViewModelProvider(this).get(OrderDetailsActivityViewModel::class.java)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_order_details)
        mBinding.lifecycleOwner = this
        mBinding.viewModel = viewModel

        val address: Address? =
            intent.getParcelableExtra(Constants.INTENT_ORDER_DETAILS_ACTIVITY_ADDRESS)
        if (address != null) // chun tuye onNewIntent intent ro avaz mikonim dar soorat rotate shodan va .. app crash nakone
            viewModel.setAddress(address)

        mBinding.mainProgressBar.visibility = View.VISIBLE
        mBinding.frameMain.visibility = View.INVISIBLE

        initViews()
        subscribeObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkPaymentResult() //check when come back from browser and payment page
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            if (intent.action == Constants.ACTION_PAYMENT_RESULT) {
                val isPaymentOk =
                    intent.extras?.getBoolean(Constants.ACTION_PAYMENT_RESULT_DATA) ?: false
                viewModel.checkPaymentResult(isPaymentOk)
            }
        }
    }

    override fun onBackPressed() {
        viewModel.cancelJobs()
        super.onBackPressed()
    }

    private fun subscribeObservers() {

        viewModel.getDeliveryMethodsResponse.observe(this, Observer { deliveryMethodResponse ->
            if (deliveryMethodResponse != null) {
                if (!deliveryMethodResponse.error) {
                    viewModel.taxPrice = deliveryMethodResponse.taxPrice.toInt()
                    mBinding.spinnerDeliveryType.adapter = MySimpleSpinnerAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        deliveryMethodResponse.methods
                    )
                    mBinding.tvTaxPrice.text = priceToStr(deliveryMethodResponse.taxPrice)
                    mBinding.mainProgressBar.visibility = View.GONE
                    mBinding.frameMain.visibility = View.VISIBLE
                } else {
                    toast(deliveryMethodResponse.errorMsg ?: Constants.SERVER_ERROR, true)
                    finish() //age server vase deliveryMethod khata bede kolan nemitunim edame bedim
                }
            } else {
                mBinding.mainProgressBar.visibility = View.GONE
                snack(Constants.SERVER_ERROR) {
                    mBinding.mainProgressBar.visibility = View.VISIBLE
                    viewModel.getDeliveryMethods()
                }
            }
        })
        //

        viewModel.deliveryMethod.observe(this, Observer { deliveryMethod ->
            if (!deliveryMethod.isBike) {
                mBinding.radioInternetPayment.isChecked = true

                mBinding.frameRadioCash.visibility = View.GONE
                mBinding.tvDeliveryDesc.text = resources.getString(R.string.post_method_desc)
            } else {
                mBinding.frameRadioCash.visibility = View.VISIBLE
                mBinding.tvDeliveryDesc.text = resources.getString(R.string.bike_method_desc)
            }

            var mainPriceSum = 0
            var discountSum = 0
            val cartItems = viewModel.cartItems.value!!
            for (cartItem in cartItems) {
                if (cartItem.hasDiscount) {
                    discountSum += (cartItem.mainPrice.toInt() - cartItem.discountPrice!!.toInt()) * cartItem.count
                }
                mainPriceSum += cartItem.mainPrice.toInt() * cartItem.count
            }

            val totalFinalPrice =
                mainPriceSum - discountSum + deliveryMethod.price.toInt() + viewModel.taxPrice

            viewModel.totalFinalMainPrice = totalFinalPrice

            val discountResponse = viewModel.checkDiscountCodeResponse.value
            if (discountResponse != null) {
                if (
                    totalFinalPrice > discountResponse.minApplicablePrice &&
                    !viewModel.isDiscountCodeExpired &&
                    !discountResponse.error
                ) {
                    when (discountResponse.type) {
                        DiscountType.Amount -> {
                            mBinding.tvTotalFinalPriceGreen.text =
                                (totalFinalPrice - discountResponse.amount).toString()
                            mBinding.tvTotalFinalPriceRed.text = totalFinalPrice.toString()
                        }
                        DiscountType.Percent -> {
                            mBinding.tvTotalFinalPriceGreen.text =
                                (totalFinalPrice * (100 - discountResponse.amount) / 100).toString()
                            mBinding.tvTotalFinalPriceRed.text = totalFinalPrice.toString()
                        }
                        DiscountType.UnDefined -> {
                            toast(
                                "تخفیف در درگاه پرداخت اعمال خواهد شد. لطفا اپلیکیشن را آپدیت کنید.",
                                true
                            )
                            //todo etefagh miofte??
                        }
                    }.exhaustive()
                    mBinding.frameFinalPriceRed.visibility = View.VISIBLE
                    mBinding.tvTotalFinalPriceGreen.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                    mBinding.tvToomanGreen.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                } else {
                    mBinding.frameFinalPriceRed.visibility = View.GONE
                    mBinding.tvTotalFinalPriceGreen.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    mBinding.tvToomanGreen.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    mBinding.tvTotalFinalPriceGreen.text = priceToStr(totalFinalPrice)
                }
            } else {
                mBinding.tvTotalFinalPriceGreen.text = priceToStr(totalFinalPrice)
            }

            mBinding.tvTotalDiscount.text = priceToStr(discountSum)
            mBinding.tvTotalMainPrice.text = priceToStr(mainPriceSum)
            mBinding.tvDeliveryPrice.text = priceToStr(deliveryMethod.price)
            mBinding.tvDeliveryPrice1.text = priceToStr(deliveryMethod.price)
        })

        viewModel.cartItems.observe(this, Observer { cartItems ->
            mAdapter.submitList(cartItems)
        })

        viewModel.checkDiscountCodeResponse.observe(this, Observer { checkDiscountCodeResponse ->
            mBinding.btnCheckDiscountCode.showProgressBar(false)
            if (checkDiscountCodeResponse != null) {
                if (!checkDiscountCodeResponse.error) {
                    if (viewModel.totalFinalMainPrice > checkDiscountCodeResponse.minApplicablePrice) {
                        /*mBinding.btnCheckDiscountCode.visibility = View.GONE
                        mBinding.ivDiscountApplied.visibility = View.VISIBLE*/
                        viewModel.calcPriceAgain()
                        toast("اعمال شد")
                        hideSoftKeyboard()
                        mBinding.frameMain.smoothScrollTo(0, 0)
                        //TODO check this animation
                        bounce(mBinding.frameFinalPrice)
                    } else {
                        viewModel.calcPriceAgain()
                        toast(
                            "این کد تخفیف برای خریدهای بالاتر از ${checkDiscountCodeResponse.minApplicablePrice} قابل اعمال است",
                            true
                        )
                    }
                } else {
                    viewModel.calcPriceAgain()
                    toast(checkDiscountCodeResponse.errorMsg ?: Constants.SERVER_ERROR, true)
                }
            } else {
                discountSnack = snack(Constants.SERVER_ERROR) {
                    requestCheckDiscount()
                }
            }
        })


        //
        viewModel.submitOrderResponse.observe(this, Observer { submitOrderResponse ->
            if (submitOrderResponse != null) {
                mBinding.btnPayment.setOnClickListener { prepareSubmitOrderRequest() }
                if (!submitOrderResponse.error) {
                    if (submitOrderResponse.paymentLink.isNullOrBlank()) { //yaani bayad in halat bashe cash+bike bude -> check mokonim hast ya na(dar soorat khataye server)
                        lockUiOnRequest(false)
                        if (viewModel.deliveryMethod.value!!.isBike && viewModel.isCashPayment) { //bike+cash
                            startActivity(
                                Intent(
                                    this,
                                    DeliveryPreparationActivity::class.java
                                )
                            ) //todo aya chizi niaze be intent pass bedam??
                            finishAll()
                        } else { //halat gheyre momken -> khataye server -> hatman bayad link midade -> faghat dar halt bike+cash null mishe
                            toast(Constants.SERVER_ERROR)
                        }
                    } else {
                        lockUiOnRequest(true)
                        val browserIntent =
                            Intent(Intent.ACTION_VIEW, Uri.parse(submitOrderResponse.paymentLink))
                        browserIntent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                        startActivity(browserIntent)
                    }
                } else {
                    lockUiOnRequest(false)
                    val errorMsg = submitOrderResponse.errorMsg ?: Constants.SERVER_ERROR
                    if (errorMsg.contains("دقیقه")) {
                        PaymentErrorDialog(
                            this,
                            R.style.my_alert_dialog,
                            errorMsg
                        ) { shouldContinuePayment, dialog ->
                            if (shouldContinuePayment) {
                                viewModel.expireDiscountCode()
                                viewModel.calcPriceAgain()
                                prepareSubmitOrderRequest()
                            }
                            dialog.dismiss()
                        }.show()
                    } else {
                        toast(errorMsg, true)
                    }
                }
            } else {
                discountSnack?.let {
                    if (it.isShown)
                        it.dismiss()
                }
                lockUiOnRequest(false)
                snack(Constants.SERVER_ERROR) { prepareSubmitOrderRequest() }
            }
        })

        viewModel.paymentResult.observe(this, Observer { paymentResult ->
            println("debug: $paymentResult")
            if (paymentResult != null) {
                if (!paymentResult.error) {
                    if (paymentResult.isPaymentOk) {
                        if (viewModel.deliveryMethod.value!!.isBike) { //if bike
                            val intent = Intent(
                                this,
                                DeliveryPreparationActivity::class.java
                            ) //todo aya chizi mikhad behesh pas bedam?
                            startActivity(intent)
                        } else {
                            toast("پرداخت شد")
                            val intent = Intent(this, TrackOrderActivity::class.java)
                            intent.putExtra(
                                Constants.INTENT_TRACK_ORDER_ACTIVITY_ORDER,
                                viewModel.cartId
                            )
                            startActivity(intent)
                        }
                        finishAll()
                    } else {
                        //TODO show a fancy message
                        toast("پرداخت ناموفق")
                    }
                } else {
                    toast(paymentResult.errorMsg ?: Constants.SERVER_ERROR, true)
                }
            } else {
                discountSnack?.let {
                    if (it.isShown)
                        it.dismiss()
                }
                lockUiOnRequest(false)
                snack(Constants.SERVER_ERROR) {
                    viewModel.checkPaymentResult()
                }
            }
            lockUiOnRequest(false)
        })
    }

    private fun initViews() {
        mBinding.spinnerDeliveryType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val deliveryMethod = parent?.selectedItem as DeliveryMethod
                    viewModel.setDeliveryMethod(deliveryMethod)
                }
            }

        mBinding.radioCashPayment.setOnCheckedChangeListener { _, isChecked ->
            mBinding.radioInternetPayment.isChecked = !isChecked
            viewModel.isCashPayment = isChecked
        }
        mBinding.radioInternetPayment.setOnCheckedChangeListener { _, isChecked ->
            mBinding.radioCashPayment.isChecked = !isChecked
        }

        mBinding.radioCashPayment.isChecked = viewModel.isCashPayment

        mAdapter = OrderSummeryAdapter()
        mBinding.recyclerViewOrderSummery.adapter = mAdapter
        mBinding.recyclerViewOrderSummery.layoutManager = LinearLayoutManager(
            this,
            RecyclerView.HORIZONTAL,
            true
        )
        mBinding.recyclerViewOrderSummery.setHasFixedSize(true)
        mBinding.switchDiscount.setOnCheckedChangeListener { _, isChecked ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(
                    mBinding.cardFrameDiscountCode,
                    AutoTransition()
                )
            }
            if (isChecked) {
                mBinding.frameDiscount.visibility = View.VISIBLE
            } else {
                mBinding.frameDiscount.visibility = View.GONE
            }
        }
        mBinding.ivBack.setOnClickListener { onBackPressed() }

        mBinding.btnPayment.setOnClickListener {
            prepareSubmitOrderRequest()
        }

        mBinding.btnCheckDiscountCode.setOnClickListener {
            requestCheckDiscount()
        }

        //age in nabashe nested az top shoru nemishe // kiram tu android :|
        mBinding.recyclerViewOrderSummery.isFocusable = false
        mBinding.frameScrollView.requestFocus()
    }

    private fun requestCheckDiscount() {
        val discountCode = mBinding.etDiscount.text.toString()
        if (discountCode.isNotBlank()) {
            mBinding.btnCheckDiscountCode.showProgressBar(true)
            viewModel.checkDiscountCode(discountCode)
        } else {
            toast("ابتدا کد تخفیف را وارد کنید")
        }
    }

    private fun prepareSubmitOrderRequest() {
        if (viewModel.isCashPayment && !viewModel.deliveryMethod.value!!.isBike) {
            toast("پرداخت نقدی فقط در صورت خرید با پیک فعال میباشد", true)
            return
        }

        lockUiOnRequest(true)
        mBinding.btnPayment.setOnClickListener(null)
        viewModel.submitOrder()
    }

    private fun lockUiOnRequest(shouldLock: Boolean) {
        mBinding.btnPayment.showProgressBar(shouldLock)
        mBinding.radioCashPayment.isEnabled = !shouldLock
        mBinding.radioInternetPayment.isEnabled = !shouldLock
        mBinding.switchDiscount.isEnabled = !shouldLock
    }

    private fun finishAll() {
        viewModel.clearCart()
        LocalBroadcastManager.getInstance(this)
            .sendBroadcast(Intent(Constants.ACTION_ON_PAYMENT_SUCCESS_FINISH_ALL))
        finish()
    }

    override fun onInternetUnavailable() {
        NoNetworkDialog(this, R.style.my_alert_dialog).show()
    }

    override fun onUnauthorizedAction(event: Event<Unit>) {
        toast(Constants.UNAUTHORIZED_MSG)
        startActivity(Intent(this, LoginActivity::class.java))
    }

}
