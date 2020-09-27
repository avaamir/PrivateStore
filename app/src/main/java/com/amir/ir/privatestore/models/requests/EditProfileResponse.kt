package com.amir.ir.privatestore.models.requests

import com.google.gson.annotations.SerializedName

data class EditProfileResponse (
    @SerializedName("name")
    val name: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("image")
    val profilePic: String,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("errorText")
    val errorMsg: String?
)