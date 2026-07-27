package com.gtesports.gtlive.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gtesports.gtlive.model.OverlayItem
import com.gtesports.gtlive.model.OverlayType
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun RealOverlayCanvas(
    overlays: List<OverlayItem>,
    selectedOverlayId: String? = null,
    isEditable: Boolean = false,
    snapToGrid: Boolean = false,
    isStageLocked: Boolean = false,
    onSelectOverlay: (String?) -> Unit = {},
    onUpdatePosition: (id: String, xRatio: Float, yRatio: Float) -> Unit = { _, _, _ -> },
    onUpdateScale: (id: String, scale: Float) -> Unit = { _, _ -> },
    onUpdateRotation: (id: String, rotation: Float) -> Unit = { _, _ -> },
    onDeleteOverlay: (id: String) -> Unit = {},
    onToggleLock: (id: String) -> Unit = {},
    onDuplicateOverlay: (id: String) -> Unit = {},
    onBringForward: (id: String) -> Unit = {},
    onSendBackward: (id: String) -> Unit = {}
) {
    // Dynamic Live Time for Clocks
    var currentTimeStr by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        val sdf = SimpleDateFormat("HH:mm:ss 'IST'", Locale.getDefault())
        while (true) {
            currentTimeStr = sdf.format(Date())
            delay(1000L)
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .clickable(enabled = isEditable) { onSelectOverlay(null) }
    ) {
        val containerWidthPx = maxWidth.value
        val containerHeightPx = maxHeight.value

        // Grid overlay lines if grid snapping is enabled
        if (isEditable && snapToGrid) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridSpacing = 32.dp.toPx()
                var x = 0f
                while (x < size.width) {
                    drawLine(Color(0xFF1E1E2C), start = Offset(x, 0f), end = Offset(x, size.height), strokeWidth = 1f)
                    x += gridSpacing
                }
                var y = 0f
                while (y < size.height) {
                    drawLine(Color(0xFF1E1E2C), start = Offset(0f, y), end = Offset(size.width, y), strokeWidth = 1f)
                    y += gridSpacing
                }
            }
        }

        // Render Overlays sorted by zIndex ascending
        overlays.filter { it.isVisible }.sortedBy { it.zIndex }.forEach { overlay ->
            val isSelected = isEditable && overlay.id == selectedOverlayId
            val xPx = containerWidthPx * overlay.xRatio
            val yPx = containerHeightPx * overlay.yRatio

            var currentScale by remember(overlay.id, overlay.scale) { mutableFloatStateOf(overlay.scale) }
            var currentRotation by remember(overlay.id, overlay.rotation) { mutableFloatStateOf(overlay.rotation) }

            Box(
                modifier = Modifier
                    .offset { IntOffset(xPx.dp.roundToPx(), yPx.dp.roundToPx()) }
                    .graphicsLayer(
                        scaleX = currentScale,
                        scaleY = currentScale,
                        rotationZ = currentRotation,
                        alpha = overlay.opacity
                    )
                    .clickable(enabled = isEditable) { onSelectOverlay(overlay.id) }
                    .then(
                        if (isEditable && !isStageLocked && !overlay.isLocked) {
                            Modifier.pointerInput(overlay.id, snapToGrid) {
                                detectTransformGestures { _, pan, zoom, rotation ->
                                    currentScale = (currentScale * zoom).coerceIn(0.2f, 4.0f)
                                    currentRotation += rotation

                                    var newXRatio = (xPx + pan.x / density) / containerWidthPx
                                    var newYRatio = (yPx + pan.y / density) / containerHeightPx

                                    if (snapToGrid) {
                                        newXRatio = (newXRatio * 20).roundToInt() / 20f
                                        newYRatio = (newYRatio * 20).roundToInt() / 20f
                                    }

                                    onUpdatePosition(overlay.id, newXRatio, newYRatio)
                                    onUpdateScale(overlay.id, currentScale)
                                    onUpdateRotation(overlay.id, currentRotation)
                                }
                            }
                        } else Modifier
                    )
            ) {
                // Overlay Visual Container
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(getOverlayBackgroundColor(overlay.type))
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color(0xFFFF0B3A) else Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    RenderOverlayContent(overlay, currentTimeStr)
                }

                // Selection Controls Box for Selected Overlay
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .offset(y = (-36).dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.9f))
                            .border(1.dp, Color(0xFFFF0B3A), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "🔒",
                                fontSize = 12.sp,
                                modifier = Modifier.clickable { onToggleLock(overlay.id) }
                            )
                            Text(
                                text = "📋",
                                fontSize = 12.sp,
                                modifier = Modifier.clickable { onDuplicateOverlay(overlay.id) }
                            )
                            Text(
                                text = "↑",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.clickable { onBringForward(overlay.id) }
                            )
                            Text(
                                text = "↓",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.clickable { onSendBackward(overlay.id) }
                            )
                            Text(
                                text = "🗑️",
                                fontSize = 12.sp,
                                modifier = Modifier.clickable { onDeleteOverlay(overlay.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RenderOverlayContent(overlay: OverlayItem, liveTimeStr: String) {
    when (overlay.type) {
        OverlayType.WEBCAM, OverlayType.CAMERA_PIP -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981))
                )
                Column {
                    Text(text = "📷 CAMERA PIP", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    Text(text = "1080p 60FPS • LIVE FEED", color = Color.LightGray, fontSize = 9.sp)
                }
            }
        }
        OverlayType.SCREEN_CAPTURE -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "📱", fontSize = 14.sp)
                Column {
                    Text(text = "SCREEN STREAM CAPTURE", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    Text(text = "MediaProjection Active", color = Color(0xFF38BDF8), fontSize = 9.sp)
                }
            }
        }
        OverlayType.LOGO -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFFF0B3A))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "GT", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
                }
                Text(
                    text = overlay.name.ifEmpty { "GT ESPORTS OFFICIAL" },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
        OverlayType.WATERMARK -> {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Red)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "🔴 GT LIVE OFFICIAL",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp
                )
            }
        }
        OverlayType.CLOCK, OverlayType.DATE_TIME -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = "⏰", fontSize = 12.sp)
                Text(
                    text = if (liveTimeStr.isNotEmpty()) liveTimeStr else "18:30:00 IST",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
        OverlayType.TEXT -> {
            Text(
                text = overlay.textContent.ifEmpty { "GT ESPORTS LIVE TICKER" },
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
        OverlayType.SUBSCRIBER_COUNTER -> {
            Column {
                Text(text = "🏆 SUBSCRIBER GOAL", color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color.DarkGray)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.75f)
                                .background(Color(0xFFFF0B3A))
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "${overlay.goalCurrent} / ${overlay.goalTarget}", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        OverlayType.VIEWER_COUNTER -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                )
                Text(text = "👁️ 3,420 VIEWERS", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
        OverlayType.DONATION_GOAL -> {
            Column {
                Text(text = "💖 DONATION GOAL", color = Color(0xFFEC4899), fontSize = 10.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "₹25,000 / ₹50,000", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        else -> {
            Text(
                text = overlay.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}

private fun getOverlayBackgroundColor(type: OverlayType): Color {
    return when (type) {
        OverlayType.WEBCAM, OverlayType.CAMERA_PIP -> Color(0xFF1D4ED8).copy(alpha = 0.85f)
        OverlayType.SCREEN_CAPTURE -> Color(0xFF0284C7).copy(alpha = 0.85f)
        OverlayType.LOGO, OverlayType.WATERMARK -> Color(0xFFFF0B3A).copy(alpha = 0.90f)
        OverlayType.SUBSCRIBER_COUNTER, OverlayType.DONATION_GOAL -> Color(0xFF1E1E2A).copy(alpha = 0.95f)
        OverlayType.CLOCK, OverlayType.DATE_TIME -> Color(0xFF0F172A).copy(alpha = 0.90f)
        else -> Color(0xFF0A0A0E).copy(alpha = 0.90f)
    }
}
