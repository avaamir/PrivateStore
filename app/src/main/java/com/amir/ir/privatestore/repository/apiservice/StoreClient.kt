package com.amir.ir.privatestore.repository.apiservice

import com.amir.ir.privatestore.models.Address
import com.amir.ir.privatestore.models.reponses.*
import com.amir.ir.privatestore.models.requests.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface StoreClient {

    //Main Pages
    @GET("mainPage")
    suspend fun getMainPage(): Response<MainPageResponse>

    @GET("mainCategoryPage/{cat_id}")
    suspend fun getCategoryPage(@Path("cat_id") catId: Int): Response<CategoryPageResponse>

    @GET("productList")
    suspend fun getProducts(
        @Query("field") field: Int,
        @Query("page") page: Int,
        @Query("orderId") orderId: Int,
        @Query("categoryId") catId: Int,
        @Query("exist") isJustInStock: Int
    ): Response<ProductListResponse>

    @GET("productDetails")
    suspend fun getProductDetails(@Query("id") pid: Int): Response<ProductDetailsResponse>

    //Authentication
    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<OnLoggedInResponse>

    @POST("signUp")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): Response<SignUpResponse>

    @POST("confirmCode")
    suspend fun confirmSignUpOTP(@Body confirmSignUpCodeRequest: ConfirmSignUpCodeRequest): Response<OnLoggedInResponse>

    @POST("reSend")
    suspend fun reSendSignUPSMS(@Body reSendSMSRequest: ReSendSMSRequest): Response<ReSendSMSResponse>

    @POST("sendForgotPassCode")
    suspend fun forgotPasswordRequest(@Body phoneObject: PhoneObject): Response<ForgotPasswordResponse>

    @POST("confirmForgetPassCode")
    suspend fun confirmForgotOTP(@Body forgotPasswordRequest: ForgotPasswordRequest): Response<OnLoggedInResponse>

    @POST("forgetPassChange")
    suspend fun changePassword(@Body changePasswordRequest: ChangePasswordRequest): Response<ForgotPasswordResponse>

    @POST("sendForgotPassCode")
    suspend fun reSendForgotSMS(@Body phoneObject: PhoneObject): Response<ReSendSMSResponse>

    //Cart
    @POST("checkCartQuantity")
    suspend fun checkCartQuantity(@Body checkCartQuantityRequest: CheckCartQuantityRequest): Response<CheckCartQuantityResponse>


    //Address
    @GET("getAddress")
    suspend fun getAddresses(): Response<GetAddressResponse>

    @POST("addAddress")
    suspend fun addAddress(@Body address: Address): Response<AddAddressResponse>

    @POST("editAddress")
    suspend fun updateAddress(@Body address: Address): Response<EditDeleteAddressResponse>

    @GET("deleteAddress")
    suspend fun deleteAddress(@Query("id") id: Int): Response<EditDeleteAddressResponse>

    //SubmitOrder
    @GET("getDeliveryMethod")
    suspend fun getDeliveryMethod(@Query("id") addressId: Int): Response<GetDeliveryMethodsResponse>

    @GET("checkDiscountCode")
    suspend fun checkDiscountCode(@Query("code") discountCode: String): Response<CheckDiscountCodeResponse>

    @POST("submitOrder")
    suspend fun submitOrder(@Body submitOrderRequest: SubmitOrderRequest): Response<SubmitOrderResponse>

    @GET("checkPayment")
    suspend fun checkPaymentResult(@Query("cartId") cartId: Int): Response<CheckPaymentResultResponse>

    //favorites
    @GET("addLikeProduct")
    suspend fun addFavorite(@Query("id") pid: Int): Response<AddFavoriteResponse>

    @GET("deleteLikeProduct")
    suspend fun unTagProduct(@Query("id") pid: Int): Response<DeleteFavoriteResponse>

    @GET("getLikesProduct")
    suspend fun getFavorites(): Response<GetFavoriteResponse>

    //Comment
    @GET("getComments")
    suspend fun getComments(
        @Query("productId") pid: Int,
        @Query("page") page: Int
    ): Response<GetCommentsResponse>

    @POST("addComment")
    suspend fun submitComment(@Body submitCommentRequest: SubmitCommentRequest): Response<SubmitCommentResponse>


    @GET("getOrderDetails")
    suspend fun getOrderDetails(@Query("id") orderId: Int): Response<GetOrderDetailsResponse>

    @GET("getOrders")
    suspend fun getOrders(): Response<GetOrdersResponse>

    //search
    @GET("search")
    suspend fun search(
        @Query("text") searchKey: String,
        @Query("categoryId") catId: Int,
        @Query("orderId") orderId: Int,
        @Query("page") page: Int,
        @Query("exist") isJustInStock: Int
    ): Response<ProductListResponse>

    //Edit Profile
    @POST("doEditPanel")
    @Multipart
    suspend fun editProfile(
        @Part("name") name: RequestBody?,
        @Part("oldPassword") oldPassword: RequestBody?,
        @Part("newPassword") newPassword: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Response<EditProfileResponse>

    @GET("deleteProfileImage")
    suspend fun deleteProfilePic(): Response<BaseServerError>

}