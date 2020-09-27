package com.amir.ir.privatestore.repository

import androidx.lifecycle.LiveData
import com.amir.ir.privatestore.UserConfigs
import com.amir.ir.privatestore.models.Message
import com.amir.ir.privatestore.models.Product
import com.amir.ir.privatestore.models.reponses.*
import com.amir.ir.privatestore.models.requests.*
import com.amir.ir.privatestore.repository.apiservice.ApiService
import com.amir.ir.privatestore.repository.apiservice.launchApi
import com.amir.ir.privatestore.utils.Constants.SERVER_ERROR
import com.amir.ir.privatestore.utils.RunOnceLiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

object RemoteRepo {
    private lateinit var serverJobs: CompletableJob

    fun getMainPage(): RunOnceLiveData<MainPageResponse?> {
        if (!::serverJobs.isInitialized || !serverJobs.isActive) serverJobs = Job()
        return object : RunOnceLiveData<MainPageResponse?>() {
            override fun onActiveRunOnce() {
                CoroutineScope(IO + serverJobs).launchApi({
                    val response = ApiService.getStoreClient().getMainPage()
                    if (response.isSuccessful) {
                        response.body()?.let { mainPageResponse ->
                            postValue(mainPageResponse)
                        }
                    } else {
                        postValue(null)
                    }
                }, {
                    postValue(null)
                })
            }

        }
    }

    fun getCategoryPage(catId: Int): RunOnceLiveData<CategoryPageResponse?> {
        if (!::serverJobs.isInitialized || !serverJobs.isActive) serverJobs = Job()
        return object : RunOnceLiveData<CategoryPageResponse?>() {
            override fun onActiveRunOnce() {
                CoroutineScope(IO + serverJobs).launchApi({
                    val response = ApiService.getStoreClient().getCategoryPage(catId)
                    if (response.isSuccessful) {
                        response.body()?.let { categoryPage ->
                            postValue(categoryPage)
                        }
                    } else {
                        postValue(null)
                    }
                }, {
                    postValue(null)
                })
            }
        }
    }

    fun getSearchProducts(
        getProductsRequest: GetProductsRequest? = null,
        searchRequest: SearchRequest? = null,
        onResponse: (ArrayList<Product>, isSuccessful: Boolean, errorMsg: String) -> Unit
    ) {
        if (!::serverJobs.isInitialized || !serverJobs.isActive) serverJobs = Job()

        CoroutineScope(IO + serverJobs).launchApi({
            val response: Response<ProductListResponse> = getProductsRequest?.run {
                ApiService.getStoreClient()
                    .getProducts(field.id, page, orderId.id, catId, if (isJustInStock) 1 else 0)
            } ?: searchRequest.run {
                ApiService.getStoreClient()
                    .search(this!!.searchKey, catId, orderId.id, page, if (isJustInStock) 1 else 0)
            }

            if (response.isSuccessful) {
                response.body().let {
                    if (it != null) {
                        withContext(Main) {
                            if (it.error) {
                                onResponse(ArrayList(), false, it.errorMsg ?: SERVER_ERROR)
                            } else {
                                onResponse(it.products, true, "")
                            }
                        }
                    } else {
                        onResponse(ArrayList(), false, "")
                    }
                }
            } else {
                withContext(Main) {
                    onResponse(ArrayList(), false, "")
                }
            }
        }, {
            GlobalScope.launch(Main) {
                onResponse(ArrayList(), false, "")
            }
        })
    }


    fun getUserOrdersList(): LiveData<GetOrdersResponse?> {
        if (!::serverJobs.isInitialized || !serverJobs.isActive) serverJobs = Job()
        return object : RunOnceLiveData<GetOrdersResponse?>() {
            override fun onActiveRunOnce() {
                CoroutineScope(IO + serverJobs).launchApi({
                    val response = ApiService.getStoreClient().getOrders()
                    postValue(response.body())
                }, {
                    postValue(null)
                })
            }
        }
    }

    fun cancelServerJobs() {
        if (::serverJobs.isInitialized && serverJobs.isActive)
            serverJobs.cancel("debug: server jobs cancelled")
    }

