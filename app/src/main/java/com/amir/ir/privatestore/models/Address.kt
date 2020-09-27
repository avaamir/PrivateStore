package com.amir.ir.privatestore.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.amir.ir.privatestore.UserConfigs
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "address_tb")
@Parcelize
data class Address(
    @PrimaryKey
    @SerializedName("id")
    var id: Int = 0,   //age default value behesh nadim autoGenerate nemikone
    @SerializedName("title")
    var originalTitle: String?,
    @SerializedName("receiver")
    val ownerName: String,
    @SerializedName("Plaque")
    val pelak: String?,
    @SerializedName("address")
    val address: String,
    @SerializedName("zip_code")
    val postCode: String,
    @SerializedName("lat")
    val lat: String? = null,
    @SerializedName("lon")
    val lng: String? = null,

    //TODO not added to ui
    @SerializedName("city")
    val city: String?,   //TODO not added to ui
    @SerializedName("state")
    val province: String?,   //TODO not added to ui
    @SerializedName("phone")
    val receiverPhone: String = UserConfigs.userVal?.phone ?: ""   //TODO not added to ui
) : Parcelable {

    @IgnoredOnParcel
    @Ignore
    @Transient
    var isSelected = false

    @IgnoredOnParcel
    val title
        get() = if (originalTitle.isNullOrBlank()) "بدون عنوان" else originalTitle

    @IgnoredOnParcel
    val pelakForUi
        get() = pelak ?: "ندارد"

}