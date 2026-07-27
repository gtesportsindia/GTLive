package com.gtesports.gtlive.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.gtesports.gtlive.model.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
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

        val safeMargin = with(density) { 16.dp.toPx() }

        overlays
            .filter { it.isVisible }
            .sortedBy { it.zIndex }
            .forEach { overlay ->
                            val isSelected = isEditable && overlay.id == selectedOverlayId

            var overlayWidth by remember(overlay.id) { mutableFloatStateOf(180f) }
            var overlayHeight by remember(overlay.id) { mutableFloatStateOf(70f) }

            var currentScale by remember(overlay.id, overlay.scale) {
                mutableFloatStateOf(overlay.scale)
            }

            var currentRotation by remember(overlay.id, overlay.rotation) {
                mutableFloatStateOf(overlay.rotation)
            }

            val startX = overlay.xRatio * stageWidth
            val startY = overlay.yRatio * stageHeight

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
                        if (isEditable && !isStageLocked && !overlay.isLocked) {

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

                                    newX =
                                        newX.coerceIn(
                                            safeMargin,
                                            maxX
                                        )

                                    newY =
                                        newY.coerceIn(
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
                