    fun getProductDetails(pid: Int): LiveData<ProductDetailsResponse?> {
        if (!::serverJobs.isInitialized || !serverJobs.isActive) serverJobs = Job()
        return object : RunOnceLiveData<ProductDetailsResponse?>() {
            override fun onActiveRunOnce() {
                CoroutineScope(IO + serverJobs).launchApi({
                    val response = ApiService.getStoreClient().getProductDetails(pid)
                    if (response.isSuccessful) {
                        response.body()?.let {
                            postValue(it)
                        }
                    } else {
                        postValue(null)
                    }
                }, {
                    postValue(null)
                })
            }
        }
    }

    fun getComments(pid: Int, page: Int): LiveData<GetCommentsResponse?> {
        if (!::serverJobs.isInitialized || !serverJobs.isActive) serverJobs = Job()
        return object : RunOnceLiveData<GetCommentsResponse?>() {
            override fun onActiveRunOnce() {
                CoroutineScope(IO + serverJobs).launchApi({
                    val response = ApiService.getStoreClient().getComments(pid, page)
                    postValue(response.body())
                }, {
                    postValue(null)
                })
            }
        }
    }

    fun getOderDetails(orderId: Int): LiveData<GetOrderDetailsResponse?> {
        if (!::serverJobs.isInitialized || !serverJobs.isActive) serverJobs = Job()
        return object : RunOnceLiveData<GetOrderDetailsResponse?>() {
            override fun onActiveRunOnce() {
                CoroutineScope(IO + serverJobs).launchApi({
                    val response = ApiService.getStoreClient().getOrderDetails(orderId)
                    postValue(response.body())
                }, {
                    postValue(null)
                })
            }
        }

    }

    fun signUpUser(signUpRequest: SignUpRequest): LiveData<SignUpResponse?> {
        if (!::serverJobs.isInitialized || !serverJobs.isActive) serverJobs = Job()
        return object : RunOnceLiveData<SignUpResponse?>() {
            override fun onActiveRunOnce() {
                CoroutineScope(IO + serverJobs).launchApi({
                    val response = ApiService.getStoreClient().signUp(signUpRequest)
                    if (response.isSuccessful) {
                        postValue(response.body())
                    } else {
                        postValue(null)
                    }
                }, {
                    postValue(null)
                })
            }
        }
    }

    fun login(req: LoginRequest): LiveData<OnLoggedInResponse?> {
        if (!::serverJobs.isInitialized || !serverJobs.isActive) serverJobs = Job()
        return object : RunOnceLiveData<OnLoggedInResponse?>() {
            override fun onActiveRunOnce() {
                CoroutineScope(IO + serverJobs).launchApi({
                    delay(1000)
                    val response = ApiService.getStoreClient().login(req)
                    response.body().let {
                        it?.user?.let { user ->
                            UserConfigs.loginUser(user)
                        }
                        postValue(it)
                    }
                }, {
                    postValue(null)
                })
            }
        }
    }

    fun confirmSignUpOTP(req: ConfirmSignUpCodeRequest): LiveData<OnLoggedInResponse?> {
        if (!::serverJobs.isInitialized || !serverJobs.isActive) serverJobs = Job()
        return object : RunOnceLiveData<OnLoggedInResponse?>() {
            override fun onActiveRunOnce() {
                CoroutineScope(IO + serverJobs).launchApi({
                    val response = ApiService.getStoreClient().confirmSignUpOTP(req)
                    response.body().let {
                        it?.user?.let { user ->
                            UserConfigs.loginUser(user)
                        }
                        postValue(it)
                    }
                }, {
                    postValue(null)
                })
            }
        }
    }

    fun reSendSignUpSms(id: Int): LiveData<ReSendSMSResponse> {
        if (!::serverJobs.isInitialized || !serverJobs.isActive) serverJobs = Job()
        return object : RunOnceLiveData<ReSendSMSResponse>() {
            override fun onActiveRunOnce() {
                CoroutineScope(IO + serverJobs).launchApi({
                    val response = ApiService.getStoreClient().reSendSignUPSMS(ReSendSMSRequest(id))
                    postValue(response.body())
                }, {
                    postValue(null)
                })
            }
        }
    }

