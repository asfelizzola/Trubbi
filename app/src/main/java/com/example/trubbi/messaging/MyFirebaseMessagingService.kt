package com.example.trubbi.messaging

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        Looper.prepare()

        Handler().post {
            Toast.makeText(baseContext, message.notification?.title, Toast.LENGTH_LONG ).show()
        }
        Looper.loop()
    }
}