package com.amir.ir.privatestore.utils
import androidx.lifecycle.LiveData

abstract class RunOnceLiveData<T> : LiveData<T>() {
    abstract fun onActiveRunOnce()

    private var isFirstTime = true
    override fun onActive() {
        if (isFirstTime) {
            isFirstTime = false
            onActiveRunOnce()
        }
    }

    fun activateAgain() {
        isFirstTime = true
    }
}