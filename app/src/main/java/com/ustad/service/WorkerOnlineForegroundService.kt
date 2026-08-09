package com.ustad.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.ustad.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkerOnlineForegroundService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
            else -> {
                val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Ustad Worker Online 🟢")
                    .setContentText("You are active and receiving nearby job requests in Sahiwal.")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build()

                startForeground(NOTIFICATION_ID, notification)
                return START_STICKY
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Worker Online Service Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps worker online state active in background"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "WorkerOnlineChannel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "com.ustad.service.ACTION_START_ONLINE"
        const val ACTION_STOP = "com.ustad.service.ACTION_STOP_ONLINE"

        fun startService(context: Context) {
            val intent = Intent(context, WorkerOnlineForegroundService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, WorkerOnlineForegroundService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }
}
