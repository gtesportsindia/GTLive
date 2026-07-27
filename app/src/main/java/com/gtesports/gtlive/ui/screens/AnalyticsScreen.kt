package com.gtesports.gtlive.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
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
fun AnalyticsScreen(
    navController: NavController,
    streamViewModel: StreamViewModel = viewModel()
) {
    val analytics by streamViewModel.analytics.collectAsState()

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
                Text(text = "← DASHBOARD", color = GTRedPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }

            Text(
                text = "REALTIME STREAM ANALYTICS",
                color = GTTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Live Health Overview Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(GTSurfaceCard)
                .border(1.dp, GTBorderDark, RoundedCornerShape(14.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "STREAM HEALTH STATUS", color = GTTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = analytics.healthStatus, color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF10B981).copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(text = "${analytics.bitrateKbps} KBPS RTMP", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bitrate & FPS Live Visual Graph
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF0F0F14))
                .border(1.dp, GTBorderDark, RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "graph")
            val offset by infiniteTransition.animateFloat(
                initialValue = 0f, targetValue = 50f,
                animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Restart),
                label = "offset"
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                val path = Path()
                path.moveTo(0f, h * 0.5f)
                var x = 0f
                while (x < w) {
                    val y = (h * 0.5f) + Math.sin((x + offset * 4) * 0.04).toFloat() * 20f
                    path.lineTo(x, y)
                    x += 12f
                }
                drawPath(path, color = Color(0xFF10B981), style = Stroke(width = 2.5.dp.toPx()))
            }

            Column {
                Text(text = "LIVE BITRATE & FPS STABILITY", color = GTTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(text = "${analytics.fps} FPS | ${analytics.bitrateKbps} Kbps", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid of Real-Time System Metrics
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AnalyticsCard("FRAME RATE (FPS)", "${analytics.fps} FPS", "Target: 60 FPS", modifier = Modifier.weight(1f))
                AnalyticsCard("NETWORK BITRATE", "${analytics.bitrateKbps} Kbps", "Zero Spikes", modifier = Modifier.weight(1f))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AnalyticsCard("CPU UTILIZATION", "${analytics.cpuUsagePercent}%", "Snapdragon 8 Gen 2", modifier = Modifier.weight(1f))
                AnalyticsCard("RAM MEMORY", "${analytics.ramUsagePercent}%", "3.8 GB / 12 GB", modifier = Modifier.weight(1f))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AnalyticsCard("NETWORK SPEED", "${analytics.networkSpeedMbps} Mbps", "5G Low Latency", modifier = Modifier.weight(1f))
                AnalyticsCard("DROPPED FRAMES", "${analytics.droppedFramesPercent}%", "0 Frames Dropped", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun AnalyticsCard(title: String, value: String, detail: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(GTSurfaceCard)
            .border(1.dp, GTBorderDark, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(text = title, color = GTTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = GTTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = detail, color = Color(0xFF10B981), fontSize = 10.sp)
        }
    }
}
