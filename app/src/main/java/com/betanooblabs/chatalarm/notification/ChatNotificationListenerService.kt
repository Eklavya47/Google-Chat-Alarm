package com.betanooblabs.chatalarm.notification

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.betanooblabs.chatalarm.alarm.ChatAlarmManager
import com.betanooblabs.chatalarm.settings.AlarmContactRepository
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
        // Temporary. This will come from app settings later.
        private const val USER_NAME = "DIPENSHU DEEP BHAT"
    }

    @Inject
    lateinit var alarmContactRepository: AlarmContactRepository

    @Inject
    lateinit var chatNotificationParser: ChatNotificationParser

    @Inject
    lateinit var chatAlarmManager: ChatAlarmManager

    private val mentionDetector = MentionDetector()

    override fun onListenerConnected() {
        super.onListenerConnected()

        Log.d(TAG, "Notification listener CONNECTED")
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
                val mentioned = mentionDetector.isMentioned(
                    message = chatNotification.message,
                    userName = USER_NAME
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

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()

        Log.d(TAG, "Notification listener DISCONNECTED")
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}