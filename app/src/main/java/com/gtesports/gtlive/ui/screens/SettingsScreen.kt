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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun SettingsScreen(navController: NavController) {
    var selectedRes by remember { mutableStateOf("1080p") }
    var selectedFps by remember { mutableStateOf("60 FPS") }
    var selectedBitrate by remember { mutableStateOf("6400 Kbps") }
    var selectedOrientation by remember { mutableStateOf("LANDSCAPE") }
    var selectedTheme by remember { mutableStateOf("GT DARK") }
    var selectedLanguage by remember { mutableStateOf("English") }

    var noiseSuppression by remember { mutableStateOf(true) }
    var autoReconnect by remember { mutableStateOf(true) }
    var ultraLowLatency by remember { mutableStateOf(true) }
    var backupStatusText by remember { mutableStateOf("") }

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
                text = "GLOBAL APP SETTINGS",
                color = GTTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Resolution
        SettingSelectorGroup("STREAM RESOLUTION", listOf("720p", "1080p", "1440p", "4K"), selectedRes) { selectedRes = it }
        Spacer(modifier = Modifier.height(12.dp))

        // 2. FPS
        SettingSelectorGroup("TARGET FRAME RATE", listOf("30 FPS", "60 FPS"), selectedFps) { selectedFps = it }
        Spacer(modifier = Modifier.height(12.dp))

        // 3. Bitrate
        SettingSelectorGroup("TARGET BITRATE", listOf("3500 Kbps", "6400 Kbps", "8000 Kbps", "12000 Kbps"), selectedBitrate) { selectedBitrate = it }
        Spacer(modifier = Modifier.height(12.dp))

        // 4. Orientation
        SettingSelectorGroup("DEFAULT ORIENTATION", listOf("LANDSCAPE", "PORTRAIT"), selectedOrientation) { selectedOrientation = it }
        Spacer(modifier = Modifier.height(12.dp))

        // 5. Theme
        SettingSelectorGroup("APP UI THEME", listOf("GT DARK", "CYBERPUNK", "AMOLED BLACK"), selectedTheme) { selectedTheme = it }
        Spacer(modifier = Modifier.height(12.dp))

        // 6. Language
        SettingSelectorGroup("INTERFACE LANGUAGE", listOf("English", "Hindi", "Bengali", "Tamil"), selectedLanguage) { selectedLanguage = it }
        Spacer(modifier = Modifier.height(16.dp))

        // Toggles
        SettingToggle("Microphone Noise Suppression", "Suppress background keyboard & fan noise", noiseSuppression) { noiseSuppression = it }
        Spacer(modifier = Modifier.height(10.dp))

        SettingToggle("Auto RTMP Reconnect", "Resume broadcast on Wi-Fi/5G network handoff", autoReconnect) { autoReconnect = it }
        Spacer(modifier = Modifier.height(10.dp))

        SettingToggle("Ultra-Low Latency Mode", "Sub-2 second delay for instant live chat", ultraLowLatency) { ultraLowLatency = it }
        Spacer(modifier = Modifier.height(16.dp))

        // Backup & Restore
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(GTSurfaceCard)
                .padding(14.dp)
        ) {
            Text(text = "BACKUP & RESTORE CONFIGURATION", color = GTTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Export or import your RTMP stream keys, scene layouts and audio presets.", color = GTTextMuted, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { backupStatusText = "Settings exported to /sdcard/gtlive_backup.json" },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                ) {
                    Text(text = "EXPORT BACKUP", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Button(
                    onClick = { backupStatusText = "Backup successfully restored!" },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text(text = "RESTORE CONFIG", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }

            if (backupStatusText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = backupStatusText, color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GTSurfaceCard)
        ) {
            Text(text = "LOGOUT FROM GT LIVE", color = GTRedPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
fun SettingSelectorGroup(
    title: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(GTSurfaceCard)
            .padding(12.dp)
    ) {
        Text(text = title, color = GTTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            options.forEach { option ->
                val isSelected = selected == option
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) GTRedPrimary else Color(0xFF181820))
                        .clickable { onSelect(option) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        color = if (isSelected) Color.White else GTTextMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SettingToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(GTSurfaceCard, shape = RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = GTTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = GTTextMuted, fontSize = 10.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = GTTextPrimary,
                checkedTrackColor = GTRedPrimary
            )
        )
    }
}
