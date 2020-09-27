package com.amir.ir.privatestore.repository.notification.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.najva.sdk.push_notification.NajvaPushNotificationHandler

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        println("debug: fcm: sender:${message.from}")
        println("debug: fcm:   data:${message.data}")
        println("debug: fcm:   json:${message.data["json"]}")
        println("debug: fcm: notifi:${message.notification?.body}")

        if (NajvaPushNotificationHandler.isNajvaMessage(applicationContext, message)) {
            NajvaPushNotificationHandler.handleMessage(applicationContext,message)
        }
        // handel other push notifications here

    }

    override fun onNewToken(token: String) {
        println("debug: fcm: newToken=$token")
        NajvaPushNotificationHandler.handleNewToken(applicationContext)
    }
}