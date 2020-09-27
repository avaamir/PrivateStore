package com.amir.ir.privatestore.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.amir.ir.privatestore.models.Address
import com.amir.ir.privatestore.repository.persistance.addressdb.AddressRepo

class AddressFragmentViewModel(application: Application) : AndroidViewModel(application) {

    init {
        AddressRepo.setContext(application)
    }

    val deleteAddressEvent get() = AddressRepo.insertAddressEvent

    val addresses get() = AddressRepo.allAddress


    fun deleteAddress(address: Address) {
        AddressRepo.delete(address)
    }


}

