package com.gtesports.gtlive.model

enum class OverlayType {
    WEBCAM, CAMERA_PIP, SCREEN_CAPTURE, LOGO, IMAGE, TEXT, CLOCK, DATE_TIME, WATERMARK, SUBSCRIBER_COUNTER, VIEWER_COUNTER, DONATION_GOAL, BROWSER_SOURCE, VIDEO_SOURCE, SHAPE, GIF, STICKER
}

data class OverlayItem(
    val id: String,
    val type: OverlayType,
    val name: String,
    val xRatio: Float = 0.05f,
    val yRatio: Float = 0.05f,
    val widthRatio: Float = 0.3f,
    val heightRatio: Float = 0.2f,
    val scale: Float = 1.0f,
    val rotation: Float = 0f,
    val opacity: Float = 1.0f,
    val isVisible: Boolean = true,
    val isLocked: Boolean = false,
    val textContent: String = "",
    val imageUrl: String = "",
    val zIndex: Int = 0,
    val goalCurrent: Int = 25000,
    val goalTarget: Int = 50000,
    val colorHex: String = "#FF0B3A"
)


enum class MediaSourceType {
    IMAGE_SOURCE, CAMERA_SOURCE, SCREEN_SOURCE, TEXT_SOURCE, COLOR_BACKGROUND, BROWSER_SOURCE, VIDEO_SOURCE
}

data class MediaSourceItem(
    val id: String,
    val name: String,
    val type: MediaSourceType,
    val urlOrPath: String = "",
    val colorHex: String = "#0A0A0E",
    val isMuted: Boolean = false,
    val volume: Float = 1.0f
)

enum class TransitionType {
    FADE, CUT, SLIDE, ZOOM
}

data class TransitionConfig(
    val type: TransitionType = TransitionType.FADE,
    val durationMs: Long = 300L
)

enum class LayoutPreset {
    FULL_SCREEN, PICTURE_IN_PICTURE, SIDE_BY_SIDE, SPLIT_SCREEN, CUSTOM
}

data class SceneItem(
    val id: String,
    val name: String,
    val type: SceneType,
    val isActive: Boolean = false,
    val layoutPreset: LayoutPreset = LayoutPreset.FULL_SCREEN,
    val overlays: List<OverlayItem> = emptyList(),
    val mediaSources: List<MediaSourceItem> = emptyList(),
    val backgroundColor: String = "#0A0A0E"
)

data class SceneConfiguration(
    val autoSave: Boolean = true,
    val snapToGrid: Boolean = true,
    val gridSizeDp: Int = 10,
    val transitionConfig: TransitionConfig = TransitionConfig(),
    val activeSceneId: String = "scene_camera"
)

data class LayerItem(
    val id: String,
    val name: String,
    val type: String,
    val isVisible: Boolean = true,
    val isLocked: Boolean = false,
    val zIndex: Int = 0
)

data class AudioMixerState(
    val micVolume: Float = 0.85f,
    val internalAudioVolume: Float = 0.90f,
    val isMicMuted: Boolean = false,
    val isInternalAudioMuted: Boolean = false,
    val noiseSuppression: Boolean = true,
    val echoCancellation: Boolean = true,
    val micPeakDb: Float = -12f,
    val internalPeakDb: Float = -6f
)

data class RecordingState(
    val isRecording: Boolean = false,
    val isPaused: Boolean = false,
    val durationSeconds: Long = 0,
    val format: String = "MP4",
    val savedFilePath: String = ""
)

data class StreamAnalyticsState(
    val fps: Int = 60,
    val bitrateKbps: Int = 6400,
    val cpuUsagePercent: Int = 24,
    val ramUsagePercent: Int = 38,
    val networkSpeedMbps: Float = 48.5f,
    val droppedFramesPercent: Float = 0.02f,
    val healthStatus: String = "EXCELLENT",
    val ccu: Int = 3420,
    val peakCcu: Int = 12450,
    val durationSeconds: Long = 1845
)

data class YouTubeBroadcastDetails(
    val broadcastId: String = "",
    val streamId: String = "",
    val title: String = "GT ESPORTS INDIA - BGMI Pro League Finals",
    val description: String = "Official GT Live Esports Stream. Top Indian squads competing for victory.",
    val privacy: String = "PUBLIC", // PUBLIC, UNLISTED, PRIVATE
    val category: String = "Gaming",
    val scheduledStartTime: String = "2026-07-27T18:00:00Z",
    val tags: List<String> = listOf("BGMI", "Esports", "GTLive", "Gaming", "India"),
    val thumbnailUrl: String = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=800",
    val rtmpIngestUrl: String = "rtmp://a.rtmp.youtube.com/live2",
    val streamKey: String = "gtlive-key-9921-x82"
)
