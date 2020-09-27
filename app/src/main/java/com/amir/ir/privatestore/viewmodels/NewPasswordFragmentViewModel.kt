package com.amir.ir.privatestore.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.amir.ir.privatestore.repository.RemoteRepo

class NewPasswordFragmentViewModel : ViewModel() {

    private val password = MutableLiveData<String>()
    val changePasswordResponse = Transformations.switchMap(password) {
        RemoteRepo.changePassword(it)
    }



    fun changePassword(password: String) {
        this.password.value = password
    }

}