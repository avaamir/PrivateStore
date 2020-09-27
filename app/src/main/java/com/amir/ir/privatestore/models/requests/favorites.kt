package com.amir.ir.privatestore.models.requests

import com.amir.ir.privatestore.models.Product
import com.google.gson.annotations.SerializedName

data class AddFavoriteResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("errorText")
    val errorMsg: String?
)

data class DeleteFavoriteResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("errorText")
    val errorMsg: String?
)

data class GetFavoriteResponse(
    @SerializedName("likes")
    val products: List<Product>,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("errorText")
    val errorMsg: String?
)
