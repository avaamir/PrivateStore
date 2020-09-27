package com.amir.ir.privatestore.models.requests

import com.amir.ir.privatestore.models.Comment
import com.google.gson.annotations.SerializedName

data class GetCommentsResponse(
    @SerializedName("comments")
    val comments: List<Comment>,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("errorText")
    val errorMsg: String?
)

data class SubmitCommentRequest(
    @SerializedName("productId")
    val pid: Int,
    @SerializedName("text")
    val text: String,
    @SerializedName("score")
    val rate: Float
)

data class SubmitCommentResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("errorText")
    val errorMsg: String?
)