package com.amir.ir.privatestore.models.reponses

import com.amir.ir.privatestore.models.Product
import com.google.gson.annotations.SerializedName

data class ProductDetailsResponse(
    @SerializedName("product")
    val product: Product,
    @SerializedName("relative_products")
    val relativeProducts: ArrayList<Product>,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("error_msg")
    val errorMsg: String? = null
)