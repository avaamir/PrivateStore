package com.amir.ir.privatestore.repository.apiservice

import android.content.Context
import android.net.ConnectivityManager
import com.amir.ir.privatestore.BuildConfig
import com.amir.ir.privatestore.UserConfigs
import com.amir.ir.privatestore.repository.apiservice.interceptors.AuthCookieInterceptor
import com.amir.ir.privatestore.repository.apiservice.interceptors.NetworkConnectionInterceptor
import com.amir.ir.privatestore.repository.apiservice.interceptors.UnauthorizedInterceptor
import com.amir.ir.privatestore.utils.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiService {
    const val Domain = "https://myshopland.ir"
    private const val Base_API_URL = "$Domain/api/"


    private lateinit var context: Context

    private var event = Event(Unit)
    private var onUnauthorizedListener: OnUnauthorizedListener? = null
    private var internetConnectionListener: InternetConnectionListener? = null

    private var token: String? = null

    @Volatile
    private var mStoreClient: StoreClient? = null

    fun getStoreClient(): StoreClient =
        mStoreClient ?: synchronized(this) {
            mStoreClient ?: retrofitBuilder.build().create(StoreClient::class.java)
                .also { mStoreClient = it }
        }

    private val retrofitBuilder: Retrofit.Builder by lazy {

        val client: OkHttpClient = OkHttpClient.Builder()
            //UnsafeOkHttpClient.getUnsafeOkHttpClientBuilder()
            .apply {
                addInterceptor { chain ->
                    val builder = chain.request().newBuilder()
                        .addHeader("Accept", "application/json")
                        .addHeader("Content-Type", "application/json")
                        .addHeader("rest-api-key", "5082e90afb3aeaffd20a4f8960e69e07")

                    token?.let {
                        builder.addHeader("Authorization", "Bearer $it")
                    }
                    val newRequest: Request = builder.build()
                    chain.proceed(newRequest)
                }

                addInterceptor(AuthCookieInterceptor())
                addInterceptor(object : UnauthorizedInterceptor() {
                    override fun onUnauthorized() {
                        if (UserConfigs.isLoggedIn) {
                            UserConfigs.logout()
                            event = Event(Unit)
                        }
                        this@ApiService.onUnauthorizedListener?.onUnauthorizedAction(event)
                    }
                })
                addInterceptor(object : NetworkConnectionInterceptor() {
                    override fun isInternetAvailable(): Boolean {
                        return isNetworkConnected()
                    }

                    override fun onInternetUnavailable() {
                        CoroutineScope(Main).launch {
                            internetConnectionListener?.onInternetUnavailable()
                        }
                    }

                })

                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                }
                //////////////todo///////////////////
                //enableTls12()
            }.build()

        Retrofit.Builder()
            .baseUrl(Base_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
    }

    @Synchronized
    fun setBearerToken(token: String) {
        this.token = token
    }

    @Synchronized
    fun setOnUnauthorizedAction(action: OnUnauthorizedListener) {
        this.onUnauthorizedListener = action
    }

    @Synchronized
    fun removeUnauthorizedAction(action: OnUnauthorizedListener) {
        if (action == onUnauthorizedListener)
            onUnauthorizedListener = null
    }

    @Synchronized
    fun setInternetConnectionListener(action: InternetConnectionListener) {
        this.internetConnectionListener = action
    }

    @Synchronized
    fun removeInternetConnectionListener(action: InternetConnectionListener) {
        if (action == internetConnectionListener) {
            this.internetConnectionListener = null
        }
    }


    fun isNetworkConnected(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo.isConnected
    }


    fun setContext(context: Context) {
        this.context = context.applicationContext
    }

    interface OnUnauthorizedListener {
        fun onUnauthorizedAction(event: Event<Unit>)
    }

    interface InternetConnectionListener {
        fun onInternetUnavailable()
        //fun onCacheUnavailable()
    }


}