package com.gtesports.gtlive.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gtesports.gtlive.ui.theme.GTBackgroundBlack
import com.gtesports.gtlive.ui.theme.GTBorderDark
import com.gtesports.gtlive.ui.theme.GTRedPrimary
import com.gtesports.gtlive.ui.theme.GTSurfaceCard
import com.gtesports.gtlive.ui.theme.GTTextMuted
import com.gtesports.gtlive.ui.theme.GTTextPrimary
import com.gtesports.gtlive.ui.theme.GTTextSecondary
import com.gtesports.gtlive.viewmodel.StreamViewModel

@Composable
fun GoLiveScreen(
    navController: NavController,
    streamViewModel: StreamViewModel = viewModel()
) {
    val streamSession by streamViewModel.streamSession.collectAsState()
    val broadcastDetails by streamViewModel.broadcastDetails.collectAsState()
    val chatMessages by streamViewModel.liveChatMessages.collectAsState()
    val scenes by streamViewModel.scenes.collectAsState()
    val layers by streamViewModel.layers.collectAsState()
    val audioMixer by streamViewModel.audioMixer.collectAsState()
    val recordingState by streamViewModel.recordingState.collectAsState()
    val analytics by streamViewModel.analytics.collectAsState()

    var chatInput by remember { mutableStateOf("") }
    var activeTab by remember { mutableStateOf("CHAT") } // CHAT, SCENES, AUDIO, RECORD
    val listState = rememberLazyListState()

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GTBackgroundBlack)
            .padding(14.dp)
    ) {
        // Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(GTSurfaceCard)
                    .border(1.dp, GTBorderDark, RoundedCornerShape(8.dp))
                    .clickable { navController.popBackStack() }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(text = "← DASHBOARD", color = GTRedPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }

            Text(
                text = "YOUTUBE LIVE STUDIO",
                color = GTTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                letterSpacing = 1.sp
            )

            // Live Pulse Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(GTSurfaceCard)
                    .border(1.dp, if (streamSession.isLive) Color(0xFF10B981) else GTBorderDark, RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
                    label = "alpha"
                )

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (streamSession.isLive) Color(0xFF10B981).copy(alpha = alpha) else Color.Gray)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (streamSession.isLive) "LIVE ${streamSession.currentViewers}" else "OFFLINE",
                    color = if (streamSession.isLive) Color(0xFF10B981) else GTTextMuted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Broadcast Title Header Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(GTSurfaceCard)
                .border(1.dp, GTBorderDark, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "BROADCAST TITLE", color = GTTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = broadcastDetails.title,
                        color = GTTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF3B82F6).copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = broadcastDetails.privacy, color = Color(0xFF3B82F6), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Control Panel Tabs (CHAT, SCENES, AUDIO, RECORD)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("CHAT", "SCENES", "AUDIO", "RECORD").forEach { tab ->
                val isSelected = activeTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) GTRedPrimary else GTSurfaceCard)
                        .clickable { activeTab = tab }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        color = if (isSelected) Color.White else GTTextMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // TAB CONTENTS
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (activeTab) {
                "CHAT" -> LiveChatSection(
                    chatMessages = chatMessages,
                    chatInput = chatInput,
                    onChatInputChange = { chatInput = it },
                    onSendMessage = {
                        streamViewModel.sendChatMessage(chatInput)
                        chatInput = ""
                    },
                    listState = listState
                )
                "SCENES" -> SceneManagerSection(
                    scenes = scenes,
                    layers = layers,
                    onSelectScene = { streamViewModel.selectScene(it) },
                    onToggleLayerVisibility = { streamViewModel.toggleLayerVisibility(it) },
                    onToggleLayerLock = { streamViewModel.toggleLayerLock(it) },
                    onMoveLayerUp = { streamViewModel.moveLayerUp(it) },
                    onMoveLayerDown = { streamViewModel.moveLayerDown(it) }
                )
                "AUDIO" -> AudioMixerSection(
                    audioMixer = audioMixer,
                    onMicVolChange = { streamViewModel.updateMicVolume(it) },
                    onInternalVolChange = { streamViewModel.updateInternalAudioVolume(it) },
                    onToggleNoiseSuppression = { streamViewModel.toggleNoiseSuppression() },
                    onToggleEchoCancellation = { streamViewModel.toggleEchoCancellation() }
                )
                "RECORD" -> RecordingSection(
                    recordingState = recordingState,
                    onToggleRecording = { streamViewModel.toggleRecording() },
                    onTogglePause = { streamViewModel.togglePauseRecording() }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Start / End Broadcast Main Button
        Button(
            onClick = {
                if (streamSession.isLive) streamViewModel.stopBroadcast() else streamViewModel.startBroadcast()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (streamSession.isLive) Color(0xFFDC2626) else GTRedPrimary
            )
        ) {
            Text(
                text = if (streamSession.isLive) "END YOUTUBE LIVE BROADCAST" else "GO LIVE ON YOUTUBE BROADCAST",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 1.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun LiveChatSection(
    chatMessages: List<com.gtesports.gtlive.model.ChatMessage>,
    chatInput: String,
    onChatInputChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    listState: androidx.compose.foundation.lazy.LazyListState
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Chat List Panel
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF101015))
                .border(1.dp, GTBorderDark, RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chatMessages) { chat ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (chat.isSuperChat) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF59E0B))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = chat.superChatAmount, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    } else if (chat.isModerator) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(GTRedPrimary)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = "MOD", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    Text(
                        text = "${chat.senderName}: ",
                        color = if (chat.isModerator) GTRedPrimary else if (chat.isSuperChat) Color(0xFFF59E0B) else Color(0xFF3B82F6),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = chat.messageText,
                        color = GTTextPrimary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Emoji Bar
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val emojis = listOf("🔥", "🎮", "GG", "🏆", "💯", "❤️", "👏", "🚀")
            items(emojis) { emoji ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(GTSurfaceCard)
                        .clickable { onChatInputChange(chatInput + emoji) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(text = emoji, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Message Input Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = chatInput,
                onValueChange = onChatInputChange,
                placeholder = { Text("Send live chat message...", color = GTTextMuted, fontSize = 12.sp) },
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = GTSurfaceCard,
                    unfocusedContainerColor = GTSurfaceCard,
                    focusedBorderColor = GTRedPrimary,
                    unfocusedBorderColor = GTBorderDark,
                    focusedTextColor = GTTextPrimary,
                    unfocusedTextColor = GTTextPrimary
                )
            )

            Button(
                onClick = onSendMessage,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GTRedPrimary),
                modifier = Modifier.height(50.dp)
            ) {
                Text(text = "SEND", fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun SceneManagerSection(
    scenes: List<com.gtesports.gtlive.model.SceneItem>,
    layers: List<com.gtesports.gtlive.model.LayerItem>,
    onSelectScene: (String) -> Unit,
    onToggleLayerVisibility: (String) -> Unit,
    onToggleLayerLock: (String) -> Unit,
    onMoveLayerUp: (String) -> Unit,
    onMoveLayerDown: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = "SCENES SELECTOR", color = GTTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(scenes) { scene ->
                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (scene.isActive) GTRedPrimary else GTSurfaceCard)
                        .border(1.dp, if (scene.isActive) GTRedPrimary else GTBorderDark, RoundedCornerShape(10.dp))
                        .clickable { onSelectScene(scene.id) }
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = scene.name,
                            color = if (scene.isActive) Color.White else GTTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (scene.isActive) "ACTIVE SCENE" else "TAP TO SWITCH",
                            color = if (scene.isActive) Color.White.copy(alpha = 0.8f) else GTTextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "LAYER STACK MANAGER", color = GTTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(layers) { layer ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(GTSurfaceCard)
                        .border(1.dp, GTBorderDark, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = layer.name, color = GTTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(text = "TYPE: ${layer.type}", color = GTTextMuted, fontSize = 10.sp)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (layer.isVisible) Color(0xFF10B981) else Color.Gray)
                                    .clickable { onToggleLayerVisibility(layer.id) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(text = if (layer.isVisible) "SHOW" else "HIDE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (layer.isLocked) Color(0xFFEF4444) else Color(0xFF3B82F6))
                                    .clickable { onToggleLayerLock(layer.id) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(text = if (layer.isLocked) "LOCK" else "UNLOCK", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AudioMixerSection(
    audioMixer: com.gtesports.gtlive.model.AudioMixerState,
    onMicVolChange: (Float) -> Unit,
    onInternalVolChange: (Float) -> Unit,
    onToggleNoiseSuppression: () -> Unit,
    onToggleEchoCancellation: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(GTSurfaceCard)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(text = "AUDIO MIXER & NOISE CONTROL", color = GTTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)

        // Mic Slider
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "MICROPHONE INPUT", color = GTTextSecondary, fontSize = 12.sp)
                Text(text = "${(audioMixer.micVolume * 100).toInt()}%", color = GTRedPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Slider(
                value = audioMixer.micVolume,
                onValueChange = onMicVolChange,
                colors = SliderDefaults.colors(thumbColor = GTRedPrimary, activeTrackColor = GTRedPrimary)
            )
        }

        // Internal Audio Slider
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "GAME INTERNAL AUDIO", color = GTTextSecondary, fontSize = 12.sp)
                Text(text = "${(audioMixer.internalAudioVolume * 100).toInt()}%", color = Color(0xFF3B82F6), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Slider(
                value = audioMixer.internalAudioVolume,
                onValueChange = onInternalVolChange,
                colors = SliderDefaults.colors(thumbColor = Color(0xFF3B82F6), activeTrackColor = Color(0xFF3B82F6))
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Noise Suppression", color = GTTextPrimary, fontSize = 12.sp)
            Switch(
                checked = audioMixer.noiseSuppression,
                onCheckedChange = { onToggleNoiseSuppression() },
                colors = SwitchDefaults.colors(checkedTrackColor = GTRedPrimary)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Echo Cancellation", color = GTTextPrimary, fontSize = 12.sp)
            Switch(
                checked = audioMixer.echoCancellation,
                onCheckedChange = { onToggleEchoCancellation() },
                colors = SwitchDefaults.colors(checkedTrackColor = GTRedPrimary)
            )
        }
    }
}

@Composable
fun RecordingSection(
    recordingState: com.gtesports.gtlive.model.RecordingState,
    onToggleRecording: () -> Unit,
    onTogglePause: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(GTSurfaceCard)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(if (recordingState.isRecording) Color(0xFF10B981) else GTRedPrimary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (recordingState.isRecording) "REC" else "OFF",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (recordingState.isRecording) "LOCAL MP4 RECORDING IN PROGRESS" else "READY TO RECORD STREAM TO GALLERY",
            color = GTTextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        if (recordingState.savedFilePath.isNotEmpty()) {
            Text(
                text = "Saved to: ${recordingState.savedFilePath}",
                color = Color(0xFF10B981),
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = onToggleRecording,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (recordingState.isRecording) Color(0xFFDC2626) else Color(0xFF10B981)
                )
            ) {
                Text(text = if (recordingState.isRecording) "STOP RECORDING" else "START RECORDING", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            if (recordingState.isRecording) {
                Button(
                    onClick = onTogglePause,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B))
                ) {
                    Text(text = if (recordingState.isPaused) "RESUME" else "PAUSE", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                }
            }
        }
    }
}
