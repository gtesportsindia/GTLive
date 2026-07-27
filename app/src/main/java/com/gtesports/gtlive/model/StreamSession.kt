package com.gtesports.gtlive.model

data class StreamSession(
    val id: String = "",
    val title: String = "GT ESPORTS INDIA - BGMI Pro League Finals",
    val description: String = "Official GT Live Esports Stream. Top Indian squads competing for victory.",
    val category: String = "BGMI Esports",
    val quality: String = "1080p60", // 720p or 1080p
    val streamType: String = "CAMERA", // CAMERA or SCREEN
    val isLive: Boolean = false,
    val currentViewers: Int = 0,
    val peakViewers: Int = 0,
    val fps: Int = 60,
    val bitrateKbps: Int = 6000,
    val startTime: Long = System.currentTimeMillis(),
    val youtubeStreamId: String = "",
    val youtubeRtmpUrl: String = "rtmp://a.rtmp.youtube.com/live2"
)
