package com.amir.ir.privatestore.models.reponses

import com.google.gson.annotations.SerializedName

data class BaseServerError(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("errorText")
    val errorMsg: String?
)