    fun forgotPasswordRequest(phone: String): LiveData<ForgotPasswordResponse?> {
        if (!::serverJobs.isInitialized || !serverJobs.isActive) serverJobs = Job()
        return object : RunOnceLiveData<ForgotPasswordResponse?>() {
            override fun onActiveRunOnce() {
                CoroutineScope(IO + serverJobs).launchApi({
                    val response =
                        ApiService.getStoreClient().forgotPasswordRequest(PhoneObject(phone))
                    postValue(response.body())
                }, {
                    postValue(null)
                })
            }
        }
    }

    fun confirmForgotOTP(req: ForgotPasswordRequest): LiveData<OnLoggedInResponse?> {
        if (!::serverJobs.isInitialized || !serverJobs.isActive) serverJobs = Job()
        return object : RunOnceLiveData<OnLoggedInResponse?>() {
            override fun onActiveRunOnce() {
                CoroutineScope(IO + serverJobs).launchApi({
                    delay(1000)
                    val response = ApiService.getStoreClient().confirmForgotOTP(req)
                    response.body().let {
                        it?.user?.let { user ->
                            UserConfigs.loginUser(user)
                        }
                        postValue(it)
                    }
                }, {
                    postValue(null)
                })
            }
        }
    }

    fun reSendForgotSms(phone: String): LiveData<ReSendSMSResponse?> {
        if (!::serverJobs.isInitialized || !serverJobs.isActive) serverJobs = Job()
        return object : RunOnceLiveData<ReSendSMSResponse?>() {
            override fun onActiveRunOnce() {
                CoroutineScope(IO + serverJobs).launchApi({
                    val response = ApiService.getStoreClient().reSendForgotSMS(PhoneObject(phone))
                    postValue(response.body())
                }, {
                    postValue(null)
                })
            }
        }
    }

    fun changePassword(password: String): LiveData<ForgotPasswordResponse?> {
        if (!::serverJobs.isInitialized || !serverJobs.isActive) serverJobs = Job()
        return object : RunOnceLiveData<ForgotPasswordResponse?>() {
            override fun onActiveRunOnce() {
                CoroutineScope(IO + serverJobs).launchApi({
                    val response =
                        ApiService.getStoreClient().changePassword(ChangePasswordRequest(password))
                    postValue(response.body())
                }, {
                    postValue(null)
                })
            }
        }
    }

    fun checkCartQuantity(checkCartQuantityRequest: CheckCartQuantityRequest): LiveData<CheckCartQuantityResponse?> {
        if (!::serverJobs.isInitialized || !serverJobs.isActive) serverJobs = Job()
        return object : RunOnceLiveData<CheckCartQuantityResponse?>() {
            override fun onActiveRunOnce() {
                CoroutineScope(IO + serverJobs).launchApi({
                    val response =
                        ApiService.getStoreClient().checkCartQuantity(checkCartQuantityRequest)
                    postValue(response.body())
                }, {
                    postValue(null)
                })
            }
        }
    }


    //todo not implemented server side.......................................................

    fun submitOrder(submitOrderRequest: SubmitOrderRequest): LiveData<SubmitOrderResponse?> {
        if (!::serverJobs.isInitialized || !serverJobs.isActive) serverJobs = Job()
        return object : RunOnceLiveData<SubmitOrderResponse?>() {
            override fun onActiveRunOnce() {
                CoroutineScope(IO + serverJobs).launchApi({
                    val response = ApiService.getStoreClient().submitOrder(submitOrderRequest)
                    postValue(response.body())
                    //delay(1000)
                    //postValue(fakeSubmitOrderResponse(false, submitOrderRequest)) //todo delete this line uncomment next line todo postValue(response.body())
                }, {
                    postValue(null)
                })
            }
        }
    }

    fun checkPaymentResult(cartId: Int): LiveData<CheckPaymentResultResponse?> {
        if (!::serverJobs.isInitialized || !serverJobs.isActive) serverJobs = Job()
        return object : RunOnceLiveData<CheckPaymentResultResponse?>() {
            override fun onActiveRunOnce() {
                CoroutineScope(IO + serverJobs).launchApi({
                    val response = ApiService.getStoreClient().checkPaymentResult(cartId)
                    postValue(response.body())
                }, {
                    postValue(null)
                })
            }
        }
    }

