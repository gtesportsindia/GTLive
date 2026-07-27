package com.gtesports.gtlive.model

data class StreamSettings(
    val resolution: String = "1080p", // 720p or 1080p
    val targetFps: Int = 60,
    val targetBitrateKbps: Int = 6000,
    val isMicMuted: Boolean = false,
    val isFrontCamera: Boolean = true,
    val audioSource: String = "Mic + Internal Audio",
    val youtubePrivacy: String = "PUBLIC", // PUBLIC, UNLISTED, PRIVATE
    val latencyMode: String = "ULTRA_LOW",
    val noiseSuppression: Boolean = true,
    val autoReconnect: Boolean = true
)
