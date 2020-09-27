package com.amir.ir.privatestore.repository.sharedprefrence

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import java.util.concurrent.TimeUnit

object PrefsRepo {
    private lateinit var prefs: SharedPreferences

    private var isCartQuantityCheckedNotTimedOut = false
    private var isLastUpdateDialogShownNotTimeout = false

    fun setContext(context: Context) {
        if (!this::prefs.isInitialized) {
            prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE)
        }
    }

    private const val MY_PREFS_NAME = "prefs"
    private const val FIELD_LAST_QUANTITY_CHECKED_TIME = "last_quantity_checked_time_millis"
    private const val FIELD_LAST_UPDATE_REQ_DIALOG_TIME = "last_update_req"

    fun isCartQuantityCheckedToday(): Boolean {
        val lastCheckQuantityRequestTime = prefs.getLong(FIELD_LAST_QUANTITY_CHECKED_TIME, 0)
        if (lastCheckQuantityRequestTime != 0L) { //if time set before
            isCartQuantityCheckedNotTimedOut =
                TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - lastCheckQuantityRequestTime) < 1
        }
        return isCartQuantityCheckedNotTimedOut
    }


    fun cartQuantityChecked() {
        isCartQuantityCheckedNotTimedOut = true
        prefs.edit()
            .putLong(FIELD_LAST_QUANTITY_CHECKED_TIME, System.currentTimeMillis())
            .apply()
    }

    fun updateDialogShown() {
        isLastUpdateDialogShownNotTimeout = true
        prefs.edit()
            .putLong(FIELD_LAST_UPDATE_REQ_DIALOG_TIME, System.currentTimeMillis())
            .apply()
    }

    fun flush() {
        isCartQuantityCheckedNotTimedOut = false
        prefs.edit().clear().apply()
    }

    fun shouldUpdateDialogShown(): Boolean {
        val lastUpdateDialogShown = prefs.getLong(FIELD_LAST_UPDATE_REQ_DIALOG_TIME, 0)
        if (lastUpdateDialogShown != 0L) { //if time set before
            isLastUpdateDialogShownNotTimeout =
                TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - lastUpdateDialogShown) < 7
        }
        return !isLastUpdateDialogShownNotTimeout
    }


}