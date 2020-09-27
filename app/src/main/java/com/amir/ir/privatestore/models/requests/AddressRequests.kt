package com.amir.ir.privatestore.models.requests

import com.amir.ir.privatestore.models.Address
import com.google.gson.annotations.SerializedName

data class AddAddressResponse (
    @SerializedName("id")
    val id: Int,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("errorText")
    val errorMsg: String?
)

data class EditDeleteAddressResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("errorText")
    val errorMsg: String?
)

data class GetAddressResponse(
    @SerializedName("addresses")
    val addresses: ArrayList<Address>,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("errorText")
    val errorMsg: String?
)