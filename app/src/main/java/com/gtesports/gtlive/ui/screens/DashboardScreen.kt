package com.gtesports.gtlive.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gtesports.gtlive.ui.theme.GTBackgroundBlack
import com.gtesports.gtlive.ui.theme.GTBorderDark
import com.gtesports.gtlive.ui.theme.GTRedPrimary
import com.gtesports.gtlive.ui.theme.GTSurfaceCard
import com.gtesports.gtlive.ui.theme.GTTextMuted
import com.gtesports.gtlive.ui.theme.GTTextPrimary
import com.gtesports.gtlive.ui.theme.GTTextSecondary

@Composable
fun DashboardScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GTBackgroundBlack)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "GT ESPORTS INDIA",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = GTRedPrimary,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Broadcast Dashboard",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = GTTextPrimary
                    )
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1E1E26))
                    .border(1.dp, GTBorderDark, RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "READY TO BROADCAST",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color(0xFF10B981),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Stats Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatBox(title = "Total Streams", value = "48", modifier = Modifier.weight(1f))
            StatBox(title = "Peak Viewers", value = "12.4K", modifier = Modifier.weight(1f))
            StatBox(title = "Watch Hours", value = "842 hrs", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "BROADCAST CONTROLS",
            style = MaterialTheme.typography.labelMedium.copy(
                color = GTTextSecondary,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Grid Menu
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            item {
                DashboardCard(
                    title = "Camera Live",
                    subtitle = "CameraX Full HD Studio",
                    badge = "1080p60",
                    onClick = { navController.navigate("camera_live") }
                )
            }
            item {
                DashboardCard(
                    title = "Screen Live",
                    subtitle = "MediaProjection Game Capture",
                    badge = "BGMI / Valo",
                    onClick = { navController.navigate("screen_live") }
                )
            }
            item {
                DashboardCard(
                    title = "Scene & Overlay Studio",
                    subtitle = "Overlays, Scenes & Sources",
                    badge = "Editor",
                    highlight = true,
                    onClick = { navController.navigate("scene_editor") }
                )
            }
            item {
                DashboardCard(
                    title = "Stream Manager",
                    subtitle = "Title, Thumbnail & Quality",
                    badge = "Setup",
                    onClick = { navController.navigate("stream_manager") }
                )
            }
            item {
                DashboardCard(
                    title = "Go Live",
                    subtitle = "YouTube API & Live Chat",
                    badge = "Broadcast",
                    onClick = { navController.navigate("go_live") }
                )
            }
            item {
                DashboardCard(
                    title = "Live Analytics",
                    subtitle = "Bitrate, FPS & Viewers",
                    badge = "Stats",
                    onClick = { navController.navigate("analytics") }
                )
            }
        }
    }
}

@Composable
fun StatBox(title: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(GTSurfaceCard)
            .border(1.dp, GTBorderDark, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(text = title, color = GTTextMuted, fontSize = 9.5.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, color = GTTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    subtitle: String,
    badge: String,
    highlight: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (highlight) GTRedPrimary else GTSurfaceCard)
            .border(1.dp, if (highlight) GTRedPrimary else GTBorderDark, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (highlight) Color.Black.copy(alpha = 0.3f) else Color(0xFF282834))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = badge, color = GTTextPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = GTTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 10.sp,
                    color = if (highlight) Color.White.copy(alpha = 0.85f) else GTTextMuted
                )
            )
        }
    }
}
