package com.betanooblabs.chatalarm.notification

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatNotificationListenerService : NotificationListenerService() {

    companion object {
        private const val TAG = "ChatAlarm"
        private const val GMAIL_PACKAGE = "com.google.android.gm"
        // Temporary. This will come from app settings later.
        private const val USER_NAME = "DIPENSHU DEEP BHAT"
    }

    override fun onListenerConnected() {
        super.onListenerConnected()

        Log.d(TAG, "Notification listener CONNECTED")
    }

    @Inject
    lateinit var chatNotificationParser: ChatNotificationParser

    private val mentionDetector = MentionDetector()

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName != GMAIL_PACKAGE) {
            return
        }

        val chatNotification = chatNotificationParser.parse(sbn)
            ?: return

        when (chatNotification.type) {

            ChatType.DIRECT_MESSAGE -> {
                Log.d(TAG, "DM received")
            }

            ChatType.SPACE -> {
                val mentioned = mentionDetector.isMentioned(
                    message = chatNotification.message,
                    userName = USER_NAME
                )

                if (mentioned) {
                    Log.d(TAG, "YOU WERE MENTIONED")
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
}