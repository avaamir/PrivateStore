package com.amir.ir.privatestore.models.enums

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class ListOrder(val id: Int) : Parcelable { //ProductListOrderIDs
    UN_DEFINED(0),
    PRICE_ASCENDING(1),
    PRICE_DESCENDING(2),
    NEW(3),
    MOST_SELL(4)
}