package com.satguru.veritask.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.RemoteMessage
import com.satguru.veritask.DeeplinkActivity
import com.satguru.veritask.R

object NotificationHelper {

    private const val EXTRA_TITLE = "title"
    private const val EXTRA_MESSAGE = "message"
    private const val EXTRA_TYPE = "type"
    private const val EXTRA_DEAL_ID = "dealId"

    private const val TYPE_DEAL_CREATED = "dealCreated"


    private const val CHANNEL_ID = "id_deals"
    private const val CHANNEL_NAME = "Deals"
    private const val CHANNEL_DESCRIPTION = "Deals notification"

    fun sendNotification(context: Context, remoteMessage: RemoteMessage) {
        // create notification channel
        createNotificationChannel(context)

        // setup title and message
        val title = remoteMessage.data[EXTRA_TITLE].orEmpty()
        val message = remoteMessage.data[EXTRA_MESSAGE].orEmpty()

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        when (remoteMessage.data[EXTRA_TYPE].orEmpty()) {
            TYPE_DEAL_CREATED -> {
                val dealId = remoteMessage.data[EXTRA_DEAL_ID].orEmpty()
                val intent = Intent(
                    context,
                    DeeplinkActivity::class.java
                )
                intent.data = Uri.parse(DeepLinkBuilder.createDealsDetailDeepLink(dealId))
                val activity =
                    PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                builder.setContentIntent(activity)
            }

            "" -> {}
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, builder.build())
    }

    private fun createNotificationChannel(context: Context) {
        // Create Notification Channel only for Android 8.0 (Oreo) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = CHANNEL_DESCRIPTION

            // Register the channel with the system
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}