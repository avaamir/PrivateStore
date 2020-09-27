package com.amir.ir.privatestore.models.requests

import com.amir.ir.privatestore.models.Address
import com.amir.ir.privatestore.models.CartItem
import com.amir.ir.privatestore.models.DeliveryMethod
import com.amir.ir.privatestore.models.Order
import com.amir.ir.privatestore.models.enums.DiscountType
import com.amir.ir.privatestore.models.enums.PaymentMethod
import com.google.gson.annotations.SerializedName

data class SubmitOrderRequest(
    @SerializedName("products")
    val cartItemSummeryRequest: List<SubmitOrderCartItemSummeryRequest>,
    @SerializedName("addressId")
    val addressId: Int,
    @SerializedName("paymentMethod")
    val paymentMethod: Int,
    @SerializedName("sendMethod")
    val sendMethod: Int,
    @SerializedName("discount_id")
    val discountId: Int
)

data class SubmitOrderResponse(
    @SerializedName("cartId")
    val cartId: Int,
    @SerializedName("url")
    val paymentLink: String?,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("errorText")
    val errorMsg: String?
)

data class CheckPaymentResultResponse(
    @SerializedName("payment_status") //todo check this to work
    val isPaymentOk: Boolean,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("errorText")
    val errorMsg: String?
)

data class SubmitOrderCartItemSummeryRequest(
    @SerializedName("pId")
    val pid: Int,
    @SerializedName("colorId")
    val colorId: Int,
    @SerializedName("sizeId")
    val sizeId: Int,
    @SerializedName("count")
    val count: Int
)

data class CheckDiscountCodeResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("kind")
    private val _type: Int,
    @SerializedName("minPrice")
    val minApplicablePrice: Int,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("errorText")
    val errorMsg: String?
) {
    val type
        get() = when (_type) {
            DiscountType.Amount.id -> {
                println("debug: type called -> amount")
                DiscountType.Amount
            }
            DiscountType.Percent.id -> DiscountType.Percent
            else -> DiscountType.UnDefined
        }

    init {
        println("debug: $_type")
    }
}

data class GetDeliveryMethodsResponse(
    @SerializedName("payment_methods")
    val paymentMethods: List<Int>?,
    @SerializedName("delivery_methods")
    val methods: List<DeliveryMethod>,
    @SerializedName("taxPrice")
    val taxPrice: String,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("errorText")
    val errorMsg: String?
)


data class GetOrdersResponse(
    @SerializedName("orders")
    val orders: ArrayList<Order>,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("errorText")
    val errorMsg: String?
)


data class GetOrderDetailsResponse(
    @SerializedName("orderList")
    private val _ordersListItem: ArrayList<OrderListItem>,
    @SerializedName("address")
    val address: Address,
    @SerializedName("total_price")
    val totalMainPrice: String,
    @SerializedName("discountPrice")
    val totalDiscountAmount: String, //todo bebin chi mifrese
    @SerializedName("paymentPrice")
    val totalPayment: String,
    @SerializedName("post_price")
    val deliveryPrice: String,
    @SerializedName("payment_method")
    private val _paymentMethod: Int,
    @SerializedName("taxPrice")
    val taxPrice: String,
    @SerializedName("payment_status")
    private val _paymentStatus: Int,
    @SerializedName("post_method")
    private val _deliveryMethodName: String,

    @SerializedName("error")
    val error: Boolean,
    @SerializedName("errorText")
    val errorMsg: String?
) {
    val deliveryMethodName get() = "($_deliveryMethodName)"
    val paymentStatus get() = if (_paymentStatus == 0) "پرداخت نشده" else "پرداخت شده"
    val paymentMethodName
        get() = when (_paymentMethod) {
            PaymentMethod.Cash.id -> "نقدی"
            PaymentMethod.Online.id -> "آنلاین"
            else -> "not-defined"
        }
    val ordersListItem
        get() = _ordersListItem.mapIndexed { index, cartItem ->
            cartItem.makeCartItem(index) //server id nemiferestad va dar local db tarif shode khater hamin inja set mikonim ta dar recyclerAdapter moshkel pish nayad
        }
}

//val totalPayment get() = (totalMainPrice.toInt() - totalDiscountAmount.toInt() + deliveryPrice.toInt()).toString() //todo bebin chi mifrese

data class OrderListItem(
    @SerializedName("id")
    val pid: Int,
    @SerializedName("title")
    val productName: String,
    @SerializedName("count")
    val count: Int,  //mojudi marbout be yek rang ya size khas (ya rang dare ya size)
    @SerializedName("color")
    val colorName: String?,
    @SerializedName("mainPrice")
    val mainPrice: String,   //gheymat un kala bar asas size
    @SerializedName("showPrice")
    val discountPrice: String?,
    @SerializedName("size")
    val sizeName: String?,
    @SerializedName("image")
    val image: String?
) {
    fun makeCartItem(id: Int) = CartItem(
        id = id,
        pid = pid,
        productName = productName,
        count = count,
        colorName = colorName,
        mainPrice = mainPrice,
        discountPrice = discountPrice,
        sizeName = sizeName,
        image = image,
        colorId = 0,
        colorCode = null,
        sizeId = 0,
        totalCount = 0
    )
}


