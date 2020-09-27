package com.amir.ir.privatestore.models

import android.os.Parcelable
import com.amir.ir.privatestore.models.enums.SlideType
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Slide(
    @SerializedName("id")
    var id: Int,
    @SerializedName("title")
    var title: String?,
    @SerializedName("image")
    var image: String,
    @SerializedName("type")
    private val _type: Int,
    @SerializedName("targetId")
    var targetId: Int, //this can be pid, catId and etc according to type, and user will navigated to associated page according to it and and associated api will be called
    @SerializedName("link")
    var link: String?
) : Parcelable {

    val type
        get() = when (_type) {
            1 -> SlideType.PRODUCT
            2 -> SlideType.CATEGORY
            else -> SlideType.NOT_DEFINED
        }
}