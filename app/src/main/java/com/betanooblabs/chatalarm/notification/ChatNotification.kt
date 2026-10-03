package com.betanooblabs.chatalarm.notification

data class ChatNotification(
    val sender: String?,
    val message: String?,
    val type: ChatType,
)

enum class ChatType {
    DIRECT_MESSAGE,
    SPACE,
}
