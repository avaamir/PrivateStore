package com.amir.ir.privatestore.models

import com.google.gson.annotations.SerializedName

data class DeliveryMethod(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: String
) {
    val isBike get() = (id == 2) //todo id Peik ke az server miad

    override fun toString(): String {
        return name
    }
}