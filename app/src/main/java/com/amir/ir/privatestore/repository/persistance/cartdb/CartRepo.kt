package com.amir.ir.privatestore.repository.persistance.cartdb

import android.content.Context
import androidx.lifecycle.LiveData
import com.amir.ir.privatestore.models.CartItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object CartRepo {
    private lateinit var job: Job
    private lateinit var context: Context

    private val cartDao: CartDao by lazy {
        CartDatabase.getInstance(context).getDao()
    }


    fun setContext(context: Context) {
        CartRepo.context = context.applicationContext
    }

    val allCartItem: LiveData<List<CartItem>> by lazy {
        cartDao.allCartItem
    }

    fun insert(items: List<CartItem>) {
        if (!CartRepo::job.isInitialized || !job.isActive)
            job = Job()
        CoroutineScope(IO + job).launch {
            cartDao.insertAll(items)
        }
    }

    fun insert(item: CartItem, onResponse: (() -> Unit)? = null) {
        if (!CartRepo::job.isInitialized || !job.isActive)
            job = Job()
        CoroutineScope(IO + job).launch {
            cartDao.insert(item)
            onResponse?.let {
                withContext(Main + job) {
                    onResponse.invoke()
                }
            }
        }
    }

    fun update(item: CartItem, onResponse: (() -> Unit)? = null) {
        if (!CartRepo::job.isInitialized || !job.isActive)
            job = Job()
        CoroutineScope(IO + job).launch {
            cartDao.update(item)
            onResponse?.let {
                withContext(Main + job) {
                    onResponse.invoke()
                }
            }
        }
    }

    fun delete(item: CartItem) {
        if (!CartRepo::job.isInitialized || !job.isActive)
            job = Job()
        CoroutineScope(IO + job).launch {
            cartDao.delete(item)
        }
    }

    fun deleteAllCartItem() {
        if (!CartRepo::job.isInitialized || !job.isActive)
            job = Job()
        CoroutineScope(IO + job).launch {
            cartDao.deleteAllCartItem()
        }
    }

    fun exist(pid: Int, sizeId: Int, colorId: Int, onResponse: (CartItem?) -> Unit) {
        if (!CartRepo::job.isInitialized || !job.isActive)
            job = Job()
        CoroutineScope(IO + job).launch {
            val cartItem = cartDao.exists(pid, sizeId, colorId)
            withContext(Main) {
                onResponse(cartItem)
            }
        }
    }

    fun cancelJobs() {
        if (CartRepo::job.isInitialized && job.isActive)
            job.cancel()
    }
}