    fun checkDiscountCode(discountCode: String): LiveData<CheckDiscountCodeResponse?> {
        if (!::serverJobs.isInitialized || !serverJobs.isActive) serverJobs = Job()
        return object : RunOnceLiveData<CheckDiscountCodeResponse?>() {
            override fun onActiveRunOnce() {
                CoroutineScope(IO + serverJobs).launchApi({
                    val response = ApiService.getStoreClient().checkDiscountCode(discountCode)
                    postValue(response.body())
                    //delay(1000)
                    //postValue(fakeCheckDiscountCodeResponse(false)) //todo delete this line uncomment next line
                    //todo postValue(response.body())
                }, {
                    postValue(null)
                })
            }
        }
    }

    fun getDeliveryMethods(addressId: Int): LiveData<GetDeliveryMethodsResponse?> {
        if (!::serverJobs.isInitialized || !serverJobs.isActive) serverJobs = Job()

        return object : RunOnceLiveData<GetDeliveryMethodsResponse?>() {
            override fun onActiveRunOnce() {
                CoroutineScope(IO + serverJobs).launchApi({
                    val deliveryMethodsResponse =
                        ApiService.getStoreClient().getDeliveryMethod(addressId)
                    postValue(deliveryMethodsResponse.body())
                    //todo////////////////////////// test purpose
                    /*delay(2000)
                    postValue(fakeGetDeliveryMethods(false))*/
                    //todo//////////////////////////
                }, {
                    postValue(null)
                })
            }

        }
    }
    //todo......................................................................................

    fun submitComment(request: SubmitCommentRequest): LiveData<SubmitCommentResponse?> {
        if (!::serverJobs.isInitialized || !serverJobs.isActive) serverJobs = Job()
        return object : RunOnceLiveData<SubmitCommentResponse?>() {
            override fun onActiveRunOnce() {
                CoroutineScope(IO + serverJobs).launchApi({
                    val response = ApiService.getStoreClient().submitComment(request)
                    postValue(response.body())
                }, {
                    postValue(null)
                })
            }
        }
    }

    fun changeProfile(
        nameRequest: RequestBody?,
        oldPasswordRequest: RequestBody?,
        newPasswordRequest: RequestBody?,
        imageRequest: MultipartBody.Part?,
        onResponse: (EditProfileResponse?) -> Unit
    ) {
        if (nameRequest == null && (newPasswordRequest == null || oldPasswordRequest == null) && imageRequest == null) {
            throw Throwable("all request bodies can not be null!!")
        }
        if (!::serverJobs.isInitialized || !serverJobs.isActive) serverJobs = Job()
        CoroutineScope(IO + serverJobs).launchApi({
            val response = ApiService.getStoreClient().editProfile(
                name = nameRequest,
                oldPassword = oldPasswordRequest,
                newPassword = newPasswordRequest,
                image = imageRequest
            )
            val body: EditProfileResponse? = response.body()
            if (body != null && !body.error) {
                UserConfigs.updateUser(
                    phone = body.phone,
                    name = body.name,
                    image = body.profilePic
                )
            }
            withContext(Main + serverJobs) {
                onResponse(body)
            }
        }, {
            GlobalScope.launch(Main) {
                onResponse(null)
            }
        })
    }

    //TODO using fake data
    fun getUserMessages(): LiveData<List<Message>?> {
        if (!::serverJobs.isInitialized || !serverJobs.isActive) serverJobs = Job()
        return object : RunOnceLiveData<List<Message>?>() {
            override fun onActiveRunOnce() {
                CoroutineScope(IO + serverJobs).launchApi({
                    postValue(ArrayList())
                }, {
                    postValue(null)
                })
            }

        }
    }

    fun deleteProfilePic(): LiveData<BaseServerError?> {
        if (!::serverJobs.isInitialized || !serverJobs.isActive) serverJobs = Job()
        return object : RunOnceLiveData<BaseServerError?>() {
            override fun onActiveRunOnce() {
                CoroutineScope(IO + serverJobs).launchApi({
                    val response = ApiService.getStoreClient().deleteProfilePic()
                    val body = response.body()
                    if (body != null && !body.error) {
                        UserConfigs.updateUser(image = null)
                    }
                    postValue(body)
                }, {
                    postValue(null)
                })
            }

        }
    }


}