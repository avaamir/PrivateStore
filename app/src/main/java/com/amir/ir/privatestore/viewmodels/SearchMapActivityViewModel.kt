package com.amir.ir.privatestore.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.amir.ir.privatestore.models.requests.SearchAddressRequest
import com.amir.ir.privatestore.models.requests.SearchAddressResponse
import com.amir.ir.privatestore.repository.apiservice.MapService
import com.amir.ir.privatestore.repository.apiservice.launchApi
import com.amir.ir.privatestore.utils.RunOnceLiveData
import com.google.gson.GsonBuilder
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.IOException

class SearchMapActivityViewModel : ViewModel() {

    var latLng: LatLng? = null

    private val searchRequest = MutableLiveData<SearchAddressRequest>()
    val searchResponse: LiveData<SearchAddressResponse?> =
        Transformations.switchMap(searchRequest) { request ->
            object : RunOnceLiveData<SearchAddressResponse?>() {
                override fun onActiveRunOnce() {
                    CoroutineScope(Dispatchers.IO).launchApi({
                        val response = MapService.client.search(request)
                        if (response.isSuccessful) {
                            println("debug: searchRequest: " + response.body().toString())
                            postValue(response.body())
                        } else {
                            val gSon = GsonBuilder().create()
                            response.errorBody()?.let { errorBody ->
                                try {
                                    val searchAddressError = gSon.fromJson(
                                        errorBody.string(), //age error dashte bashe app crush mikone
                                        SearchAddressResponse::class.java
                                    )
                                    postValue(searchAddressError)
                                } catch (e: IOException) {
                                    println("debug: searchRequest: ${e.message}")
                                }
                            }
                        }
                    }, {
                        println("debug: searchRequest: ${it.message}")
                        postValue(null)
                    })
                }
            }
        }


    fun search(text: String) {
        if (latLng == null)
            throw Throwable("can not be null at this version")
        latLng?.let { latLng ->
            searchRequest.value = SearchAddressRequest(
                text = text,
                lat = latLng.latitude,
                lng = latLng.longitude
            )
        }
    }

}