package com.betanooblabs.chatalarm.notification

import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.betanooblabs.chatalarm.alarm.ChatAlarmManager
import com.betanooblabs.chatalarm.settings.ChatAlarmPreferencesRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatNotificationListenerService : NotificationListenerService() {

    private val serviceScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    companion object {
        private const val TAG = "ChatAlarm"
        private const val GMAIL_PACKAGE = "com.google.android.gm"
    }

    @Inject
    lateinit var alarmContactRepository: ChatAlarmPreferencesRepository

    @Inject
    lateinit var chatNotificationParser: ChatNotificationParser

    @Inject
    lateinit var chatAlarmManager: ChatAlarmManager

    private val mentionDetector = MentionDetector()

    override fun onListenerConnected() {
        super.onListenerConnected()

        NotificationListenerStatus.setConnected(true)
        Log.d(TAG, "Notification listener CONNECTED")
    }

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "Notification listener SERVICE CREATED")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName != GMAIL_PACKAGE) {
            return
        }

        val chatNotification = chatNotificationParser.parse(sbn)
            ?: return

        when (chatNotification.type) {

            ChatType.DIRECT_MESSAGE -> {
                Log.d(TAG, "DM received")
                val senderName = chatNotification.sender ?: return

                serviceScope.launch {
                    val allowedContacts =
                        alarmContactRepository.contactNames.first()

                    if (senderName in allowedContacts) {
                        Log.d(TAG, "ALARM_ALLOWED: $senderName")
                        chatAlarmManager.startAlarm()
                    } else {
                        Log.d(TAG, "ALARM_IGNORED: $senderName")
                    }
                }
            }

            ChatType.SPACE -> {
                serviceScope.launch {
                    val userName = alarmContactRepository.userChatName.first()
                        ?: return@launch

                    val mentioned = mentionDetector.isMentioned(
                        message = chatNotification.message,
                        userName = userName
                    )

                    if (mentioned) {
                        Log.d(TAG, "YOU WERE MENTIONED")
                        chatAlarmManager.startAlarm()
                    } else {
                        Log.d(TAG, "Space message - no mention")
                    }
                }
            }
        }
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()

        NotificationListenerStatus.setConnected(false)
        Log.d(TAG, "Notification listener DISCONNECTED")
    }

    override fun onDestroy() {
        Log.d(TAG, "Notification listener SERVICE DESTROYED")

        serviceScope.cancel()

        NotificationListenerStatus.setConnected(false)

        super.onDestroy()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "Notification listener UNBOUND")
        return super.onUnbind(intent)
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "Notification listener BIND")
        return super.onBind(intent)
    }
}