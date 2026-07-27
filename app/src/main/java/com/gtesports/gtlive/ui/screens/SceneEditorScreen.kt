package com.gtesports.gtlive.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gtesports.gtlive.model.OverlayType
import com.gtesports.gtlive.model.SceneItem
import com.gtesports.gtlive.model.SceneType
import com.gtesports.gtlive.ui.components.RealOverlayCanvas
import com.gtesports.gtlive.viewmodel.StreamViewModel

@Composable
fun SceneEditorScreen(
    navController: NavController,
    streamViewModel: StreamViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        streamViewModel.initStorage(context)
    }

    val scenes by streamViewModel.scenes.collectAsState()
    val overlays by streamViewModel.overlays.collectAsState()
    val selectedOverlayId by streamViewModel.selectedOverlayId.collectAsState()
    val sceneConfig by streamViewModel.sceneConfig.collectAsState()

    val activeScene = scenes.find { it.id == sceneConfig.activeSceneId } ?: scenes.firstOrNull() ?: SceneItem(
        "sc_gameplay", "BGMI GAMEPLAY", SceneType.SCREEN
    )
    val selectedOverlay = overlays.find { it.id == selectedOverlayId }

    var isAddSourceOpen by remember { mutableStateOf(false) }
    var isLayersOpen by remember { mutableStateOf(false) }
    var isScenesOpen by remember { mutableStateOf(false) }
    var isStageLocked by remember { mutableStateOf(false) }
    var snapToGrid by remember { mutableStateOf(sceneConfig.snapToGrid) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // REAL OVERLAY ENGINE PREVIEW & EDITING CANVAS
        RealOverlayCanvas(
            overlays = overlays,
            selectedOverlayId = selectedOverlayId,
            isEditable = true,
            snapToGrid = snapToGrid,
            isStageLocked = isStageLocked,
            onSelectOverlay = { streamViewModel.selectOverlay(it) },
            onUpdatePosition = { id, x, y -> streamViewModel.updateOverlayPosition(id, x, y) },
            onUpdateScale = { id, s -> streamViewModel.updateOverlayScale(id, s) },
            onUpdateRotation = { id, r -> streamViewModel.updateOverlayRotation(id, r) },
            onDeleteOverlay = { id -> streamViewModel.deleteOverlay(id) },
            onToggleLock = { id -> streamViewModel.toggleOverlayLock(id) },
            onDuplicateOverlay = { id -> streamViewModel.duplicateOverlay(id) },
            onBringForward = { id -> streamViewModel.bringOverlayForward(id) },
            onSendBackward = { id -> streamViewModel.sendOverlayBackward(id) }
        )

        // TOP FLOATING HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
                    .clickable { navController.popBackStack() }
                    .padding(8.dp)
            ) {
                Text(text = "←", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.8f))
                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "PRISM LIVE STUDIO • ${activeScene.name}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (snapToGrid) Color.White else Color.Black.copy(alpha = 0.8f))
                    .clickable { snapToGrid = !snapToGrid }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (snapToGrid) "SNAP ON" else "SNAP OFF",
                    color = if (snapToGrid) Color.Black else Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // SELECTED OVERLAY OPACITY SLIDER BAR
        selectedOverlay?.let { currentOverlay ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF12121A)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 70.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "OPACITY: ${(currentOverlay.opacity * 100).toInt()}% • ${currentOverlay.name}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = if (currentOverlay.isLocked) "🔒 Locked" else "🔓 Lock",
                                color = if (currentOverlay.isLocked) Color.Yellow else Color.Gray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { streamViewModel.toggleOverlayLock(currentOverlay.id) }
                            )
                            Text(
                                text = "📋 Clone",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { streamViewModel.duplicateOverlay(currentOverlay.id) }
                            )
                            Text(
                                text = "🗑️ Delete",
                                color = Color(0xFFFF0B3A),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { streamViewModel.deleteOverlay(currentOverlay.id) }
                            )
                        }
                    }
                    Slider(
                        value = currentOverlay.opacity,
                        onValueChange = { streamViewModel.updateOverlayOpacity(currentOverlay.id, it) },
                        valueRange = 0.1f..1.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFF0B3A),
                            activeTrackColor = Color(0xFFFF0B3A)
                        )
                    )
                }
            }
        }

        // FLOATING "+" BUTTON TO ADD SOURCES
        FloatingActionButton(
            onClick = { isAddSourceOpen = true },
            containerColor = Color.White,
            contentColor = Color.Black,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = selectedOverlay?.let { 160.dp } ?: 70.dp, end = 16.dp)
                .size(56.dp)
        ) {
            Text(text = "+", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }

        // BOTTOM TOOLBAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color.Black)
                .border(1.dp, Color.White.copy(alpha = 0.2f))
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.clickable { isAddSourceOpen = true }) {
                Text(text = "+ Add Source", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
            Box(modifier = Modifier.clickable { isLayersOpen = true }) {
                Text(text = "Layers (${overlays.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
            Box(modifier = Modifier.clickable { snapToGrid = !snapToGrid }) {
                Text(text = "Grid", color = if (snapToGrid) Color.White else Color.Gray, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
            Box(modifier = Modifier.clickable { isScenesOpen = true }) {
                Text(text = "Scenes", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
            Box(modifier = Modifier.clickable { isStageLocked = !isStageLocked }) {
                Text(text = if (isStageLocked) "Locked" else "Lock Stage", color = if (isStageLocked) Color.Yellow else Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }

        // ADD SOURCE BOTTOM SHEET
        if (isAddSourceOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .clickable { isAddSourceOpen = false },
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .background(Color(0xFF0C0C10))
                        .padding(16.dp)
                        .clickable(enabled = false) {}
                ) {
                    Text(text = "ADD OVERLAY SOURCE", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    listOf(
                        Pair(OverlayType.SCREEN_CAPTURE, "📱 Screen Capture Stream"),
                        Pair(OverlayType.WEBCAM, "📷 Camera PIP Frame"),
                        Pair(OverlayType.LOGO, "🏆 GT Esports Logo"),
                        Pair(OverlayType.WATERMARK, "🏷️ GT Live Watermark"),
                        Pair(OverlayType.TEXT, "📝 Custom Text Ticker"),
                        Pair(OverlayType.CLOCK, "⏰ Live Real-Time Clock"),
                        Pair(OverlayType.SUBSCRIBER_COUNTER, "🎯 Subscriber Goal Bar"),
                        Pair(OverlayType.VIEWER_COUNTER, "👁️ Live Viewer Badge"),
                        Pair(OverlayType.DONATION_GOAL, "💖 Donation Goal Bar"),
                        Pair(OverlayType.IMAGE, "🖼️ Custom PNG/JPG Graphic")
                    ).forEach { (type, name) ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF14141C))
                                .clickable {
                                    streamViewModel.addOverlay(type, name)
                                    isAddSourceOpen = false
                                }
                                .padding(12.dp)
                        ) {
                            Text(text = "+ Add $name", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // LAYERS BOTTOM SHEET
        if (isLayersOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .clickable { isLayersOpen = false },
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .background(Color(0xFF0C0C10))
                        .padding(16.dp)
                        .clickable(enabled = false) {}
                ) {
                    Text(text = "LAYERS & STACK ORDER", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(modifier = Modifier.heightIn(max = 260.dp)) {
                        items(overlays.sortedByDescending { it.zIndex }) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF14141C))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = if (item.isVisible) "👁️" else "🙈",
                                        modifier = Modifier.clickable { streamViewModel.toggleOverlayVisibility(item.id) }
                                    )
                                    Text(
                                        text = if (item.isLocked) "🔒" else "🔓",
                                        modifier = Modifier.clickable { streamViewModel.toggleOverlayLock(item.id) }
                                    )
                                    Text(text = item.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        text = "↑",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        modifier = Modifier.clickable { streamViewModel.bringOverlayForward(item.id) }
                                    )
                                    Text(
                                        text = "↓",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        modifier = Modifier.clickable { streamViewModel.sendOverlayBackward(item.id) }
                                    )
                                    Text(
                                        text = "🗑️",
                                        modifier = Modifier.clickable { streamViewModel.deleteOverlay(item.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // SCENES BOTTOM SHEET
        if (isScenesOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .clickable { isScenesOpen = false },
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .background(Color(0xFF0C0C10))
                        .padding(16.dp)
                        .clickable(enabled = false) {}
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "SCENE MANAGER", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        Text(
                            text = "+ New Scene",
                            color = Color(0xFFFF0B3A),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.clickable {
                                streamViewModel.createScene("CUSTOM SCENE ${scenes.size + 1}", SceneType.CUSTOM)
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    scenes.forEach { sc ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (sc.id == activeScene.id) Color.White else Color(0xFF14141C))
                                .clickable {
                                    streamViewModel.selectScene(sc.id)
                                    isScenesOpen = false
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = sc.name,
                                color = if (sc.id == activeScene.id) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "📋",
                                    modifier = Modifier.clickable { streamViewModel.duplicateScene(sc.id) }
                                )
                                if (scenes.size > 1) {
                                    Text(
                                        text = "🗑️",
                                        modifier = Modifier.clickable { streamViewModel.deleteScene(sc.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
