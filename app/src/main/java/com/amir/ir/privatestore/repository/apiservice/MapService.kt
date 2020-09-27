package com.amir.ir.privatestore.repository.apiservice

import com.amir.ir.privatestore.BuildConfig
import com.amir.ir.privatestore.repository.apiservice.interceptors.NetworkConnectionInterceptor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MapService {

    private const val Base_API_URL = "https://map.ir/"
    val token by lazy { "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjJhMjdmYjNkMGE0ZGY3MzZhYTE2YmUwZjU5NWEzNDM3ZmM1NjNhZDBjZTkyZmUzNzYwZjYwYjdkYzE3MGIxMjg4YWE0NGQ1YjczMDI1YzI0In0.eyJhdWQiOiI4NzQyIiwianRpIjoiMmEyN2ZiM2QwYTRkZjczNmFhMTZiZTBmNTk1YTM0MzdmYzU2M2FkMGNlOTJmZTM3NjBmNjBiN2RjMTcwYjEyODhhYTQ0ZDViNzMwMjVjMjQiLCJpYXQiOjE1ODcwNTQxODMsIm5iZiI6MTU4NzA1NDE4MywiZXhwIjoxNTg5NjQ2MTgzLCJzdWIiOiIiLCJzY29wZXMiOlsiYmFzaWMiXX0.LLDwmxqErMEVKqBrd38ZCRgkbDxlPs8QYDQHlMlXxH_LySPV4gtZgAszulX6LJguV-J5JBXjxtci5UCJqN6nOCPdf2XQU6ByudTqJoFFCihMoVTt19r4PzsW628e0DtVGgCdGrSnQcbV8gsd2MpBd6sF37k95IVojNmxjVvfFJ96aE_oZF0ds1lsZE4F9a5FbQ5bt-sUCD6PebjcMJMlyZ1tcwDGNzOIqPbYSuMFJGEj7VOLbEhXp4u-4r5lyD5bH28wPguq90AC9woBOx9JrLA6SQ_MoGYB9p7wXSmJaw4cEtYApTmXOsc3JZaGG4Hgd_v7QSh2J4yWWeVzZz4sjw" }

    val client: MapClient by lazy {
        retrofitBuilder.build().create(MapClient::class.java)
    }

    private var internetConnectionListener: ApiService.InternetConnectionListener? = null

    private val retrofitBuilder: Retrofit.Builder by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        val client: OkHttpClient = OkHttpClient.Builder().apply {
            addNetworkInterceptor { chain ->
                val newRequest: Request = chain.request().newBuilder()
                    .removeHeader("Content-Type")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-api-key", token)
                    .build()
                chain.proceed(newRequest)
            }
            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            }
            addInterceptor(object : NetworkConnectionInterceptor() {
                override fun isInternetAvailable(): Boolean {
                    return ApiService.isNetworkConnected()
                }

                override fun onInternetUnavailable() {
                    CoroutineScope(Main).launch {
                        internetConnectionListener?.onInternetUnavailable()
                    }
                }

            })
            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                })
            }
        }.build()

        Retrofit.Builder()
            .baseUrl(Base_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
    }

    @Synchronized
    fun setInternetConnectionListener(action: ApiService.InternetConnectionListener) {
        this.internetConnectionListener = action
        println("debugt: apiServiceLevel: attached")
    }

    @Synchronized
    fun removeInternetConnectionListener(action: ApiService.InternetConnectionListener) {
        if (action == internetConnectionListener) {
            this.internetConnectionListener = null
            println("debugt: apiServiceLevel: removed")
        }
    }


}