package com.amir.ir.privatestore.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class Product(
    @SerializedName("id")
    val pid: Int,
    @SerializedName("title")
    val name: String,
    @SerializedName("android_des")
    val desc: String? = null,
    @SerializedName("image") //for product adapters
    val image: String? = null,
    @SerializedName("images") //for product detail page todo felan goftam images[0] set beshe badan dar product details image slider ezafe shavad
    val images: ArrayList<String>? = null,
    @SerializedName("mainPrice")
    val mainPrice: String,
    @SerializedName("showPrice")
    val discountPrice: String,
    @SerializedName("discountPercent")
    val discountPercent: String? = null,
    @SerializedName("quantityLimit")
    val totalQuantity: Int,
    @SerializedName("colors")
    val colors: @RawValue ArrayList<Color>? = null,
    @SerializedName("sizes")
    val sizes: @RawValue ArrayList<Size>? = null,
    @SerializedName("slug")
    val slug: String? = null,
    @Transient
    var isTagged: Boolean
) : Parcelable {
    val hasDiscount: Boolean get() = discountPrice != mainPrice
    val hasColor: Boolean get() = !colors.isNullOrEmpty()
    val hasSize: Boolean get() = !sizes.isNullOrEmpty()
    val isOutOfStock: Boolean get() = !hasSize && !hasColor && (totalQuantity == 0)

    fun makeFavorite(userId: Int) = Favorite(
        pid = pid,
        userId = userId,
        name = name,
        image = images?.get(0) ?: image
    )
}