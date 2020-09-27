package com.amir.ir.privatestore.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.amir.ir.privatestore.models.Address
import com.amir.ir.privatestore.models.requests.ReverseGeo
import com.amir.ir.privatestore.models.requests.SearchAddressItem
import com.amir.ir.privatestore.repository.apiservice.MapService
import com.amir.ir.privatestore.repository.apiservice.launchApi
import com.amir.ir.privatestore.utils.RunOnceLiveData
import com.google.gson.GsonBuilder
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import java.io.IOException


class MapsActivityViewModel : ViewModel() {

    var locationFoundFlag: Boolean = false
    var isProviderEnabled: Boolean = true

    var saveAddressTextFromHere: Boolean = true

    private val activityOpenedForEdit: Boolean get() = addressFromIntent != null
    var addressFromIntent: Address? = null
        private set

    var selectedLocationMarker: Marker? = null

    var mapboxMap: MapboxMap? = null

    private var useAddressItemResult = false

    var isFirstMapClickedFlag = true
        get() {
            if (field) {
                field = false
                return true
            }
            return field
        }
    var isFirstLoadUserLocUI = true
        private set
        get() {
            if (field) {
                field = false
                return true
            }
            return field
        }

    private val _selectedLatLng = MutableLiveData(LatLng())
    val reverseGeo: LiveData<ReverseGeo?> = Transformations.switchMap(_selectedLatLng) {
        object : RunOnceLiveData<ReverseGeo?>() {
            override fun onActiveRunOnce() {
                if (it.latitude == 0.0 && it.longitude == 0.0)
                    return
                CoroutineScope(IO).launchApi({
                    val response = MapService.client.reverseGeo(
                        it.latitude.toString(),
                        it.longitude.toString()
                    )
                    if (response.isSuccessful) {
                        println("debug: reverseGeo: " + response.body().toString())
                        postValue(response.body())
                    } else {
                        val gSon = GsonBuilder().create()
                        response.errorBody()?.let { errorBody ->
                            try {
                                val geoError = gSon.fromJson(
                                    errorBody.string(),
                                    ReverseGeo::class.java
                                )
                                println("debug: reverseGeo: $geoError")
                                postValue(geoError)
                            } catch (e: IOException) {
                                println("debug: reverseGeo: ${e.message}")
                            }
                        }
                    }
                }, {
                    println("debug: reverseGeo: ${it.message}")
                    postValue(null)
                })
            }
        }
    }
    private val reverseGeoVal get() = reverseGeo.value
    private var searchAddressItem: SearchAddressItem? = null

    private val selectedLatLng get() = _selectedLatLng.value!!
    var userLocation: LatLng? = null

    var isMapMarked = false
        private set

    fun setSelectedLatLng(latLng: LatLng) {
        isMapMarked = true
        this._selectedLatLng.value = latLng
        useAddressItemResult = false
    }

    fun setAddressItem(searchAddressItem: SearchAddressItem) {
        isMapMarked = true
        this.searchAddressItem = searchAddressItem
        val coordinates = searchAddressItem.geom!!.coordinates //TODO momkene null bashe??
        selectedLatLng.latitude = coordinates[1].toDouble()
        selectedLatLng.longitude = coordinates[0].toDouble()
        useAddressItemResult = true
    }

    fun tryAgain() {
        this._selectedLatLng.value = this._selectedLatLng.value
    }

    fun setAddressFromIntent(address: Address) {
        if (!address.lat.isNullOrBlank()) {
            isMapMarked = true
        }
        this.addressFromIntent = address
    }

    fun getAddressForSubmit(): Address {
        val addressText: String
        val province: String
        val city: String

        if (saveAddressTextFromHere) {
            if (!useAddressItemResult) {
                addressText =
                    reverseGeoVal?.address_compact ?: addressFromIntent?.address
                            ?: "" //age etelat hanuz az server gerefte nashode bud hamun addressFromIntent bemune
                province =
                    reverseGeoVal?.province ?: addressFromIntent?.province ?: ""
                city = reverseGeoVal?.city ?: addressFromIntent?.city ?: ""
            } else {
                addressText = searchAddressItem!!.address
                province = searchAddressItem!!.province ?: ""
                city = searchAddressItem!!.city ?: ""
            }
        } else {
            addressText = addressFromIntent?.address ?: ""
            province = reverseGeoVal?.province ?: addressFromIntent?.province ?: ""
            city = reverseGeoVal?.city ?: addressFromIntent?.city ?: ""
        }

        return if (!activityOpenedForEdit) {
            Address(
                id = 0,
                originalTitle = null,
                ownerName = "",
                pelak = "",
                postCode = "",
                province = province,
                city = city,
                lat = selectedLatLng.latitude.toString(),
                lng = selectedLatLng.longitude.toString(),
                address = addressText
            )
        } else {
            addressFromIntent!!.copy(
                province = province,
                city = city,
                lat = selectedLatLng.latitude.toString(),
                lng = selectedLatLng.longitude.toString(),
                address = addressText
            )
        }
    }

}