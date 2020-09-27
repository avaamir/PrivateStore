package com.amir.ir.privatestore.repository.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.najva.sdk.Najva


class NotificationEventReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        println("debug: NotificationEventReceiver: onReceive: " + intent.getStringExtra(Najva.JSON_DATA))
        // You can interactive with google analyitcs here
        //todo handle messages here
        //Toast.makeText(context, "Notification Received Broadcast", Toast.LENGTH_LONG).show()
    }
}