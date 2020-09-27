package com.amir.ir.privatestore.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.amir.ir.privatestore.models.requests.GetOrdersResponse
import com.amir.ir.privatestore.repository.RemoteRepo

class TrackOrdersFragmentViewModel: ViewModel() {
    val orderList: LiveData<GetOrdersResponse?> get() =  RemoteRepo.getUserOrdersList()
}