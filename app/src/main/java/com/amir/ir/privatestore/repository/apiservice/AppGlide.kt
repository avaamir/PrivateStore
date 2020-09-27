package com.amir.ir.privatestore.repository.apiservice

import android.content.Context
import com.amir.ir.privatestore.utils.prelollipop.okhttp.Tls12SocketFactory.Companion.enableTls12
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import okhttp3.OkHttpClient
import java.io.InputStream
import java.util.concurrent.TimeUnit

@GlideModule
class AppGlide : AppGlideModule() {

    @Override
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        println("debug: appGlideModule Registered")
        val client = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .enableTls12()
            .addInterceptor {
                println("debugfuck: GLide is fucking Run")
                it.proceed(it.request())
            }
            .build()

        val factory = OkHttpUrlLoader.Factory(client)
        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            factory
        )
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

}