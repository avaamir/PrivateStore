package com.amir.ir.privatestore.models.reponses

import com.amir.ir.privatestore.models.Product
import com.google.gson.annotations.SerializedName

data class ProductListResponse(
    @SerializedName("products")
    val products: ArrayList<Product>,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("errorText")
    val errorMsg: String?
)