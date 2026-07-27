package com.gtesports.gtlive.model

data class UserProfile(
    val uid: String = "",
    val displayName: String = "GT Esports Broadcaster",
    val email: String = "broadcaster@gtesports.in",
    val photoUrl: String = "",
    val role: String = "PRO BROADCASTER",
    val youtubeChannelId: String = "",
    val totalStreams: Int = 0,
    val peakViewers: Int = 0,
    val totalWatchHours: Double = 0.0
)
