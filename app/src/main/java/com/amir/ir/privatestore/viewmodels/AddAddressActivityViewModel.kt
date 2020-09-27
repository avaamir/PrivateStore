package com.amir.ir.privatestore.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amir.ir.privatestore.models.Address
import com.amir.ir.privatestore.repository.persistance.addressdb.AddressRepo

class AddAddressActivityViewModel(application: Application) : AndroidViewModel(application) {

    init {
        AddressRepo.setContext(application)
    }

    private val _address = MutableLiveData<Address>()

    val updateAddressEvent get() = AddressRepo.updateAddressEvent
    val insertAddressEvent get() = AddressRepo.insertAddressEvent

    val address: LiveData<Address> get() = _address
    var isActivityOpenedForEdit = false

    val isLatLngSet get() = !_address.value?.lat.isNullOrBlank()
    val isProvinceSet get() = !_address.value?.province.isNullOrBlank()
    val isCitySet get() = !_address.value?.city.isNullOrBlank()


    fun setAddress(address: Address, isMapResult: Boolean = false) {
        _address.value.let { lastAddress ->
            if (lastAddress != address) {
                if (isMapResult) { //latLng bayad save shavad
                    _address.value = lastAddress?.copy(
                        lat = address.lat,
                        lng = address.lng,
                        province = address.province,
                        city = address.city,
                        address = address.address
                    ) ?: address
                    println("debug: mapResult: $address")
                } else { //latLng nabayad taghir konad
                    if (lastAddress != null) { //etelate UI dare save mishe ke bere to mapActivity
                        _address.value = address.copy(
                            id = lastAddress.id,
                            lat = lastAddress.lat,
                            lng = lastAddress.lng
                        )
                        println("debug: saveUI: $lastAddress")
                    } else { //etelat az intent umade
                        _address.value = address
                        println("debug: getFromIntent: $address")
                    }/*todo dar mapActivity LatLng null mirese*/
                }
            }

        }

    }

    fun saveAddressToServerAndDB(address: Address) {
        val temp = _address.value
        if (isActivityOpenedForEdit) {
            AddressRepo.update(
                address.copy(
                    id = temp!!.id,
                    lat = temp.lat,
                    lng = temp.lng,
                    province = temp.province,
                    city = temp.city
                )
            )
        } else {
            AddressRepo.insert(
                address.copy(
                    lat = temp!!.lat,
                    lng = temp.lng,
                    province = temp.province,
                    city = temp.city
                )
            )
        }
    }


}