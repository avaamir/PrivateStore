package com.amir.ir.privatestore.repository.apiservice

import com.amir.ir.privatestore.models.requests.ReverseGeo
import com.amir.ir.privatestore.models.requests.SearchAddressRequest
import com.amir.ir.privatestore.models.requests.SearchAddressResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MapClient {
    @GET("fast-reverse")
    suspend fun reverseGeo(
        @Query("lat") lat: String,
        @Query("lon") lng: String
    ): Response<ReverseGeo>

    @POST("search/v2/autocomplete")
    suspend fun search(@Body searchAddressRequest: SearchAddressRequest): Response<SearchAddressResponse>
}