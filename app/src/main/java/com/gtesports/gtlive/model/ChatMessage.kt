package com.gtesports.gtlive.model

data class ChatMessage(
    val id: String = "",
    val senderName: String = "",
    val senderAvatar: String = "",
    val messageText: String = "",
    val timestamp: String = "",
    val isSuperChat: Boolean = false,
    val superChatAmount: String = "",
    val isModerator: Boolean = false,
    val isVerified: Boolean = false
)
