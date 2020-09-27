package com.amir.ir.privatestore.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.amir.ir.privatestore.models.requests.ConfirmSignUpCodeRequest
import com.amir.ir.privatestore.models.requests.OnLoggedInResponse
import com.amir.ir.privatestore.models.requests.ReSendSMSResponse
import com.amir.ir.privatestore.repository.RemoteRepo
import com.amir.ir.privatestore.utils.DoubleTrigger
import com.amir.ir.privatestore.utils.Event

class ConfirmCodeSignUpFragmentViewModel : ViewModel() {

    var isTimerStarted: Boolean = false
    private var id: Int = 0


    private val eventSendCodeAgain = MutableLiveData<Event<Any>>()
    private val eventSendSMSAgain = MutableLiveData<Event<Any>>()

    private val confirmSignUpCodeRequest = MutableLiveData<ConfirmSignUpCodeRequest>()
    val loginResponse: LiveData<OnLoggedInResponse?> =
        Transformations.switchMap(DoubleTrigger(confirmSignUpCodeRequest, eventSendCodeAgain)) {
            val req = it.first
            RemoteRepo.confirmSignUpOTP(req!!)
        }

    val reSendSMSResponse: LiveData<ReSendSMSResponse> =
        Transformations.switchMap(eventSendSMSAgain) {
            if (id == 0)
                throw (Throwable("id can not be zero"))
            RemoteRepo.reSendSignUpSms(id)
        }

    fun sendOTP(otp: String) {
        if (id == 0)
            throw (Throwable("id can not be zero"))
        val req = ConfirmSignUpCodeRequest(id, otp)
        confirmSignUpCodeRequest.value = req
    }

    fun setId(id: Int) {
        if (id != 0)
            this.id = id
    }

    fun sendOTPAgain() {
        eventSendCodeAgain.value = Event(Unit)
    }

    fun smsOTPAgain() {
        eventSendSMSAgain.value = Event(Unit)
    }


}