package com.replica

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class ReplicaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val downloadChannel = NotificationChannel(
                DOWNLOAD_CHANNEL_ID,
                "Download Manager",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifies download progress and status for media files"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(downloadChannel)
        }
    }

    companion object {
        const val DOWNLOAD_CHANNEL_ID = "download_channel"
    }
}
