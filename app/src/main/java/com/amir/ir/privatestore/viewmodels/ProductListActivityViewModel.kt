package com.amir.ir.privatestore.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.amir.ir.privatestore.models.Category
import com.amir.ir.privatestore.models.Product
import com.amir.ir.privatestore.models.enums.Field
import com.amir.ir.privatestore.models.enums.ListOrder
import com.amir.ir.privatestore.models.requests.GetProductsRequest
import com.amir.ir.privatestore.models.requests.SearchRequest
import com.amir.ir.privatestore.repository.RemoteRepo
import com.amir.ir.privatestore.utils.Event


private const val CHUNK_SIZE = 10 //todo ask mammad about this

class ProductListActivityViewModel : ViewModel() {

    var isFirstTime: Boolean = true
        private set
        get() {
            if (field) {
                field = false
                return true
            }
            return field
        }

    var isJustInStock: Boolean = true
        private set
    var listOrder: ListOrder = ListOrder.NEW
        private set

    var isRecyclerGrid = false

    private var _toolbarTitle: MutableLiveData<String> = MutableLiveData()
    val toolbarTitle: LiveData<String> get() = _toolbarTitle

    private val _orderTitle: MutableLiveData<String> = MutableLiveData()
    val orderTitle: LiveData<String> get() = _orderTitle


    private val _onSortOrderChangedEvent = MutableLiveData<Event<ListOrder>>()
    val onSortOrderChangedEvent: LiveData<Event<ListOrder>> get() = _onSortOrderChangedEvent

    //Start Of Pagination variables
    private val _serverErrorMsg = MutableLiveData<Event<String>>()
    val serverErrorMsg: LiveData<Event<String>> get() = _serverErrorMsg

    private var category: Category? = null
    lateinit var field: Field
        private set

    private var isRequestActive: Boolean = false

    private val _isAllPageLoaded: MutableLiveData<Event<Boolean>> = MutableLiveData()

    val isAllPageLoaded: LiveData<Event<Boolean>>
        get() = _isAllPageLoaded

    var isAllPageLoadedVal = false
        private set


    private val page: MutableLiveData<Int> = MutableLiveData()
    private var _page = 0
    private var totalPage = 0

    val isListEmpty get() = totalPage == 0

    private val list: ArrayList<Product> = ArrayList()

    //TODO............
    var searchKey: String? = null
    private val isActivityOpenedForSearch get() = searchKey != null
    //todo.............

    val products: LiveData<ArrayList<Product>> = Transformations.switchMap(page) { page ->
        val newList: MutableLiveData<ArrayList<Product>> = MutableLiveData()

        if (page > totalPage) { //lastPage passed: load new Page
            var getProductsRequest: GetProductsRequest? = null
            var searchRequest: SearchRequest? = null

            if (isActivityOpenedForSearch) {
                searchRequest = SearchRequest(
                    searchKey = searchKey!!,
                    page = page,
                    catId = category?.id ?: 0,
                    orderId = listOrder,
                    isJustInStock = isJustInStock
                )
            } else {
                getProductsRequest = GetProductsRequest(
                    field = field,
                    page = page,
                    catId = category?.id ?: 0,
                    orderId = listOrder,
                    isJustInStock = isJustInStock
                )
            }

            RemoteRepo.getSearchProducts(
                getProductsRequest = getProductsRequest,
                searchRequest = searchRequest
            ) { it, isSuccessful, errorMsg ->
                isRequestActive = false
                if (it.isNotEmpty()) {
                    this.list.addAll(it)
                    totalPage = page
                    val startIndex = when (page) {
                        in 0..3 -> 0
                        else -> CHUNK_SIZE * (page - 3)
                    }
                    val endIndex = when (page) {
                        in 0..3 -> {
                            if (list.size > CHUNK_SIZE * (page + 1))
                                CHUNK_SIZE * page
                            else
                                list.size
                        }
                        else -> {
                            if (list.size > CHUNK_SIZE * page)
                                CHUNK_SIZE * page
                            else
                                list.size
                        }
                    }
                    //todo taze ezafe kardam shoru
                    if (it.size < CHUNK_SIZE) {
                        isAllPageLoadedVal = true
                        _page = totalPage

                        _isAllPageLoaded.value =
                            Event(true) //notify Ui to update (for example hide the Progress bar in bottom of recyclerView)
                    }
                    //todo taze ezafe kardam tamam
                    newList.value = ArrayList(this.list.subList(startIndex, endIndex))
                } else {
                    if (isSuccessful) {
                        isAllPageLoadedVal = true
                        _page = totalPage

                        _isAllPageLoaded.value =
                            Event(true) //notify Ui to update (for example hide the Progress bar in bottom of recyclerView)
                    } else {
                        _page--
                        _serverErrorMsg.value = Event(errorMsg)
                    }
                }
            }
        } else { // data is already loaded, get it from list object in viewModel
            val startIndex = when (page) {
                in 0..3 -> 0
                else -> CHUNK_SIZE * (page - 3)
            }
            val endIndex = when (page) {
                in 0..3 -> {
                    if (list.size > CHUNK_SIZE * page)
                        CHUNK_SIZE * page
                    else
                        list.size
                }
                else -> {
                    if (list.size > CHUNK_SIZE * (page + 1))
                        CHUNK_SIZE * page
                    else
                        list.size
                }
            }
            newList.value = ArrayList(this.list.subList(startIndex, endIndex))
        }
        newList
    }
    //End Of Pagination variables


