package com.amir.ir.privatestore.models

import android.os.Parcelable
import com.amir.ir.privatestore.models.enums.OrderStatus
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Order(
    @SerializedName("id")
    val id: Int,
    @SerializedName("date")
    val date: String,
    @SerializedName("paymentPrice")
    val price: String,
    @SerializedName("status")
    var _status: String,
    @SerializedName("orderCode")
    private val _trackNumber: String
) : Parcelable {

    val trackNumber get() = _trackNumber.toUpperCase(Locale.US)
    val status
        get() = when (_status) {
            "موفق" -> OrderStatus.Delivered
            "در حال ارسال" -> OrderStatus.Preparation
            "ناموفق" -> OrderStatus.Failed
            else -> OrderStatus.Delivering
        }
}