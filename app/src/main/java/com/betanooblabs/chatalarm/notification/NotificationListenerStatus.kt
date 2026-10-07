package com.betanooblabs.chatalarm.notification

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object NotificationListenerStatus {

    private val _isConnected = MutableStateFlow(false)

    val isConnected: StateFlow<Boolean> =
        _isConnected.asStateFlow()

    fun isListenerEnabled(context: Context): Boolean {
        val componentName = ComponentName(
            context,
            ChatNotificationListenerService::class.java
        )

        return NotificationManagerCompat
            .getEnabledListenerPackages(context)
            .contains(componentName.packageName)
    }

    fun setConnected(connected: Boolean) {
        _isConnected.value = connected
    }

    fun openSettings(context: Context) {
        val intent = Intent(
            Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
        )

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    fun requestRebind(context: Context) {
        if (!isListenerEnabled(context)) {
            return
        }

        val componentName = ComponentName(
            context,
            ChatNotificationListenerService::class.java
        )

        NotificationListenerService.requestRebind(componentName)

        Log.d(
            "ChatAlarm",
            "Requested notification listener rebind"
        )
    }
}