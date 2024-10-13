package com.cmd.flora.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.cmd.flora.R
import java.lang.ref.WeakReference
import java.util.Date

class NotificationUtils private constructor() {

    /**
     * static.
     */
    companion object {

        /**
         * context.
         */
        var weakContext: WeakReference<Context>? = null

        /**
         * builder map.
         */
        var builderMap = hashMapOf<Class<*>, NotificationCompat.Builder>()

        @SuppressLint("StaticFieldLeak")
        var manager: NotificationManagerCompat? = null

        /**
         * channel id(app name).
         */
        private var channelId: String = ""

        /**
         * channel name(app name).
         */
        private var channelName: String = ""

        /**
         * initialize.
         */
        @JvmStatic
        fun initialize(context: Context) {
            weakContext = WeakReference(context)
            manager = NotificationManagerCompat.from(context)
            channelId = context.getString(R.string.app_name)
            channelName = channelId
            // Android Oからチャンネル登録しないと通知がでません
            registerChannel()
        }

        /**
         * Android Oからチャンネル登録しないと通知がでません。
         */
        @JvmStatic
        private fun registerChannel() {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            val manager = weakContext!!.get()!!.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        /**
         * register notification.
         */
        @JvmStatic
        fun register(clazz: Class<*>, title: Int = R.string.app_name) {
            weakContext?.get()?.apply {
                builderMap[clazz] ?: run {
                    NotificationCompat.Builder(this, channelId)
                        .setContentTitle(getString(title))
                        .apply { builderMap[clazz] = this }
                }
            }
        }

        /**
         * notify.
         */
        @SuppressLint("MissingPermission")
        @JvmStatic
        fun notify(
            clazz: Class<*>,
            icon: Int?,
            text: String?,
            title: String? = null,
            pendingIntent: PendingIntent? = null
        ) {
            builderMap[clazz]?.apply {
                icon?.apply { setSmallIcon(this) }
                title?.apply { setContentTitle(this) }
                setContentText(text)
                setAutoCancel(true)
                setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE or Notification.DEFAULT_LIGHTS)
                setContentIntent(pendingIntent)
                if (weakContext?.get()?.hasPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS)) == true) {
                    val myNotificationId = Date().time.toInt()
                    manager?.notify(myNotificationId, build())
                }
            }
        }
    }
}