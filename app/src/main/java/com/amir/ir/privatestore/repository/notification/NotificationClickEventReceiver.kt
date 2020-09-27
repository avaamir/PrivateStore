package com.amir.ir.privatestore.repository.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.najva.sdk.Najva

class NotificationClickEventReceiver : BroadcastReceiver() {
    override fun onReceive(context : Context, intent : Intent) {
        println("debug: NotificationClickEventReceiver onReceive: " + intent.getIntExtra(Najva.BUTTON_ID,-1) + ", "
                + intent.getStringExtra(Najva.MESSAGE_ID))
        //todo handle notification clicks here
        //Toast.makeText(context, "Notification Clicked Broadcast", Toast.LENGTH_LONG).show()
    }
}