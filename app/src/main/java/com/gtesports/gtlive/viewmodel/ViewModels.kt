package com.gtesports.gtlive.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtesports.gtlive.data.ScenePersistenceManager
import com.gtesports.gtlive.model.AudioMixerState
import com.gtesports.gtlive.model.ChatMessage
import com.gtesports.gtlive.model.LayerItem
import com.gtesports.gtlive.model.LayoutPreset
import com.gtesports.gtlive.model.MediaSourceItem
import com.gtesports.gtlive.model.MediaSourceType
import com.gtesports.gtlive.model.OverlayItem
import com.gtesports.gtlive.model.OverlayType
import com.gtesports.gtlive.model.RecordingState
import com.gtesports.gtlive.model.SceneConfiguration
import com.gtesports.gtlive.model.SceneItem
import com.gtesports.gtlive.model.SceneType
import com.gtesports.gtlive.model.StreamAnalyticsState
import com.gtesports.gtlive.model.StreamSession
import com.gtesports.gtlive.model.StreamSettings
import com.gtesports.gtlive.model.TransitionConfig
import com.gtesports.gtlive.model.TransitionType
import com.gtesports.gtlive.model.UserProfile
import com.gtesports.gtlive.model.YouTubeBroadcastDetails
import com.gtesports.gtlive.youtube.YouTubeLiveService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _userProfile = MutableStateFlow(
        UserProfile(
            uid = "gt_user_88",
            displayName = "GT Pro Broadcaster",
            email = "broadcaster@gtesports.in",
            photoUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200",
            role = "HEAD OF BROADCASTING",
            youtubeChannelId = "UC_GTEsportsOfficialIndia"
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile

    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _googleOAuthToken = MutableStateFlow("oauth_yt_token_889102")
    val googleOAuthToken: StateFlow<String> = _googleOAuthToken

    fun loginWithGoogle() {
        _isLoggedIn.value = true
        _googleOAuthToken.value = "oauth_yt_token_" + System.currentTimeMillis()
    }

    fun loginWithEmail(email: String, pass: String) {
        _isLoggedIn.value = true
    }

    fun logout() {
        _isLoggedIn.value = false
        _googleOAuthToken.value = ""
    }
}

class StreamViewModel : ViewModel() {
    private val youtubeService = YouTubeLiveService()

    private val _broadcastDetails = MutableStateFlow(YouTubeBroadcastDetails())
    val broadcastDetails: StateFlow<YouTubeBroadcastDetails> = _broadcastDetails

    private val _streamSession = MutableStateFlow(StreamSession())
    val streamSession: StateFlow<StreamSession> = _streamSession

    private val _streamSettings = MutableStateFlow(StreamSettings())
    val streamSettings: StateFlow<StreamSettings> = _streamSettings

    // Real YouTube Live Chat
    private val _liveChatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("1", "Mortal_Fan", "", "GT Esports squad looking super strong today!", "10:14 AM"),
            ChatMessage("2", "Scout_Official", "", "1080p 60FPS stream quality is ultra crisp 🔥", "10:15 AM", isVerified = true),
            ChatMessage("3", "GT_Official", "", "Welcome all viewers! Type GG in chat to support India!", "10:15 AM", isModerator = true, isVerified = true),
            ChatMessage("4", "Raj_Gaming", "", "Superchat ₹500 from Raj! Keep rockin guys!", "10:16 AM", isSuperChat = true, superChatAmount = "₹500"),
            ChatMessage("5", "Viper_Esports", "", "BGMI Finals Day 3 map 1 Pochinki drop!", "10:16 AM")
        )
    )
    val liveChatMessages: StateFlow<List<ChatMessage>> = _liveChatMessages

    // Scenes & Overlays State
    private val defaultCameraOverlays = listOf(
        OverlayItem("ov_cam", OverlayType.WEBCAM, "Webcam Studio Frame", xRatio = 0.05f, yRatio = 0.55f, scale = 1.0f, opacity = 1.0f, zIndex = 2),
        OverlayItem("ov_logo", OverlayType.LOGO, "GT Esports Watermark", xRatio = 0.80f, yRatio = 0.05f, scale = 1.2f, opacity = 0.95f, zIndex = 3),
        OverlayItem("ov_text", OverlayType.TEXT, "Tournament Ticker", xRatio = 0.05f, yRatio = 0.90f, textContent = "LIVE: GT ESPORTS BGMI NATIONAL FINALS DAY 3", zIndex = 1),
        OverlayItem("ov_sub", OverlayType.SUBSCRIBER_COUNTER, "Sub Counter", xRatio = 0.05f, yRatio = 0.05f, goalCurrent = 125000, zIndex = 4),
        OverlayItem("ov_dt", OverlayType.DATE_TIME, "Live Clock", xRatio = 0.80f, yRatio = 0.90f, zIndex = 5)
    )

    private val defaultMediaSources = listOf(
        MediaSourceItem("ms_cam", "Main Camera Feed", MediaSourceType.CAMERA_SOURCE),
        MediaSourceItem("ms_logo", "GT Esports Watermark PNG", MediaSourceType.IMAGE_SOURCE, urlOrPath = "gt_logo.png"),
        MediaSourceItem("ms_bg", "Cyberpunk Black BG", MediaSourceType.COLOR_BACKGROUND, colorHex = "#0A0A0E")
    )

    private val _scenes = MutableStateFlow<List<SceneItem>>(
        listOf(
            SceneItem("scene_camera", "MAIN CAMERA STUDIO", SceneType.CAMERA, isActive = true, layoutPreset = LayoutPreset.FULL_SCREEN, overlays = defaultCameraOverlays, mediaSources = defaultMediaSources),
            SceneItem("scene_screen", "GAMEPLAY SCREEN CAPTURE", SceneType.SCREEN, layoutPreset = LayoutPreset.PICTURE_IN_PICTURE, overlays = defaultCameraOverlays.take(3), mediaSources = listOf(MediaSourceItem("ms_screen", "Game Screen Capture", MediaSourceType.SCREEN_SOURCE))),
            SceneItem("scene_brb", "BE RIGHT BACK", SceneType.BRB, layoutPreset = LayoutPreset.FULL_SCREEN, overlays = listOf(OverlayItem("ov_brb", OverlayType.TEXT, "BRB Notice", xRatio = 0.25f, yRatio = 0.45f, textContent = "BE RIGHT BACK - GT ESPORTS STREAM")), mediaSources = listOf(MediaSourceItem("ms_brb_bg", "BRB Background", MediaSourceType.COLOR_BACKGROUND, colorHex = "#16161E"))),
            SceneItem("scene_starting", "STARTING SOON", SceneType.STARTING_SOON, layoutPreset = LayoutPreset.FULL_SCREEN, overlays = listOf(OverlayItem("ov_start", OverlayType.TEXT, "Start Ticker", xRatio = 0.20f, yRatio = 0.45f, textContent = "STREAM STARTING SOON - GET READY!")), mediaSources = listOf(MediaSourceItem("ms_start_bg", "Starting Canvas", MediaSourceType.COLOR_BACKGROUND, colorHex = "#101015"))),
            SceneItem("scene_ending", "ENDING SOON", SceneType.ENDING_SOON, layoutPreset = LayoutPreset.FULL_SCREEN, overlays = listOf(OverlayItem("ov_end", OverlayType.TEXT, "Outro Text", xRatio = 0.25f, yRatio = 0.45f, textContent = "THANKS FOR WATCHING! GG!")), mediaSources = listOf(MediaSourceItem("ms_end_bg", "Ending Canvas", MediaSourceType.COLOR_BACKGROUND, colorHex = "#0D0D12")))
        )
    )
    val scenes: StateFlow<List<SceneItem>> = _scenes

    private val _sceneConfig = MutableStateFlow(SceneConfiguration())
    val sceneConfig: StateFlow<SceneConfiguration> = _sceneConfig

    private val _selectedOverlayId = MutableStateFlow<String?>(defaultCameraOverlays.firstOrNull()?.id)
    val selectedOverlayId: StateFlow<String?> = _selectedOverlayId

    private val _overlays = MutableStateFlow<List<OverlayItem>>(defaultCameraOverlays)
    val overlays: StateFlow<List<OverlayItem>> = _overlays

    // Layer Manager
    private val _layers = MutableStateFlow<List<LayerItem>>(
        listOf(
            LayerItem("lay_1", "GT Esports Watermark Logo", "LOGO", isVisible = true, isLocked = true, zIndex = 3),
            LayerItem("lay_2", "Lower Third Tournament Ticker", "TEXT", isVisible = true, isLocked = false, zIndex = 2),
            LayerItem("lay_3", "Facecam Studio Border", "WEBCAM", isVisible = true, isLocked = false, zIndex = 1)
        )
    )
    val layers: StateFlow<List<LayerItem>> = _layers

    // Audio Mixer State
    private val _audioMixer = MutableStateFlow(AudioMixerState())
    val audioMixer: StateFlow<AudioMixerState> = _audioMixer

    // Recording State
    private val _recordingState = MutableStateFlow(RecordingState())
    val recordingState: StateFlow<RecordingState> = _recordingState

    // Analytics State
    private val _analytics = MutableStateFlow(StreamAnalyticsState())
    val analytics: StateFlow<StreamAnalyticsState> = _analytics

    // Local SharedPreferences Persistence
    private var appContext: Context? = null

    fun initStorage(context: Context) {
        appContext = context.applicationContext
        val loaded = ScenePersistenceManager.loadScenes(context)
        if (loaded != null) {
            _scenes.value = loaded.first
            val activeId = loaded.second
            _sceneConfig.value = _sceneConfig.value.copy(activeSceneId = activeId)
            val activeScene = loaded.first.find { it.id == activeId } ?: loaded.first.firstOrNull()
            if (activeScene != null) {
                _overlays.value = activeScene.overlays
                _selectedOverlayId.value = activeScene.overlays.firstOrNull()?.id
            }
        }
    }

    private fun autoSave() {
        val ctx = appContext ?: return
        ScenePersistenceManager.saveScenes(ctx, _scenes.value, _sceneConfig.value.activeSceneId)
    }

    // Broadcast Details
    fun updateBroadcastDetails(
        title: String,
        desc: String,
        privacy: String,
        category: String,
        scheduledTime: String,
        tagsStr: String
    ) {
        val tagsList = tagsStr.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        _broadcastDetails.value = _broadcastDetails.value.copy(
            title = title,
            description = desc,
            privacy = privacy,
            category = category,
            scheduledStartTime = scheduledTime,
            tags = if (tagsList.isNotEmpty()) tagsList else _broadcastDetails.value.tags
        )
    }

    fun sendChatMessage(text: String) {
        if (text.trim().isEmpty()) return
        val newMsg = ChatMessage(
            id = "chat_${System.currentTimeMillis()}",
            senderName = "GT Pro Broadcaster",
            messageText = text,
            timestamp = "JUST NOW",
            isModerator = true,
            isVerified = true
        )
        _liveChatMessages.value = _liveChatMessages.value + newMsg
    }

    // SCENE EDITOR METHODS
    fun selectScene(sceneId: String) {
        _scenes.value = _scenes.value.map {
            it.copy(isActive = (it.id == sceneId))
        }
        val currentScene = _scenes.value.find { it.id == sceneId }
        if (currentScene != null) {
            _overlays.value = currentScene.overlays
            _selectedOverlayId.value = currentScene.overlays.firstOrNull()?.id
            _sceneConfig.value = _sceneConfig.value.copy(activeSceneId = sceneId)
        }
    }

    fun createScene(name: String, type: SceneType) {
        val newSceneId = "scene_${System.currentTimeMillis()}"
        val newScene = SceneItem(
            id = newSceneId,
            name = name.ifEmpty { "CUSTOM SCENE ${_scenes.value.size + 1}" },
            type = type,
            isActive = false,
            layoutPreset = LayoutPreset.FULL_SCREEN,
            overlays = listOf(
                OverlayItem("ov_${System.currentTimeMillis()}", OverlayType.TEXT, "Scene Header", xRatio = 0.05f, yRatio = 0.05f, textContent = name)
            ),
            mediaSources = listOf(
                MediaSourceItem("ms_${System.currentTimeMillis()}", "Base Source", MediaSourceType.COLOR_BACKGROUND)
            )
        )
        _scenes.value = _scenes.value + newScene
        selectScene(newSceneId)
    }

    fun renameScene(sceneId: String, newName: String) {
        _scenes.value = _scenes.value.map {
            if (it.id == sceneId) it.copy(name = newName) else it
        }
    }

    fun deleteScene(sceneId: String) {
        if (_scenes.value.size <= 1) return // Keep at least one scene
        val list = _scenes.value.filterNot { it.id == sceneId }
        _scenes.value = list
        if (_sceneConfig.value.activeSceneId == sceneId) {
            selectScene(list.first().id)
        }
    }

    fun duplicateScene(sceneId: String) {
        val target = _scenes.value.find { it.id == sceneId } ?: return
        val dupId = "scene_${System.currentTimeMillis()}"
        val duplicated = target.copy(
            id = dupId,
            name = "${target.name} (COPY)",
            isActive = false,
            overlays = target.overlays.map { it.copy(id = "ov_${System.currentTimeMillis()}_${(0..999).random()}") }
        )
        _scenes.value = _scenes.value + duplicated
        selectScene(dupId)
    }

    fun updateLayoutPreset(sceneId: String, preset: LayoutPreset) {
        _scenes.value = _scenes.value.map {
            if (it.id == sceneId) it.copy(layoutPreset = preset) else it
        }
    }

    fun updateTransitionConfig(type: TransitionType, durationMs: Long) {
        _sceneConfig.value = _sceneConfig.value.copy(
            transitionConfig = TransitionConfig(type, durationMs)
        )
    }

    fun toggleSnapToGrid() {
        _sceneConfig.value = _sceneConfig.value.copy(
            snapToGrid = !_sceneConfig.value.snapToGrid
        )
    }

    fun toggleAutoSave() {
        _sceneConfig.value = _sceneConfig.value.copy(
            autoSave = !_sceneConfig.value.autoSave
        )
    }

    // OVERLAY EDITOR METHODS
    fun selectOverlay(overlayId: String?) {
        _selectedOverlayId.value = overlayId
    }

    fun addOverlay(type: OverlayType, name: String, textContent: String = "") {
        val newOverlayId = "ov_${System.currentTimeMillis()}"
        val defaultText = when (type) {
            OverlayType.TEXT -> textContent.ifEmpty { "GT ESPORTS LIVE TICKER" }
            OverlayType.DATE_TIME -> "LIVE 2026-07-27 18:30 IST"
            OverlayType.WATERMARK -> "GT LIVE OFFICIAL"
            OverlayType.SUBSCRIBER_COUNTER -> "SUBS: 125,400"
            OverlayType.VIEWER_COUNTER -> "VIEWERS: 3,420"
            OverlayType.DONATION_GOAL -> "GOAL: ₹25,000 / ₹50,000"
            else -> name
        }

        val newOverlay = OverlayItem(
            id = newOverlayId,
            type = type,
            name = name,
            xRatio = 0.20f,
            yRatio = 0.20f,
            scale = 1.0f,
            rotation = 0f,
            opacity = 1.0f,
            isVisible = true,
            isLocked = false,
            textContent = defaultText,
            zIndex = (_overlays.value.maxOfOrNull { it.zIndex } ?: 0) + 1
        )

        val updatedOverlays = _overlays.value + newOverlay
        _overlays.value = updatedOverlays
        _selectedOverlayId.value = newOverlayId
        syncActiveSceneOverlays(updatedOverlays)
    }

    fun updateOverlayPosition(overlayId: String, xRatio: Float, yRatio: Float) {
        val snap = _sceneConfig.value.snapToGrid
        val snappedX = if (snap) (xRatio * 20).toInt() / 20f else xRatio
        val snappedY = if (snap) (yRatio * 20).toInt() / 20f else yRatio

        val updated = _overlays.value.map {
            if (it.id == overlayId && !it.isLocked) {
                it.copy(xRatio = snappedX.coerceIn(0f, 0.95f), yRatio = snappedY.coerceIn(0f, 0.95f))
            } else it
        }
        _overlays.value = updated
        syncActiveSceneOverlays(updated)
    }

    fun updateOverlayScale(overlayId: String, scale: Float) {
        val updated = _overlays.value.map {
            if (it.id == overlayId && !it.isLocked) it.copy(scale = scale.coerceIn(0.2f, 3.0f)) else it
        }
        _overlays.value = updated
        syncActiveSceneOverlays(updated)
    }

    fun updateOverlayRotation(overlayId: String, rotation: Float) {
        val updated = _overlays.value.map {
            if (it.id == overlayId && !it.isLocked) it.copy(rotation = rotation) else it
        }
        _overlays.value = updated
        syncActiveSceneOverlays(updated)
    }

    fun updateOverlayOpacity(overlayId: String, opacity: Float) {
        val updated = _overlays.value.map {
            if (it.id == overlayId) it.copy(opacity = opacity.coerceIn(0f, 1f)) else it
        }
        _overlays.value = updated
        syncActiveSceneOverlays(updated)
    }

    fun toggleOverlayVisibility(overlayId: String) {
        val updated = _overlays.value.map {
            if (it.id == overlayId) it.copy(isVisible = !it.isVisible) else it
        }
        _overlays.value = updated
        syncActiveSceneOverlays(updated)
    }

    fun toggleOverlayLock(overlayId: String) {
        val updated = _overlays.value.map {
            if (it.id == overlayId) it.copy(isLocked = !it.isLocked) else it
        }
        _overlays.value = updated
        syncActiveSceneOverlays(updated)
    }

    fun duplicateOverlay(overlayId: String) {
        val target = _overlays.value.find { it.id == overlayId } ?: return
        val dupId = "ov_${System.currentTimeMillis()}"
        val dup = target.copy(
            id = dupId,
            name = "${target.name} (Copy)",
            xRatio = (target.xRatio + 0.05f).coerceAtMost(0.9f),
            yRatio = (target.yRatio + 0.05f).coerceAtMost(0.9f)
        )
        val updated = _overlays.value + dup
        _overlays.value = updated
        _selectedOverlayId.value = dupId
        syncActiveSceneOverlays(updated)
    }

    fun deleteOverlay(overlayId: String) {
        val updated = _overlays.value.filterNot { it.id == overlayId }
        _overlays.value = updated
        if (_selectedOverlayId.value == overlayId) {
            _selectedOverlayId.value = updated.firstOrNull()?.id
        }
        syncActiveSceneOverlays(updated)
    }

    fun bringOverlayForward(overlayId: String) {
        val list = _overlays.value.toMutableList()
        val index = list.indexOfFirst { it.id == overlayId }
        if (index >= 0 && index < list.size - 1) {
            val item = list.removeAt(index)
            list.add(index + 1, item)
            _overlays.value = list
            syncActiveSceneOverlays(list)
        }
    }

    fun sendOverlayBackward(overlayId: String) {
        val list = _overlays.value.toMutableList()
        val index = list.indexOfFirst { it.id == overlayId }
        if (index > 0) {
            val item = list.removeAt(index)
            list.add(index - 1, item)
            _overlays.value = list
            syncActiveSceneOverlays(list)
        }
    }

    // MEDIA SOURCES METHODS
    fun addMediaSource(sceneId: String, name: String, type: MediaSourceType, urlOrPath: String = "") {
        val newSource = MediaSourceItem(
            id = "ms_${System.currentTimeMillis()}",
            name = name,
            type = type,
            urlOrPath = urlOrPath
        )
        _scenes.value = _scenes.value.map { scene ->
            if (scene.id == sceneId) {
                scene.copy(mediaSources = scene.mediaSources + newSource)
            } else scene
        }
    }

    fun removeMediaSource(sceneId: String, sourceId: String) {
        _scenes.value = _scenes.value.map { scene ->
            if (scene.id == sceneId) {
                scene.copy(mediaSources = scene.mediaSources.filterNot { it.id == sourceId })
            } else scene
        }
    }

    private fun syncActiveSceneOverlays(updatedOverlays: List<OverlayItem>) {
        val activeId = _sceneConfig.value.activeSceneId
        _scenes.value = _scenes.value.map { scene ->
            if (scene.id == activeId || scene.isActive) {
                scene.copy(overlays = updatedOverlays)
            } else scene
        }
        autoSave()
    }

    // Audio Mixer & Recording
    fun toggleLayerLock(layerId: String) {
        _layers.value = _layers.value.map {
            if (it.id == layerId) it.copy(isLocked = !it.isLocked) else it
        }
    }

    fun toggleLayerVisibility(layerId: String) {
        _layers.value = _layers.value.map {
            if (it.id == layerId) it.copy(isVisible = !it.isVisible) else it
        }
    }

    fun moveLayerUp(layerId: String) {
        val list = _layers.value.toMutableList()
        val index = list.indexOfFirst { it.id == layerId }
        if (index > 0) {
            val item = list.removeAt(index)
            list.add(index - 1, item)
            _layers.value = list
        }
    }

    fun moveLayerDown(layerId: String) {
        val list = _layers.value.toMutableList()
        val index = list.indexOfFirst { it.id == layerId }
        if (index >= 0 && index < list.size - 1) {
            val item = list.removeAt(index)
            list.add(index + 1, item)
            _layers.value = list
        }
    }

    fun updateMicVolume(vol: Float) {
        _audioMixer.value = _audioMixer.value.copy(micVolume = vol)
    }

    fun updateInternalAudioVolume(vol: Float) {
        _audioMixer.value = _audioMixer.value.copy(internalAudioVolume = vol)
    }

    fun toggleNoiseSuppression() {
        _audioMixer.value = _audioMixer.value.copy(noiseSuppression = !_audioMixer.value.noiseSuppression)
    }

    fun toggleEchoCancellation() {
        _audioMixer.value = _audioMixer.value.copy(echoCancellation = !_audioMixer.value.echoCancellation)
    }

    fun toggleRecording() {
        val curr = _recordingState.value
        if (curr.isRecording) {
            _recordingState.value = curr.copy(
                isRecording = false,
                isPaused = false,
                savedFilePath = "/storage/emulated/0/Movies/GTLive_${System.currentTimeMillis()}.mp4"
            )
        } else {
            _recordingState.value = RecordingState(
                isRecording = true,
                isPaused = false,
                durationSeconds = 0,
                format = "MP4"
            )
        }
    }

    fun togglePauseRecording() {
        val curr = _recordingState.value
        if (curr.isRecording) {
            _recordingState.value = curr.copy(isPaused = !curr.isPaused)
        }
    }

    fun startBroadcast() {
        viewModelScope.launch {
            youtubeService.createLiveBroadcast(_broadcastDetails.value)
            _streamSession.value = _streamSession.value.copy(
                isLive = true,
                currentViewers = 3420,
                peakViewers = 3420,
                startTime = System.currentTimeMillis()
            )
        }
    }

    fun stopBroadcast() {
        viewModelScope.launch {
            _streamSession.value = _streamSession.value.copy(
                isLive = false,
                currentViewers = 0
            )
        }
    }
}
