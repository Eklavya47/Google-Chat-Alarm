package com.betanooblabs.chatalarm.notification

import android.util.Log

class MentionDetector {

    fun isMentioned(
        message: String?,
        userName: String,
    ): Boolean {
        if (message.isNullOrBlank()) {
            return false
        }

        Log.d(
            "ChatAlarm",
            "MENTION CHECK | message=[$message] | username=[$userName]"
        )

        return message.contains("@$userName", ignoreCase = true) ||
                message.contains("@all", ignoreCase = true)
    }

}