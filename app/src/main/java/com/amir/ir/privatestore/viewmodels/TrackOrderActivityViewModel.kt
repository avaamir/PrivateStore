package com.amir.ir.privatestore.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.amir.ir.privatestore.models.Order
import com.amir.ir.privatestore.models.requests.GetOrderDetailsResponse
import com.amir.ir.privatestore.repository.RemoteRepo
import com.amir.ir.privatestore.utils.DoubleTrigger
import com.amir.ir.privatestore.utils.Event

class TrackOrderActivityViewModel : ViewModel() {
    lateinit var order: Order
        private set

    private val tryAgainEvent: MutableLiveData<Event<Any>> = MutableLiveData()

    private val orderId: MutableLiveData<Int> = MutableLiveData()
    val orderPageResponse: LiveData<GetOrderDetailsResponse?> =
        Transformations.switchMap(DoubleTrigger(orderId, tryAgainEvent)) {
            RemoteRepo.getOderDetails(it.first!!)
        }

    fun setOrder(order: Order) {
        if (this.orderId.value != order.id) {
            this.order = order
            this.orderId.value = order.id
        }
    }

    fun tryAgain() {
        tryAgainEvent.value = Event(Unit)
    }

}