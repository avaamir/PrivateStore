package com.amir.ir.privatestore.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.amir.ir.privatestore.UserConfigs
import com.amir.ir.privatestore.models.*
import com.amir.ir.privatestore.models.reponses.ProductDetailsResponse
import com.amir.ir.privatestore.repository.RemoteRepo
import com.amir.ir.privatestore.repository.persistance.cartdb.CartRepo
import com.amir.ir.privatestore.repository.persistance.favoritedb.FavoriteRepo
import com.amir.ir.privatestore.utils.DoubleTrigger
import com.amir.ir.privatestore.utils.Event
import kotlin.math.min

class ProductDetailsActivityViewModel(application: Application) : AndroidViewModel(application) {

    init {
        CartRepo.setContext(application)
        FavoriteRepo.setContext(application)
    }

    var isFirstTime = true  //use for crossFade animation
        private set
        get() {
            if (field) {
                field = false
                return true
            }
            return field
        }

    var isRequestLoginForTag: Boolean = false
    var chosenSize: Size? = null
    var chosenColor: Color? = null


    private val _favoriteDeleteEvent = MutableLiveData<Product>()
    val favoriteDeleteResponse = Transformations.switchMap(_favoriteDeleteEvent) {
        FavoriteRepo.delete(it.pid)
    }

    private val _favoriteInsertUpdateEvent = MutableLiveData<Favorite>()
    val favoriteInsertUpdateResponse = Transformations.switchMap(_favoriteInsertUpdateEvent) {
        FavoriteRepo.insertUpdate(it)
    }


    private val _product = MutableLiveData<Product>()
    private val pid: MutableLiveData<Int> = MutableLiveData()

    val loggedInEvent
        get() = Transformations.map(UserConfigs.user) {
            Event(it != null)
        }


    private val _productDetailsPageResponse: LiveData<ProductDetailsResponse?> =
        Transformations.switchMap(DoubleTrigger(pid, _product)) {
            println("debug: scope -> product Transformation")
            val product = it.second
            val pid = it.first
            RemoteRepo.getProductDetails(product?.pid ?: pid!!)
        }

    val product: LiveData<Product?> = Transformations.map(_productDetailsPageResponse) {
        it?.product
    }

    val relatedProducts: LiveData<ArrayList<Product>?> =
        Transformations.map(_productDetailsPageResponse) {
            it?.relativeProducts
        }

    private val _tagEvent: MutableLiveData<Event<Any>> = MutableLiveData()
    val isTagged: LiveData<Boolean> = Transformations.switchMap(DoubleTrigger(product, _tagEvent)) {
        println("debug: scope -> isTagged transformation start")
        val product = it.first
        if (product != null) {
            FavoriteRepo.exist(product.pid)
        } else {
            MutableLiveData(false)
        }
    }

    private val _navigateToCartEvent: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val navigateToCartEvent get() = _navigateToCartEvent


    private val _toolbarTitle: LiveData<String> =
        Transformations.switchMap(DoubleTrigger(_product, product)) {
            MutableLiveData(it.second?.name ?: it.first?.name ?: "")
        }
    val toolbarTitle: LiveData<String> get() = _toolbarTitle


    fun setPid(pid: Int) {
        if (this.pid.value != pid)
            this.pid.value = pid
    }


    fun setProduct(product: Product) {
        if (product != _product.value) {
            _product.value = product
            pid.value = product.pid
        }
    }

    fun tryAgain() {
        this.pid.value = this.pid.value
    }


    fun cancelJobs() {
        RemoteRepo.cancelServerJobs() //todo should cancel this specific job not all server jobs
    }

    fun toggleTag() {
        val isTagged = isTagged.value
        val product = this.product.value!!
        if (isTagged != null) {
            if (!isTagged) {
                println("debug: insertUpdate Calling")
                _favoriteInsertUpdateEvent.value =
                    Favorite(
                        pid = product.pid,
                        userId = UserConfigs.userVal!!.id,
                        name = product.name,
                        image = product.images?.get(0)
                    )
            } else {
                println("debug: delete Calling")
                _favoriteDeleteEvent.value = product
            }
        } else {
            println("debug: insertUpdate Calling")
            _favoriteInsertUpdateEvent.value =
                Favorite(
                    pid = product.pid,
                    userId = UserConfigs.userVal!!.id,
                    name = product.name,
                    image = product.images?.get(0)
                )
        }

        //   Handler().postDelayed({
        this._tagEvent.value = Event(Unit)
        //   }, 2000)

    }

    fun addToCart() {
        val chosenColor = this.chosenColor
        val chosenSize = this.chosenSize
        val colorId = chosenColor?.id ?: 0
        val sizeId = chosenSize?.id ?: 0


        CartRepo.exist(
            product.value!!.pid,
            sizeId,
            colorId
        ) { cartItem -> //todo age ye masouli avalesh size nadashte bashe vali badan behesh size ezafe beshe che konim??

            val product = this.product.value!!
            val mainPrice = if (product.hasSize) {
                chosenSize!!.mainPrice
            } else {
                product.mainPrice
            }
            val discountPrice = if (product.hasSize) {
                chosenSize!!.discountPrice
            } else {
                product.discountPrice
            }
            val totalCount =
                when {
                    product.hasSize && product.hasColor -> {
                        min(chosenColor!!.count, chosenSize!!.count)
                    }
                    product.hasSize -> {
                        chosenSize!!.count
                    }
                    product.hasColor -> {
                        chosenColor!!.count
                    }
                    else -> {
                        product.totalQuantity
                    }
                }

            if (cartItem != null) { //already exist
                if (cartItem.count < totalCount) {
                    CartRepo.update(
                        cartItem.copy(
                            count = cartItem.count + 1,
                            mainPrice = mainPrice,
                            discountPrice = discountPrice,
                            totalCount = totalCount
                        )
                    ) {
                        _navigateToCartEvent.value = Event(true)
                    }
                } else {
                    CartRepo.update(
                        cartItem.copy(
                            count = totalCount,
                            mainPrice = mainPrice,
                            discountPrice = discountPrice,
                            totalCount = totalCount
                        )
                    )
                    _navigateToCartEvent.value = Event(false) //tedad darkhasti bishtar az mojudi
                }
            } else { //item not exist
                CartRepo.insert(
                    CartItem(
                        pid = product.pid,
                        productName = product.name,
                        count = 1,
                        colorId = colorId,
                        colorName = chosenColor?.name,
                        colorCode = chosenColor?.colorCode,
                        mainPrice = mainPrice,
                        discountPrice = discountPrice,
                        sizeId = chosenSize?.id ?: 0,
                        sizeName = chosenSize?.name,
                        image = product.images?.get(0), //todo aya momkene yek masoul ax nadshte bashe?
                        totalCount = totalCount
                    )
                ) {
                    _navigateToCartEvent.value = Event(true)
                }
            }
        }
    }


}