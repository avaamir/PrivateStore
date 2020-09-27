package com.amir.ir.privatestore.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.amir.ir.privatestore.models.requests.ForgotPasswordRequest
import com.amir.ir.privatestore.models.requests.OnLoggedInResponse
import com.amir.ir.privatestore.models.requests.ReSendSMSResponse
import com.amir.ir.privatestore.repository.RemoteRepo
import com.amir.ir.privatestore.utils.DoubleTrigger
import com.amir.ir.privatestore.utils.Event

class ConfirmCodeForgotFragmentViewModel : ViewModel() {
    var isTimerStarted: Boolean = false
    private lateinit var phone: String


    private val eventSendCodeAgain = MutableLiveData<Event<Any>>()
    private val eventSendSMSAgain = MutableLiveData<Event<Any>>()

    private val confirmSignUpCodeRequest = MutableLiveData<ForgotPasswordRequest>()
    val loginResponse: LiveData<OnLoggedInResponse?> =
        Transformations.switchMap(DoubleTrigger(confirmSignUpCodeRequest, eventSendCodeAgain)) {
            val req = it.first
            RemoteRepo.confirmForgotOTP(req!!)
        }

    val reSendSMSResponse: LiveData<ReSendSMSResponse?> =
        Transformations.switchMap(eventSendSMSAgain) {
            RemoteRepo.reSendForgotSms(phone)
        }

    fun sendOTP(otp: String) {
        val req = ForgotPasswordRequest(phone, otp)
        confirmSignUpCodeRequest.value = req
    }

    fun setPhone(phone: String?) {
        phone?.let {
            this.phone = phone
        }
    }

    fun sendOTPAgain() {
        eventSendCodeAgain.value = Event(Unit)
    }

    fun smsOTPAgain() {
        eventSendSMSAgain.value = Event(Unit)
    }

}