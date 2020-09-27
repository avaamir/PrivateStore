package com.amir.ir.privatestore.models

import android.os.Parcelable
import android.text.BoringLayout
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
class Size(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("quantityLimit")
    val count: Int = 0,
    @SerializedName("size")
    val name: String,
    @SerializedName("mainPrice")
    val mainPrice: String,
    @SerializedName("showPrice")
    val discountPrice: String
) : Parcelable {
    val hasDiscount: Boolean get() = mainPrice != discountPrice

    @IgnoredOnParcel
    @Transient var isSelected: Boolean = false
}