    fun setFieldAndCategory(
        field: Field,
        category: Category?,
        _listOrder: ListOrder = ListOrder.MOST_SELL,
        isJustInStock: Boolean = true
    ) {
        this.category = category
        this.field = field
        this.listOrder = if (field == Field.NEW || field == Field.MOST_SELL) {
            ListOrder.PRICE_ASCENDING
        } else {
            _listOrder
        }
        val listOrder = this.listOrder

        this.isJustInStock = isJustInStock

        if (category == null) {
            _toolbarTitle.value = when (field) {
                Field.UN_DEFINED -> throw(Throwable("Just Category can be null, Field can not be Un-Defined"))
                Field.NEW -> "جدیدترین ها"
                Field.MOST_SELL -> "پرفروش ترین ها"
                Field.AMAZING -> "شگفت انگیزها"
                Field.OUR_RECOMMEND -> "پیشنهاد ما به شما"
                Field.CATEGORY -> throw(Throwable("Can not have category==null with Field=CATEGORY"))
            }
        } else {
            _toolbarTitle.value = category.title
        }

        _orderTitle.value = when (listOrder) {
            ListOrder.UN_DEFINED -> throw(Throwable("Order can not be Un-Defined"))
            //ListOrder.AVAILABLE -> "موجود"
            ListOrder.PRICE_ASCENDING -> "قیمت از کم به زیاد"
            ListOrder.PRICE_DESCENDING -> "قیمت از زیاد به کم"
            ListOrder.NEW -> "جدیدترین"
            ListOrder.MOST_SELL -> "پرفروش ترین"
        }



        nextPage()
    }

    fun setListOrderAndJustInStock(
        listOrder: ListOrder,
        isJustInStock: Boolean
    ) {
        this.isJustInStock = isJustInStock
        this.listOrder = listOrder
        resetList()
        _onSortOrderChangedEvent.value = Event(listOrder)
    }

    fun resetList() {
        if (!isRequestActive) {
            isRequestActive = true
            this.list.clear()
            totalPage = 0
            this._page = 0
            page.value = 1
            isAllPageLoadedVal = false
            _isAllPageLoaded.value = Event(false)
        }
    }

    fun nextPage() {
        if (listOrder == ListOrder.UN_DEFINED)
            throw(Throwable("first set categoryId and orderId and field then call this function"))

        if (isRequestActive) {
            return
        } else {
            isRequestActive = true
        }

        if (_page < totalPage || !isAllPageLoadedVal)
            this.page.value = ++_page
    }

    fun prevPage() {
        if (listOrder == ListOrder.UN_DEFINED)
            throw(Throwable("first set categoryId and orderId and field then call this function"))

        /*if (isRequestActive) { //todo vase prevPage niazi nadarim activeReq Konim ya checkesh konim chum already download shode va tu list hastesh
            return
        } else {
            isRequestActive = true
        }*/

        if (_page > 3)
            this.page.value = --_page
    }

    fun cancelJobs() {
        //todo not implemented
    }
}