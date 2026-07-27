package com.gtesports.gtlive.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
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
    onUpdatePosition: (String, Float, Float) -> Unit = { _, _, _ -> },
    onUpdateScale: (String, Float) -> Unit = { _, _ -> },
    onUpdateRotation: (String, Float) -> Unit = { _, _ -> },
    onDeleteOverlay: (String) -> Unit = {},
    onToggleLock: (String) -> Unit = {},
    onDuplicateOverlay: (String) -> Unit = {},
    onBringForward: (String) -> Unit = {},
    onSendBackward: (String) -> Unit = {}
) {

    var currentTime by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        while (true) {
            currentTime = sdf.format(Date()) + " IST"
            delay(1000)
        }
    }

    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {

        val stageWidth = constraints.maxWidth.toFloat()
        val stageHeight = constraints.maxHeight.toFloat()

        val safeMargin = with(density) {
            16.dp.toPx()
        }
                if (isEditable && snapToGrid) {
            Canvas(modifier = Modifier.fillMaxSize()) {

                val grid = 32.dp.toPx()

                var x = 0f
                while (x <= size.width) {
                    drawLine(
                        color = Color(0x22FFFFFF),
                        start = Offset(x, 0f),
                        end = Offset(x, size.height)
                    )
                    x += grid
                }

                var y = 0f
                while (y <= size.height) {
                    drawLine(
                        color = Color(0x22FFFFFF),
                        start = Offset(0f, y),
                        end = Offset(size.width, y)
                    )
                    y += grid
                }
            }
        }

        overlays
            .filter { it.isVisible }
            .sortedBy { it.zIndex }
            .forEach { overlay ->

                val isSelected =
                    isEditable &&
                    overlay.id == selectedOverlayId

                var overlayWidth by remember(overlay.id) {
                    mutableFloatStateOf(180f)
                }

                var overlayHeight by remember(overlay.id) {
                    mutableFloatStateOf(70f)
                }

                var currentScale by remember(
                    overlay.id,
                    overlay.scale
                ) {
                    mutableFloatStateOf(overlay.scale)
                }

                var currentRotation by remember(
                    overlay.id,
                    overlay.rotation
                ) {
                    mutableFloatStateOf(overlay.rotation)
                }

                val startX =
                    overlay.xRatio * stageWidth

                val startY =
                    overlay.yRatio * stageHeight
                                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                startX.roundToInt(),
                                startY.roundToInt()
                            )
                        }
                        .graphicsLayer {
                            scaleX = currentScale
                            scaleY = currentScale
                            rotationZ = currentRotation
                            alpha = overlay.opacity
                        }
                        .onGloballyPositioned {
                            overlayWidth = it.size.width.toFloat()
                            overlayHeight = it.size.height.toFloat()
                        }
                        .clickable(enabled = isEditable) {
                            onSelectOverlay(overlay.id)
                        }
                        .then(
                            if (
                                isEditable &&
                                !isStageLocked &&
                                !overlay.isLocked
                            ) {

                                Modifier.pointerInput(
                                    overlay.id,
                                    currentScale,
                                    currentRotation,
                                    snapToGrid
                                ) {

                                    detectTransformGestures { _, pan, zoom, rotation ->

                                        currentScale =
                                            (currentScale * zoom)
                                                .coerceIn(0.30f, 4f)

                                        currentRotation += rotation

                                        var newX = startX + pan.x
                                        var newY = startY + pan.y

                                        val maxX =
                                            (stageWidth - overlayWidth * currentScale)
                                                .coerceAtLeast(safeMargin)

                                        val maxY =
                                            (stageHeight - overlayHeight * currentScale)
                                                .coerceAtLeast(safeMargin)
                                                                                        newX = newX.coerceIn(
                                            safeMargin,
                                            maxX
                                        )

                                        newY = newY.coerceIn(
                                            safeMargin,
                                            maxY
                                        )

                                        var xRatio = newX / stageWidth
                                        var yRatio = newY / stageHeight

                                        if (snapToGrid) {
                                            xRatio =
                                                (xRatio * 20)
                                                    .roundToInt() / 20f

                                            yRatio =
                                                (yRatio * 20)
                                                    .roundToInt() / 20f
                                        }

                                        onUpdatePosition(
                                            overlay.id,
                                            xRatio,
                                            yRatio
                                        )

                                        onUpdateScale(
                                            overlay.id,
                                            currentScale
                                        )

                                        onUpdateRotation(
                                            overlay.id,
                                            currentRotation
                                        )
                                    }
                                }

                            } else {
                                Modifier
                            }
                        )
                ) {
                                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(getOverlayBackgroundColor(overlay.type))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected)
                                    Color(0xFFFF0B3A)
                                else
                                    Color.White.copy(alpha = 0.20f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(
                                horizontal = 12.dp,
                                vertical = 8.dp
                            )
                    ) {

                        RenderOverlayContent(
                            overlay = overlay,
                            liveTimeStr = currentTime
                        )
                    }

                    if (isSelected) {

                        Canvas(
                            modifier = Modifier.matchParentSize()
                        ) {
                            drawRect(
                                color = Color(0xFFFF0B3A),
                                style = Stroke(width = 3f)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (-44).dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF101010))
                                .border(
                                    1.dp,
                                    Color(0xFFFF0B3A),
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(
                                    horizontal = 10.dp,
                                    vertical = 6.dp
                                )
                        ) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Lock",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable {
                                            onToggleLock(overlay.id)
                                        }
                                )

                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Duplicate",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable {
                                            onDuplicateOverlay(overlay.id)
                                        }
                                )

                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Bring Forward",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable {
                                            onBringForward(overlay.id)
                                        }
                                )

                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Send Backward",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable {
                                            onSendBackward(overlay.id)
                                        }
                                )

                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color(0xFFFF3B30),
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable {
                                            onDeleteOverlay(overlay.id)
                                        }
                                )
                            }
                        }
                    }
                                }
                                @Composable
