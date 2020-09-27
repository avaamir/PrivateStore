package com.amir.ir.privatestore.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(
    @SerializedName("id")
    var id: Int,
    @SerializedName("title")
    var title: String?,
    @SerializedName("image")
    var image: String
) : Parcelable