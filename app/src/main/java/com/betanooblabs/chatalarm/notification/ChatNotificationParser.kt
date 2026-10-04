package com.betanooblabs.chatalarm.notification

import android.service.notification.StatusBarNotification

class ChatNotificationParser {

    fun parse(sbn: StatusBarNotification): ChatNotification? {
        val notification = sbn.notification
        val extras = notification.extras

        if (notification.category != "msg") {
            return null
        }

        val title = extras.getCharSequence("android.title")?.toString()
        val text = extras.getCharSequence("android.text")?.toString()
        val subText = extras.getCharSequence("android.subText")?.toString()

        val type = when {
            subText?.startsWith("Chat •") == true -> {
                ChatType.DIRECT_MESSAGE
            }

            subText?.startsWith("Spaces •") == true -> {
                ChatType.SPACE
            }

            else -> {
                return null
            }
        }

        return ChatNotification(
            sender = title,
            message = text,
            type = type
        )
    }
}