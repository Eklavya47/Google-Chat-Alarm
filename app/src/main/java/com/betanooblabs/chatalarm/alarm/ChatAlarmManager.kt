package com.betanooblabs.chatalarm.alarm

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import javax.inject.Inject

class ChatAlarmManager @Inject constructor(
    private val context: Context
) {

    fun startAlarm() {
        if (AlarmSoundService.isRunning) {
            return
        }
        
        val intent = Intent(
            context,
            AlarmSoundService::class.java
        )

        ContextCompat.startForegroundService(
            context,
            intent
        )
    }

    fun stopAlarm() {
        val intent = Intent(
            context,
            AlarmSoundService::class.java
        ).apply {
            action = AlarmSoundService.ACTION_STOP_ALARM
        }

        context.startService(intent)
    }
}