private fun RenderOverlayContent(
    overlay: OverlayItem,
    liveTimeStr: String
) {

    when (overlay.type) {

        OverlayType.WEBCAM,
        OverlayType.CAMERA_PIP -> {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF22C55E))
                )

                Column {

                    Text(
                        text = "CAMERA",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )

                    Text(
                        text = "1080P • LIVE",
                        color = Color.LightGray,
                        fontSize = 9.sp
                    )
                }
            }
        }

        OverlayType.SCREEN_CAPTURE -> {

            Column {

                Text(
                    text = "SCREEN CAPTURE",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )

                Text(
                    text = "MediaProjection Active",
                    color = Color(0xFF38BDF8),
                    fontSize = 9.sp
                )
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

                    Text(
                        text = "GT",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = overlay.name.ifBlank {
                        "GT ESPORTS INDIA"
                    },
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
                    .background(Color(0xFFFF0B3A))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {

                Text(
                    text = "GT LIVE OFFICIAL",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
        }
                OverlayType.CLOCK,
        OverlayType.DATE_TIME -> {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                Text(
                    text = "TIME",
                    color = Color(0xFFFFD54F),
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )

                Text(
                    text = liveTimeStr,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }

        OverlayType.TEXT -> {

            Text(
                text = overlay.textContent.ifBlank {
                    "GT ESPORTS LIVE"
                },
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }

        OverlayType.SUBSCRIBER_COUNTER -> {

            Column {

                Text(
                    text = "SUBSCRIBER GOAL",
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Box(
                    modifier = Modifier
                        .width(120.dp)
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

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "${overlay.goalCurrent}/${overlay.goalTarget}",
                    color = Color.White,
                    fontSize = 9.sp
                )
            }
        }

        OverlayType.VIEWER_COUNTER -> {

            Text(
                text = "LIVE • 3420 Viewers",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }

        OverlayType.DONATION_GOAL -> {

            Column {

                Text(
                    text = "DONATION GOAL",
                    color = Color(0xFFEC4899),
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )

                Text(
                    text = "₹25,000 / ₹50,000",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
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
}private fun getOverlayBackgroundColor(
    type: OverlayType
): Color {

    return when (type) {

        OverlayType.WEBCAM,
        OverlayType.CAMERA_PIP ->
            Color(0xFF1D4ED8).copy(alpha = 0.85f)

        OverlayType.SCREEN_CAPTURE ->
            Color(0xFF0284C7).copy(alpha = 0.85f)

        OverlayType.LOGO,
        OverlayType.WATERMARK ->
            Color(0xFFFF0B3A).copy(alpha = 0.90f)

        OverlayType.SUBSCRIBER_COUNTER,
        OverlayType.DONATION_GOAL ->
            Color(0xFF1E1E2A).copy(alpha = 0.95f)

        OverlayType.CLOCK,
        OverlayType.DATE_TIME ->
            Color(0xFF0F172A).copy(alpha = 0.90f)

        OverlayType.VIEWER_COUNTER ->
            Color(0xFF111827).copy(alpha = 0.90f)

        OverlayType.TEXT ->
            Color(0xFF111111).copy(alpha = 0.90f)

        else ->
            Color(0xFF0A0A0E).copy(alpha = 0.90f)
    }
}
