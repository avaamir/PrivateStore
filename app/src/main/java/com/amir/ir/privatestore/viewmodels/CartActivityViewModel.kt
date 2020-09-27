package com.amir.ir.privatestore.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.amir.ir.privatestore.models.CartItem
import com.amir.ir.privatestore.models.requests.CartItemSummeryResponse
import com.amir.ir.privatestore.models.requests.CheckCartQuantityRequest
import com.amir.ir.privatestore.repository.RemoteRepo
import com.amir.ir.privatestore.repository.persistance.cartdb.CartRepo
import com.amir.ir.privatestore.repository.sharedprefrence.PrefsRepo
import com.amir.ir.privatestore.utils.Event


class CartActivityViewModel(application: Application) : AndroidViewModel(application) {

    init {
        CartRepo.setContext(application)
        PrefsRepo.setContext(application)
    }

    private val checkCartQuantityEvent = MutableLiveData<CheckCartQuantityRequest>()
    val checkCartItemsResponseMessage: LiveData<String?> =
        Transformations.switchMap(checkCartQuantityEvent) { cartQuantityRequest ->
            Transformations.map(RemoteRepo.checkCartQuantity(cartQuantityRequest)) { response ->
                if (response != null) {
                    PrefsRepo.cartQuantityChecked()
                    if (!response.error) {
                        val cartItems = updateCartListMatchWithSummery(response.cartSummeryItems)
                        if (cartItems.isEmpty()) {
                            ""  //don't need to show a message
                        } else {
                            for (cartItem in cartItems) {
                                CartRepo.update(cartItem)
                            }
                            "موجودی یا قیمت تعدادی از اجناس تغییر کرده است. لطفا سبد خود را دوباره بررسی کنید و ادامه دهید" //update message
                        }
                    } else {
                        response.errorMsg //server has an error show to user
                    }
                } else {
                    null //server error
                }
            }
        }

    private fun updateCartListMatchWithSummery(summeryItems: ArrayList<CartItemSummeryResponse>): List<CartItem> {
        val needToUpdateCartItemList = ArrayList<CartItem>()
        cartItemsList?.forEach { cartItem ->
            var position = 0
            summeryItems.forEachIndexed { index, summeryItem ->
                if (
                    cartItem.pid == summeryItem.pid
                    && cartItem.sizeId == summeryItem.sizeId
                    && cartItem.colorId == summeryItem.colorId
                ) { //identify and match items
                    if (
                        cartItem.mainPrice != summeryItem.mainPrice
                        || cartItem.discountPrice != summeryItem.discountPrice
                        || cartItem.count > summeryItem.quantityLimit
                    ) { //check if need to update add to udpate list
                        println("debugcc: original -> $cartItem")
                        println("debugcc:   new    -> $summeryItem")
                        needToUpdateCartItemList.add(
                            cartItem.copy(
                                mainPrice = summeryItem.mainPrice,
                                discountPrice = summeryItem.discountPrice,
                                count = if (summeryItem.quantityLimit > cartItem.count) cartItem.count else summeryItem.quantityLimit
                            )
                        )
                    }
                    position = index //age in kar ro nakonim concurrent modification exception mide
                    return@forEachIndexed
                }
            }
            summeryItems.removeAt(position) //to iterate faster in next loop
        }
        return needToUpdateCartItemList
    }

    //get() = _checkCartItemsResponse

    private val _uiEvents = MutableLiveData<Event<String>>()
    val uiMessages get() = _uiEvents

    private val cartItemsList: List<CartItem>? get() = cartItems.value

    val cartItems by lazy {
        CartRepo.allCartItem
    }

    val isCartEmpty: Boolean
        get() = cartItemsList.isNullOrEmpty()

    fun clearCart() {
        CartRepo.deleteAllCartItem()
    }


    fun delete(cartItem: CartItem) {
        CartRepo.delete(cartItem)
    }

    fun increase(cartItem: CartItem) {
        val count = cartItem.count
        if (count < cartItem.totalCount) {
            CartRepo.insert(cartItem.copy(count = count + 1))
        } else {
            _uiEvents.value = Event("تعداد درخواستی شما از موجودی بیشتر میباشد")
        }
    }

    fun decrease(cartItem: CartItem) {
        val count = cartItem.count
        if (count > 1) {
            CartRepo.update(cartItem.copy(count = count - 1))
        }
        //else delete(cartItem)
    }

    fun checkCartQuantity(): Boolean {
        if (!PrefsRepo.isCartQuantityCheckedToday()) {
            cartItemsList?.let { list ->
                checkCartQuantityEvent.value =
                    CheckCartQuantityRequest(
                        list.map { cartItem ->
                            cartItem.makeSummeryForCheckQuantity()
                        }
                    )
            }
            return false
        }
        return true
    }


    fun cancelJobs() {
        CartRepo.cancelJobs()
    }

}