package com.betanooblabs.chatalarm.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.betanooblabs.chatalarm.R

class AlarmSoundService : Service() {

    companion object {
        private const val CHANNEL_ID = "chat_alarm"
        private const val NOTIFICATION_ID = 1001

        const val ACTION_STOP_ALARM =
            "com.betanooblabs.chatalarm.STOP_ALARM"

        var isRunning = false
            private set
    }

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onCreate() {
        super.onCreate()

        isRunning = true

        createNotificationChannel()

        startForeground(
            NOTIFICATION_ID,
            createNotification()
        )

        startAlarm()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        if (intent?.action == ACTION_STOP_ALARM) {
            stopAlarm()
        }

        return START_NOT_STICKY
    }

    private fun startAlarm() {
        mediaPlayer = MediaPlayer.create(
            this,
            R.raw.alarm_sound
        ).apply {
            isLooping = true

            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )

            start()
        }

        val vibratorManager =
            getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager

        vibrator = vibratorManager.defaultVibrator

        val vibrationPattern = longArrayOf(
            0,
            500,
            500
        )

        vibrator?.vibrate(
            VibrationEffect.createWaveform(
                vibrationPattern,
                0
            )
        )
    }

    private fun stopAlarm() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        vibrator?.cancel()
        vibrator = null

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotification(): Notification {
        val stopIntent = Intent(
            this,
            AlarmSoundService::class.java
        ).apply {
            action = ACTION_STOP_ALARM
        }

        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(
            this,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Chat Alarm")
            .setContentText("You received a Chat message")
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .addAction(
                0,
                "Stop Alarm",
                stopPendingIntent
            )
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Chat Alarm",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for Chat alarms"
            setSound(null, null)
        }

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null

        vibrator?.cancel()
        vibrator = null

        isRunning = false

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}