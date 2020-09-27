package com.amir.ir.privatestore.models.enums

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class Field(val id: Int) : Parcelable {
    UN_DEFINED(0),
    CATEGORY(1),
    NEW(2),
    MOST_SELL(3),
    AMAZING(4),
    /////////
    OUR_RECOMMEND(4) //todo our_recommend smate server implement nashode felan id amazing ro bar gardundam
}