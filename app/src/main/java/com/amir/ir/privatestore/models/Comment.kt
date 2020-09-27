package com.amir.ir.privatestore.models

import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("id")
    val id: Int,
    @SerializedName("user_name")
    val userName: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("score")
    val rate: Float,
    @SerializedName("date")
    val date: String,
    @SerializedName("image")
    val image: String? = null
)