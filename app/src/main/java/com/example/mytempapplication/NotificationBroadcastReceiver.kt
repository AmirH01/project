package com.example.mytempapplication

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat

val channelID = "channel1"
val medicationNameExtra = "medicationNameExtra"
val frequencyExtra = "frequencyExtra"
val descriptionExtra = "descriptionExtra"

class NotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(intent?.getStringExtra(medicationNameExtra))
            .setContentText(intent?.getStringExtra(descriptionExtra))
            .build()

        val  manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification)
    }

    companion object {
        private var notificationID = 0
        @JvmStatic
        fun getAndIncrementNotificationId(): Int {
            notificationID++
            Log.e("NotificationID","$notificationID")
            return notificationID
        }
    }
}