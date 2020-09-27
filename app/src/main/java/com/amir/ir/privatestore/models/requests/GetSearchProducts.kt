package com.amir.ir.privatestore.models.requests

import com.amir.ir.privatestore.models.enums.Field
import com.amir.ir.privatestore.models.enums.ListOrder

data class GetProductsRequest(
    val field: Field,
    val page: Int,
    val catId: Int,
    val orderId: ListOrder,
    val isJustInStock: Boolean
)

data class SearchRequest(
    val searchKey: String,
    val page: Int,
    val catId: Int,
    val orderId: ListOrder,
    val isJustInStock: Boolean
)