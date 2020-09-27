package com.amir.ir.privatestore.models.requests

import com.google.gson.annotations.SerializedName

class CheckCartQuantityRequest(
    @SerializedName("items")
    val checkQuantityCartItems: List<CheckQuantityCartItemSummeryRequest>
)

class CheckCartQuantityResponse(
    @SerializedName("cartItems")
    val cartSummeryItems: ArrayList<CartItemSummeryResponse>,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("errorText")
    val errorMsg: String?
)

data class CheckQuantityCartItemSummeryRequest(
    @SerializedName("pId")
    val pid: Int,
    @SerializedName("colorId")
    val colorId: Int,
    @SerializedName("sizeId")
    val sizeId: Int
)

data class CartItemSummeryResponse(
    @SerializedName("pId")
    val pid: Int,
    @SerializedName("colorId")
    val colorId: Int,
    @SerializedName("sizeId")
    val sizeId: Int,
    @SerializedName("quantityLimit")
    val quantityLimit: Int,
    @SerializedName("showPrice")
    val discountPrice: String,
    @SerializedName("mainPrice")
    val mainPrice: String
)