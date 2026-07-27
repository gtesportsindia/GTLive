package com.gtesports.gtlive.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gtesports.gtlive.service.ScreenCaptureService
import com.gtesports.gtlive.ui.components.RealOverlayCanvas
import com.gtesports.gtlive.ui.theme.GTBackgroundBlack
import com.gtesports.gtlive.ui.theme.GTBorderDark
import com.gtesports.gtlive.ui.theme.GTRedPrimary
import com.gtesports.gtlive.ui.theme.GTSurfaceCard
import com.gtesports.gtlive.ui.theme.GTTextMuted
import com.gtesports.gtlive.ui.theme.GTTextPrimary
import com.gtesports.gtlive.viewmodel.StreamViewModel
import kotlin.random.Random

@Composable
fun ScreenLiveScreen(
    navController: NavController,
    streamViewModel: StreamViewModel = viewModel()
) {
    val context = LocalContext.current
    val overlays by streamViewModel.overlays.collectAsState()

    // Live capture state from service
    val isCapturing by ScreenCaptureService.isCapturingFlow.collectAsState()
    val stats by ScreenCaptureService.captureStatsFlow.collectAsState()

    // Screen Settings
    var selectedResolutionLabel by remember { mutableStateOf("1080p") }
    var selectedWidth by remember { mutableIntStateOf(1920) }
    var selectedHeight by remember { mutableIntStateOf(1080) }

    var selectedFps by remember { mutableIntStateOf(60) }
    var selectedBitrateKbps by remember { mutableIntStateOf(8000) }
    var selectedOrientation by remember { mutableStateOf("LANDSCAPE") }
    var selectedAudioMode by remember { mutableStateOf("INTERNAL + MIC") }
    var rtmpUrl by remember { mutableStateOf("rtmp://a.rtmp.youtube.com/live2/gtlive-screen") }

    // Audio / Post Notification Permissions
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
    }

    // MediaProjection Activity Launcher
    val mediaProjectionManager = remember {
        context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    val screenCaptureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val intent = Intent(context, ScreenCaptureService::class.java).apply {
                action = ScreenCaptureService.ACTION_START_CAPTURE
                putExtra(ScreenCaptureService.EXTRA_RESULT_CODE, result.resultCode)
                putExtra(ScreenCaptureService.EXTRA_RESULT_DATA, result.data)
                putExtra(ScreenCaptureService.EXTRA_WIDTH, selectedWidth)
                putExtra(ScreenCaptureService.EXTRA_HEIGHT, selectedHeight)
                putExtra(ScreenCaptureService.EXTRA_FPS, selectedFps)
                putExtra(ScreenCaptureService.EXTRA_BITRATE, selectedBitrateKbps)
                putExtra(ScreenCaptureService.EXTRA_ORIENTATION, selectedOrientation)
                putExtra(ScreenCaptureService.EXTRA_AUDIO_SOURCE, selectedAudioMode)
                putExtra(ScreenCaptureService.EXTRA_RTMP_URL, rtmpUrl)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!hasAudioPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GTBackgroundBlack)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
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
                Text(text = "← BACK TO DASHBOARD", color = GTRedPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            // Live Pulse Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(GTSurfaceCard)
                    .border(1.dp, if (isCapturing) Color(0xFF10B981) else GTBorderDark, RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(700),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "alpha"
                )

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isCapturing) Color(0xFF10B981).copy(alpha = alpha) else Color.Gray)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isCapturing) "MEDIAPROJECTION ACTIVE" else "SCREEN CAPTURE IDLE",
                    color = if (isCapturing) Color(0xFF10B981) else GTTextMuted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // LIVE PREVIEW / MONITOR CANVAS
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0D0D12))
                .border(1.dp, if (isCapturing) Color(0xFF10B981) else GTBorderDark, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (isCapturing) {
                // Interactive Gameplay Waveform Animation
                val infiniteTransition = rememberInfiniteTransition(label = "wave")
                val waveOffset by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 100f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "waveOffset"
                )

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    // Grid backdrop
                    for (x in 0..canvasWidth.toInt() step 60) {
                        drawLine(Color(0xFF1E1E28), Offset(x.toFloat(), 0f), Offset(x.toFloat(), canvasHeight), strokeWidth = 1f)
                    }
                    for (y in 0..canvasHeight.toInt() step 40) {
                        drawLine(Color(0xFF1E1E28), Offset(0f, y.toFloat()), Offset(canvasWidth, y.toFloat()), strokeWidth = 1f)
                    }

                    // Audio & Gameplay capture wave
                    val path = Path()
                    path.moveTo(0f, canvasHeight / 2)
                    var x = 0f
                    while (x < canvasWidth) {
                        val y = (canvasHeight / 2) + Math.sin((x + waveOffset * 5) * 0.03).toFloat() * 28f
                        path.lineTo(x, y)
                        x += 10f
                    }
                    drawPath(path, color = Color(0xFF10B981), style = Stroke(width = 3.dp.toPx()))
                }

                // Real Overlay Canvas Over Stream Feed
                RealOverlayCanvas(
                    overlays = overlays,
                    isEditable = false
                )

                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFF10B981), RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "LIVE SCREEN CAPTURE IN PROGRESS",
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "${stats.width}x${stats.height} @ ${stats.fps} FPS",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Bitrate: ${stats.bitrateKbps} Kbps | Audio: ${stats.audioMode}",
                        color = GTTextMuted,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DURATION: %02d:%02d".format(stats.durationSeconds / 60, stats.durationSeconds % 60),
                            color = Color(0xFF3B82F6),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "FRAMES: ${stats.capturedFrames}",
                            color = Color(0xFFF59E0B),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                // Idle State Display
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(GTRedPrimary.copy(alpha = 0.15f))
                            .border(1.dp, GTRedPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "LIVE", color = GTRedPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "SCREEN & GAMEPLAY STREAMING",
                        color = GTTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Android MediaProjection API + AudioPlaybackCapture",
                        color = GTTextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // CONTROLS AND SETTINGS SECTION
        Text(
            text = "STREAM QUALITY & CAPTURE SETTINGS",
            color = GTTextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 1. RESOLUTION SELECTOR
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(GTSurfaceCard)
                .border(1.dp, GTBorderDark, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Text(text = "RESOLUTION", color = GTTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    Triple("720p", 1280, 720),
                    Triple("1080p", 1920, 1080),
                    Triple("1440p", 2560, 1440)
                ).forEach { (label, w, h) ->
                    val isSelected = selectedResolutionLabel == label
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) GTRedPrimary else Color(0xFF181820))
                            .clickable(enabled = !isCapturing) {
                                selectedResolutionLabel = label
                                selectedWidth = w
                                selectedHeight = h
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else GTTextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. FPS & BITRATE ROW
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // FPS Box
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GTSurfaceCard)
                    .border(1.dp, GTBorderDark, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text(text = "FRAME RATE", color = GTTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(30, 60).forEach { fpsVal ->
                        val isSelected = selectedFps == fpsVal
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFF3B82F6) else Color(0xFF181820))
                                .clickable(enabled = !isCapturing) { selectedFps = fpsVal }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$fpsVal FPS",
                                color = if (isSelected) Color.White else GTTextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Orientation Box
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GTSurfaceCard)
                    .border(1.dp, GTBorderDark, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text(text = "ORIENTATION", color = GTTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("LANDSCAPE", "PORTRAIT").forEach { orient ->
                        val isSelected = selectedOrientation == orient
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFF8B5CF6) else Color(0xFF181820))
                                .clickable(enabled = !isCapturing) { selectedOrientation = orient }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (orient == "LANDSCAPE") "LAND" else "PORT",
                                color = if (isSelected) Color.White else GTTextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3. BITRATE SELECTOR
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(GTSurfaceCard)
                .border(1.dp, GTBorderDark, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Text(text = "TARGET BITRATE (Kbps)", color = GTTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(4000, 6000, 8000, 12000).forEach { brVal ->
                    val isSelected = selectedBitrateKbps == brVal
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFF10B981) else Color(0xFF181820))
                            .clickable(enabled = !isCapturing) { selectedBitrateKbps = brVal }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${brVal / 1000}M",
                            color = if (isSelected) Color.White else GTTextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4. AUDIO SOURCE SELECTOR
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(GTSurfaceCard)
                .border(1.dp, GTBorderDark, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Text(text = "AUDIO CAPTURE SOURCE", color = GTTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("INTERNAL + MIC", "INTERNAL ONLY", "MIC ONLY").forEach { mode ->
                    val isSelected = selectedAudioMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFFF59E0B) else Color(0xFF181820))
                            .clickable(enabled = !isCapturing) { selectedAudioMode = mode }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (mode.contains(" + ")) "MIXED" else if (mode.contains("INTERNAL")) "GAME" else "MIC",
                            color = if (isSelected) Color.Black else GTTextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 5. YOUTUBE RTMP INGESTION URL INPUT
        OutlinedTextField(
            value = rtmpUrl,
            onValueChange = { rtmpUrl = it },
            label = { Text("YOUTUBE RTMP STREAM URL", color = GTTextMuted, fontSize = 11.sp) },
            singleLine = true,
            enabled = !isCapturing,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = GTSurfaceCard,
                unfocusedContainerColor = GTSurfaceCard,
                focusedBorderColor = GTRedPrimary,
                unfocusedBorderColor = GTBorderDark,
                focusedTextColor = GTTextPrimary,
                unfocusedTextColor = GTTextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // MAIN START / STOP CAPTURE BUTTON
        Button(
            onClick = {
                if (isCapturing) {
                    val stopIntent = Intent(context, ScreenCaptureService::class.java).apply {
                        action = ScreenCaptureService.ACTION_STOP_CAPTURE
                    }
                    context.startService(stopIntent)
                } else {
                    val captureIntent = mediaProjectionManager.createScreenCaptureIntent()
                    screenCaptureLauncher.launch(captureIntent)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isCapturing) Color(0xFFDC2626) else GTRedPrimary
            )
        ) {
            Text(
                text = if (isCapturing) "STOP SCREEN CAPTURE SERVICE" else "START SCREEN CAPTURE SERVICE",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
