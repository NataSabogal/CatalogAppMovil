package com.example.catalogapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.catalogapp.data.network.RetrofitClient
import com.example.catalogapp.data.repository.ProductRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.distinctUntilChanged

class DownloadService : Service() {
    private val notificationId = 101
    private val channelId = "download_channel_v2"
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val repository = ProductRepository(RetrofitClient.apiService)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        val notification = createNotification(0)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(notificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(notificationId, notification)
        }

        serviceScope.launch {
            repository.syncCatalogProgress()
                .distinctUntilChanged()
                .collect { progress ->
                    updateNotification(progress)
                }

            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
        return START_NOT_STICKY
    }

    private fun updateNotification(progress: Int) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(notificationId, createNotification(progress))
    }

    private fun createNotification(progress: Int): Notification {
        val title = "Sincronizando Catálogo"
        val content = if (progress < 100) "Progreso: $progress%" else "Sincronización completa"

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(100, progress, false)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Sincronización de Datos",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
