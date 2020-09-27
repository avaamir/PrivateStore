package com.amir.ir.privatestore.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Color(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("quantityLimit")
    var count: Int = 0,
    @SerializedName("color")
    var name: String,
    @SerializedName("colorCode")
    var colorCode: String
) : Parcelable {
    @IgnoredOnParcel
    @Transient
    var isSelected: Boolean = false
}