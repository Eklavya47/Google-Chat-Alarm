package com.betanooblabs.chatalarm.notification

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatNotificationListenerService : NotificationListenerService() {

    companion object {
        private const val TAG = "ChatAlarm"
        private const val GMAIL_PACKAGE = "com.google.android.gm"
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName != GMAIL_PACKAGE) {
            return
        }

        val extras = sbn.notification.extras

        Log.d(TAG, "========== GMAIL NOTIFICATION ==========")
        Log.d(TAG, "Title: ${extras.getCharSequence("android.title")}")
        Log.d(TAG, "Text: ${extras.getCharSequence("android.text")}")
        Log.d(TAG, "BigText: ${extras.getCharSequence("android.bigText")}")
        Log.d(TAG, "Category: ${sbn.notification.category}")
        Log.d(TAG, "========================================")
    }
}