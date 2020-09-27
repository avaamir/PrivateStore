package com.amir.ir.privatestore.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.amir.ir.privatestore.models.requests.LoginRequest
import com.amir.ir.privatestore.models.requests.OnLoggedInResponse
import com.amir.ir.privatestore.repository.RemoteRepo

class LoginFragmentViewModel : ViewModel() {

    private val loginRequest: MutableLiveData<LoginRequest> = MutableLiveData()
    val user: LiveData<OnLoggedInResponse?> = Transformations.switchMap(loginRequest) { req ->
        RemoteRepo.login(req)
    }

    fun loginUser(phone: String, password: String) {
        val loginReq = LoginRequest(phone, password)
        loginRequest.value = loginReq
    }

}
