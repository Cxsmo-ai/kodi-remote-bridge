package com.kodiremote.bridge.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.kodiremote.bridge.R
import com.kodiremote.bridge.api.KodiJsonRpcClient
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Foreground service to maintain Kodi connection in background
 */
@AndroidEntryPoint
class KodiConnectionService : Service() {
    
    @Inject
    lateinit var kodiJsonRpcClient: KodiJsonRpcClient
    
    private val CHANNEL_ID = "KodiConnectionChannel"
    private val NOTIFICATION_ID = 1001
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
        
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
    override fun onDestroy() {
        super.onDestroy()
        kodiJsonRpcClient.disconnect()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Kodi Connection",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Maintains connection to Kodi media center"
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Kodi Remote Bridge")
            .setContentText("Connected to Kodi")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }
}
