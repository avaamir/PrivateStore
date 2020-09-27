package com.amir.ir.privatestore.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.amir.ir.privatestore.models.requests.ForgotPasswordResponse
import com.amir.ir.privatestore.repository.RemoteRepo

class ForgotPasswordFragmentViewModel : ViewModel() {


    private val _phone: MutableLiveData<String> = MutableLiveData()
    val phone: LiveData<String> get() = _phone

    val forgotPassResponse: LiveData<ForgotPasswordResponse?> = Transformations.switchMap(_phone) { phone ->
        RemoteRepo.forgotPasswordRequest(phone)
    }

    fun requestNewPassword(phone: String) {
        this._phone.value = phone
    }


}