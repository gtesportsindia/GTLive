package com.gtesports.gtlive.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.gtesports.gtlive.camera.CameraXHelper
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gtesports.gtlive.ui.components.RealOverlayCanvas
import com.gtesports.gtlive.ui.theme.GTBackgroundBlack
import com.gtesports.gtlive.ui.theme.GTBorderDark
import com.gtesports.gtlive.ui.theme.GTRedPrimary
import com.gtesports.gtlive.ui.theme.GTSurfaceCard
import com.gtesports.gtlive.ui.theme.GTTextMuted
import com.gtesports.gtlive.ui.theme.GTTextPrimary
import com.gtesports.gtlive.viewmodel.StreamViewModel
import kotlinx.coroutines.delay

@Composable
fun CameraLiveScreen(
    navController: NavController,
    streamViewModel: StreamViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val overlays by streamViewModel.overlays.collectAsState()

    // Permission state
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasCameraPermission = permissions[Manifest.permission.CAMERA] ?: hasCameraPermission
        hasAudioPermission = permissions[Manifest.permission.RECORD_AUDIO] ?: hasAudioPermission
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission || !hasAudioPermission) {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            )
        }
    }

    if (!hasCameraPermission || !hasAudioPermission) {
        // Permission Request UI
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GTBackgroundBlack)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(GTRedPrimary.copy(alpha = 0.15f))
                        .border(1.dp, GTRedPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "CAM", color = GTRedPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "CAMERA & AUDIO PERMISSIONS",
                    color = GTTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "GT LIVE Studio requires Camera and Microphone permissions to record full 1080p60 camera live streams.",
                    color = GTTextMuted,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(28.dp))
                Button(
                    onClick = {
                        permissionLauncher.launch(
                            arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GTRedPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "GRANT PERMISSIONS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Back to Dashboard",
                    color = GTTextMuted,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { navController.popBackStack() }
                )
            }
        }
    } else {
        // Real CameraX Live Stream Screen
        val cameraHelper = remember { CameraXHelper(context) }
        var previewViewRef by remember { mutableStateOf<PreviewView?>(null) }
        var isFrontCam by remember { mutableStateOf(true) }
        var isFlashOn by remember { mutableStateOf(false) }
        var isFlashSupported by remember { mutableStateOf(false) }
        var isMicMuted by remember { mutableStateOf(false) }
        var isLiveStreaming by remember { mutableStateOf(false) }

        // Camera Zoom & Focus state
        var currentZoomRatio by remember { mutableStateOf(1.0f) }
        var minZoomRatio by remember { mutableStateOf(1.0f) }
        var maxZoomRatio by remember { mutableStateOf(5.0f) }
        var focusPoint by remember { mutableStateOf<Offset?>(null) }

        DisposableEffect(Unit) {
            onDispose {
                cameraHelper.stopCamera()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GTBackgroundBlack)
        ) {
            // CameraX Viewport
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, zoomChange, _ ->
                            val newZoom = (currentZoomRatio * zoomChange).coerceIn(minZoomRatio, maxZoomRatio)
                            currentZoomRatio = newZoom
                            cameraHelper.setZoomRatio(newZoom)
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            focusPoint = offset
                            previewViewRef?.let { pView ->
                                cameraHelper.tapToFocus(pView, offset.x, offset.y)
                            }
                        }
                    }
            ) {
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                            previewViewRef = this
                            cameraHelper.startCamera(
                                lifecycleOwner = lifecycleOwner,
                                previewView = this,
                                useFrontCamera = isFrontCam,
                                onCameraBound = { cam ->
                                    isFlashSupported = cam.cameraInfo.hasFlashUnit()
                                    cam.cameraInfo.zoomState.observe(lifecycleOwner) { zoomState ->
                                        currentZoomRatio = zoomState.zoomRatio
                                        minZoomRatio = zoomState.minZoomRatio
                                        maxZoomRatio = zoomState.maxZoomRatio
                                    }
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Tap-To-Focus Visual Feedback
                focusPoint?.let { point ->
                    var focusAlpha by remember(point) { mutableStateOf(1f) }
                    var focusScale by remember(point) { mutableStateOf(1.2f) }

                    LaunchedEffect(point) {
                        focusAlpha = 1f
                        focusScale = 1.2f
                        delay(100)
                        focusScale = 1.0f
                        delay(800)
                        focusAlpha = 0f
                        focusPoint = null
                    }

                    if (focusAlpha > 0f) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(
                                color = Color(0xFFFF0B3A),
                                radius = 36.dp.toPx() * focusScale,
                                center = point,
                                style = Stroke(width = 2.dp.toPx()),
                                alpha = focusAlpha
                            )
                            drawCircle(
                                color = Color(0xFF10B981),
                                radius = 4.dp.toPx(),
                                center = point,
                                alpha = focusAlpha
                            )
                        }
                    }
                }
            }

            // REAL OVERLAY ENGINE LAYER
            RealOverlayCanvas(
                overlays = overlays,
                isEditable = false
            )

            // HUD OVERLAY LAYER
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.7f))
                            .border(1.dp, GTBorderDark, RoundedCornerShape(8.dp))
                            .clickable { navController.popBackStack() }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(text = "← EXIT STUDIO", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    // Live Status Pulse Badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Black.copy(alpha = 0.75f))
                            .border(1.dp, if (isLiveStreaming) GTRedPrimary else GTBorderDark, RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                        val pulseAlpha by infiniteTransition.animateFloat(
                            initialValue = 0.3f,
                            targetValue = 1.0f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(700),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "pulseAlpha"
                        )

                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isLiveStreaming) GTRedPrimary.copy(alpha = pulseAlpha) else Color.Gray)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isLiveStreaming) "LIVE STREAMING" else "READY TO STREAM",
                            color = if (isLiveStreaming) GTRedPrimary else GTTextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                // Middle Quick Indicators (FPS, Bitrate, Zoom Level)
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.75f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "1080p60 | 6400 Kbps | ${if (isFrontCam) "FRONT" else "BACK"}",
                                color = Color(0xFF10B981),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.75f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = String.format("ZOOM: %.1fx", currentZoomRatio),
                                color = Color(0xFF3B82F6),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Bottom Control Panel
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Quick Zoom Selector Chips
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Black.copy(alpha = 0.75f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(1.0f, 2.0f, 3.0f, 5.0f).forEach { ratio ->
                            val isSelected = kotlin.math.abs(currentZoomRatio - ratio) < 0.2f
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (isSelected) GTRedPrimary else Color.Transparent)
                                    .clickable {
                                        currentZoomRatio = ratio
                                        cameraHelper.setZoomRatio(ratio)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${ratio.toInt()}x",
                                    color = if (isSelected) Color.White else GTTextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Main Action Buttons Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Switch Camera
                        Button(
                            onClick = {
                                isFrontCam = !isFrontCam
                                isFlashOn = false
                                previewViewRef?.let { pView ->
                                    cameraHelper.switchCamera(
                                        lifecycleOwner = lifecycleOwner,
                                        previewView = pView,
                                        onCameraBound = { cam ->
                                            isFlashSupported = cam.cameraInfo.hasFlashUnit()
                                            cam.cameraInfo.zoomState.observe(lifecycleOwner) { zoomState ->
                                                currentZoomRatio = zoomState.zoomRatio
                                                minZoomRatio = zoomState.minZoomRatio
                                                maxZoomRatio = zoomState.maxZoomRatio
                                            }
                                        }
                                    )
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GTSurfaceCard.copy(alpha = 0.9f))
                        ) {
                            Text(
                                text = if (isFrontCam) "FRONT" else "BACK",
                                color = GTTextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Flash Toggle
                        Button(
                            onClick = {
                                if (isFlashSupported) {
                                    isFlashOn = !isFlashOn
                                    cameraHelper.setFlashEnabled(isFlashOn)
                                }
                            },
                            enabled = isFlashSupported,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFlashOn) Color(0xFFF59E0B) else GTSurfaceCard.copy(alpha = 0.9f)
                            )
                        ) {
                            Text(
                                text = if (!isFlashSupported) "NO FLASH" else if (isFlashOn) "FLASH ON" else "FLASH OFF",
                                color = if (isFlashOn) Color.Black else GTTextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Mic Toggle
                        Button(
                            onClick = { isMicMuted = !isMicMuted },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isMicMuted) GTRedPrimary else GTSurfaceCard.copy(alpha = 0.9f)
                            )
                        ) {
                            Text(
                                text = if (isMicMuted) "MIC MUTED" else "MIC ACTIVE",
                                color = GTTextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // GO LIVE / END STREAM Big Control Button
                    Button(
                        onClick = { isLiveStreaming = !isLiveStreaming },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLiveStreaming) Color(0xFFDC2626) else GTRedPrimary
                        )
                    ) {
                        Text(
                            text = if (isLiveStreaming) "END LIVE STREAM" else "START CAMERA LIVE STREAM",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}
