package com.betanooblabs.chatalarm.notification

class MentionDetector {

    fun isMentioned(
        message: String?,
        userName: String,
    ): Boolean {
        if (message.isNullOrBlank()) {
            return false
        }

        return message.contains(
            "@$userName",
            ignoreCase = true
        )
    }

}