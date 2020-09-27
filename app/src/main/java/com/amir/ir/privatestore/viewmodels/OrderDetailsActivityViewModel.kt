package com.amir.ir.privatestore.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.amir.ir.privatestore.models.Address
import com.amir.ir.privatestore.models.CartItem
import com.amir.ir.privatestore.models.DeliveryMethod
import com.amir.ir.privatestore.models.enums.PaymentMethod
import com.amir.ir.privatestore.models.requests.*
import com.amir.ir.privatestore.repository.RemoteRepo
import com.amir.ir.privatestore.repository.persistance.cartdb.CartRepo
import com.amir.ir.privatestore.utils.DoubleTrigger

class OrderDetailsActivityViewModel(application: Application) : AndroidViewModel(application) {

    init {
        CartRepo.setContext(application)
    }

    var taxPrice: Int = 0
    var totalFinalMainPrice: Int = 0
    private var discountId = 0
    var isDiscountCodeExpired =
        false //age vaziyat pardakht suspend bashe (yaani bere to dargah biad birun) server khata mide raje be discountCode, vaghti -1 mikonamesh ke discountCode yek bar sabt shode vali hala error dare
        private set
    private val _deliveryMethod = MutableLiveData<DeliveryMethod>()
    val deliveryMethod: LiveData<DeliveryMethod> = _deliveryMethod

    var isCashPayment: Boolean = false

    var cartId = 0
        private set
    private var paymentLink: String? = null
    private var isPaymentProgressCompeleted =
        true // faghat az tarigh intent mitavan in ra moshakhas kard

    private val paymentResultReceivedViaIntentEvent = MutableLiveData<Boolean>()
    private val checkPaymentEvent = MutableLiveData<Int>()
    val paymentResult: LiveData<CheckPaymentResultResponse?> =
        Transformations.switchMap(
            DoubleTrigger(checkPaymentEvent, paymentResultReceivedViaIntentEvent)
        ) {
            val cartId = it.first
            val isPaymentOk = it.second
            if (isPaymentOk != null && isPaymentProgressCompeleted) {
                println("debugt: from intent")
                MutableLiveData(
                    CheckPaymentResultResponse(
                        isPaymentOk,
                        false,
                        null
                    )
                )
            } else {
                Transformations.map(RemoteRepo.checkPaymentResult(cartId!!)) { checkPaymentResultResponse ->
                    println("debugt: request to server")
                    checkPaymentResultResponse
                }
            }
        }

    private val submitOrderRequestEvent = MutableLiveData<SubmitOrderRequest>()
    val submitOrderResponse: LiveData<SubmitOrderResponse?> =
        Transformations.switchMap(submitOrderRequestEvent) { submitOrderRequest ->
            Transformations.map(RemoteRepo.submitOrder(submitOrderRequest)) { response ->
                if (response != null && !response.error) {  //todo key ino true false konam??
                    isPaymentProgressCompeleted = false
                }
                cartId = response?.cartId ?: 0
                paymentLink = response?.paymentLink
                response
            }
        }

    private val checkDiscountCodeEvent = MutableLiveData<String>()
    val checkDiscountCodeResponse: LiveData<CheckDiscountCodeResponse?> =
        Transformations.switchMap(checkDiscountCodeEvent) { discountCode ->
            Transformations.map(RemoteRepo.checkDiscountCode(discountCode)) { checkDiscountCodeResponse ->
                if (checkDiscountCodeResponse != null) {
                    if (!checkDiscountCodeResponse.error) {
                        discountId = checkDiscountCodeResponse.id
                        isDiscountCodeExpired = false
                    } else {
                        if ((checkDiscountCodeResponse.errorMsg ?: "").contains("دقیقه")) {
                            if (discountId == checkDiscountCodeResponse.id) {
                                isDiscountCodeExpired = true
                            }
                        }
                    }
                }
                checkDiscountCodeResponse
            }
        }


    private val getDeliveryMethodsEvent = MutableLiveData<Int>()
    val getDeliveryMethodsResponse: LiveData<GetDeliveryMethodsResponse?> =
        Transformations.switchMap(getDeliveryMethodsEvent) { addressId ->
            println("debug: GetDeliveryMethodsRequest: $addressId")
            Transformations.map(RemoteRepo.getDeliveryMethods(addressId)) { getDeliveryMethodResponse ->
                println("debug: GetDeliveryMethodsResponse: $getDeliveryMethodResponse")
                if (getDeliveryMethodResponse != null && !getDeliveryMethodResponse.error) {
                    if (!getDeliveryMethodResponse.methods.isNullOrEmpty() && deliveryMethod.value == null) {
                        println("debug: deliveryMethod.value initialed Again")
                        _deliveryMethod.value = getDeliveryMethodResponse.methods[0]
                    }
                }
                getDeliveryMethodResponse
            }
        }


    private val _address: MutableLiveData<Address> = MutableLiveData()
    val address: LiveData<Address> get() = _address

    val cartItems: LiveData<List<CartItem>> by lazy {
        Transformations.map(CartRepo.allCartItem) {
            it.mapNotNull { cartItem ->
                if (cartItem.count > 0) {
                    cartItem
                } else {
                    null
                }
            }
        }
    }

    fun setAddress(address: Address) {
        if (_address.value != address) {
            _address.value = address
            getDeliveryMethods()
        }
    }

    fun getDeliveryMethods() {
        getDeliveryMethodsEvent.value = address.value!!.id
    }

    fun cancelJobs() {
        RemoteRepo.cancelServerJobs() //todo cancel this specific job
        CartRepo.cancelJobs()
    }

    fun checkDiscountCode(discountCode: String) {
        checkDiscountCodeEvent.value = discountCode
    }


    fun submitOrder() {
        val cartItems = cartItems.value!!
        val summeryItems = cartItems.map {
            it.makeSummeryForSubmitOrder()
        }

        submitOrderRequestEvent.value =
            SubmitOrderRequest(
                cartItemSummeryRequest = summeryItems,
                addressId = address.value!!.id,
                paymentMethod = if (isCashPayment) PaymentMethod.Cash.id else PaymentMethod.Online.id,
                sendMethod = _deliveryMethod.value!!.id,
                discountId = if (isDiscountCodeExpired) 0 else discountId
            )
    }

    fun checkPaymentResult(isPaymentOk: Boolean? = null) {  //checkPaymentResult from intent or if not pressed `backToAppBtn` in browser request to server to check it
        if (!isPaymentProgressCompeleted) {
            if (isPaymentOk == null) { // onResume req to server
                if (cartId != 0 && paymentLink != null) {
                    checkPaymentEvent.value = cartId
                }
            } else { //from intent
                isPaymentProgressCompeleted = true
                paymentResultReceivedViaIntentEvent.value = isPaymentOk
            }
        }
    }


    fun setDeliveryMethod(deliveryMethod: DeliveryMethod) {
        _deliveryMethod.value = deliveryMethod
    }

    fun calcPriceAgain() {
        _deliveryMethod.value = _deliveryMethod.value
    }

    fun clearCart() {
        CartRepo.deleteAllCartItem()
    }

    fun expireDiscountCode() {
        isDiscountCodeExpired = true
    }


}