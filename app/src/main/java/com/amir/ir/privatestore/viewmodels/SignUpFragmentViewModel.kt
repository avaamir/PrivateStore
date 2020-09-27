package com.amir.ir.privatestore.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.amir.ir.privatestore.models.requests.SignUpRequest
import com.amir.ir.privatestore.models.requests.SignUpResponse
import com.amir.ir.privatestore.repository.RemoteRepo

class SignUpFragmentViewModel : ViewModel() {

    private val _userSignUpReq: MutableLiveData<SignUpRequest> = MutableLiveData()
    val signUpResponse: LiveData<SignUpResponse?> = Transformations.switchMap(_userSignUpReq) {
        RemoteRepo.signUpUser(it)
    }

    fun setSignUpInfo(name: String, phone: String, password: String) {
        this._userSignUpReq.value = SignUpRequest(name, phone, password)
    }

}