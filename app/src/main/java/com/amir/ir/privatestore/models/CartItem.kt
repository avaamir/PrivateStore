package com.amir.ir.privatestore.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.amir.ir.privatestore.models.requests.CheckQuantityCartItemSummeryRequest
import com.amir.ir.privatestore.models.requests.SubmitOrderCartItemSummeryRequest
import com.google.gson.annotations.SerializedName

@Entity(tableName = "cart_tb")
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val pid: Int,
    val productName: String,
    val count: Int,  //mojudi marbout be yek rang ya size khas (ya rang dare ya size)
    val colorId: Int = 0,
    val colorName: String?,
    val colorCode: String?,
    val mainPrice: String,   //gheymat un kala bar asas size
    val discountPrice: String?,
    val sizeId: Int = 0,
    val sizeName: String?,
    val image: String?,
    val totalCount: Int //mojudi kala az un rang ya size
) {
    val hasDiscount: Boolean get() = (discountPrice != null && discountPrice != mainPrice)

    val colorNameForCartItemTextView get() = colorName ?: "ندارد"
    val sizeNameForCartItemTextView get() = sizeName ?: "ندارد"

    val colorForUI: Color? get() = if (colorCode != null) Color(0,0, colorName!!, colorCode) else null

    fun makeSummeryForCheckQuantity() = CheckQuantityCartItemSummeryRequest(
        pid = pid,
        colorId = colorId,
        sizeId = sizeId
    )

    fun makeSummeryForSubmitOrder() = SubmitOrderCartItemSummeryRequest(
        pid = pid,
        colorId = colorId,
        sizeId = sizeId,
        count = count
    )

}