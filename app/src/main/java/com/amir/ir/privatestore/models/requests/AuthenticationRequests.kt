package com.amir.ir.privatestore.models.requests

import com.amir.ir.privatestore.models.User
import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("phone")
    val phone: String,
    @SerializedName("password")
    val password: String
)

data class SignUpRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("password")
    val password: String
)

data class OnLoggedInResponse( //vase login, va sign-up , change-Password: vaghti user token ro gereft ina bar migarde
    @SerializedName("id")
    private val id: Int,
    @SerializedName("name")
    private val name: String,
    @SerializedName("phone")
    private val phone: String,
    @SerializedName("image")
    private val profilePic: String? = null,
    @SerializedName("token")
    private val token: String,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("errorText")
    val errorMsg: String?
) {
    val user: User?
        get() = if (!error) {
            User(
                _id = id,
                name = name,
                phone = phone,
                profilePic = profilePic,
                token = token
            )
        } else {
            null
        }
}

data class SignUpResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("errorText")
    val errorMsg: String?
)


data class ConfirmSignUpCodeRequest(
    @SerializedName("id")
    val id: Int,
    @SerializedName("code")
    val otp: String?
)

data class ReSendSMSResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("errorText")
    val errorMsg: String?
)

data class ReSendSMSRequest(
    val id: Int
)


data class ForgotPasswordRequest(
    @SerializedName("phone")
    val phone: String,  //vase ersal shomare
    @SerializedName("code")
    val otp: String? = null //faghat vase taeed otp
)

data class ForgotPasswordResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("errorText")
    val errorMsg: String?
)

data class ChangePasswordRequest(
    val password: String
)

data class PhoneObject(
    val phone: String
)
