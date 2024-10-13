package com.cmd.flora.utils

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.os.bundleOf
import com.cmd.flora.R
import com.cmd.flora.view.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private val self = MyFirebaseMessagingService::class.java
        val NOTIFICATION = "NOTIFICATION"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.notification?.let {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(NOTIFICATION, true)
            val bundle = bundleOf()
            Log.d("MyFirebaseMessagingService", remoteMessage.data.toString())
            remoteMessage.data.forEach { bundle.putString(it.key, it.value) }
            intent.putExtras(bundle)
            val pIntent = PendingIntent.getActivity(
                this,
                System.currentTimeMillis().toInt(),
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            NotificationUtils.notify(self, R.drawable.ic_noti, it.body, it.title, pIntent)
        }

    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)

    }
}