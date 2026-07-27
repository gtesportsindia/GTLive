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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
fun StreamManagerScreen(
    navController: NavController,
    streamViewModel: StreamViewModel = viewModel()
) {
    val details by streamViewModel.broadcastDetails.collectAsState()

    var title by remember { mutableStateOf(details.title) }
    var description by remember { mutableStateOf(details.description) }
    var privacy by remember { mutableStateOf(details.privacy) }
    var category by remember { mutableStateOf(details.category) }
    var scheduledTime by remember { mutableStateOf("2026-07-27 18:00 IST") }
    var tagsInput by remember { mutableStateOf(details.tags.joinToString(", ")) }

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
                Text(text = "← BACK TO DASHBOARD", color = GTRedPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }

            Text(
                text = "STREAM MANAGER",
                color = GTTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Broadcast Title Input
        Text(text = "BROADCAST TITLE", color = GTTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = GTSurfaceCard, unfocusedContainerColor = GTSurfaceCard,
                focusedBorderColor = GTRedPrimary, unfocusedBorderColor = GTBorderDark,
                focusedTextColor = GTTextPrimary, unfocusedTextColor = GTTextPrimary
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Description Input
        Text(text = "STREAM DESCRIPTION", color = GTTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = GTSurfaceCard, unfocusedContainerColor = GTSurfaceCard,
                focusedBorderColor = GTRedPrimary, unfocusedBorderColor = GTBorderDark,
                focusedTextColor = GTTextPrimary, unfocusedTextColor = GTTextPrimary
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Privacy Options
        Text(text = "YOUTUBE PRIVACY STATUS", color = GTTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("PUBLIC", "UNLISTED", "PRIVATE").forEach { option ->
                val isSelected = privacy == option
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) GTRedPrimary else GTSurfaceCard)
                        .clickable { privacy = option }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = option, color = if (isSelected) Color.White else GTTextMuted, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Category Picker
        Text(text = "STREAM CATEGORY", color = GTTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Gaming", "Esports", "Mobile Gaming", "IRL").forEach { cat ->
                val isSelected = category == cat
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color(0xFF3B82F6) else GTSurfaceCard)
                        .clickable { category = cat }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = cat, color = if (isSelected) Color.White else GTTextMuted, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Schedule Stream & Tags
        Text(text = "SCHEDULED START TIME", color = GTTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = scheduledTime,
            onValueChange = { scheduledTime = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = GTSurfaceCard, unfocusedContainerColor = GTSurfaceCard,
                focusedBorderColor = GTRedPrimary, unfocusedBorderColor = GTBorderDark,
                focusedTextColor = GTTextPrimary, unfocusedTextColor = GTTextPrimary
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(text = "SEARCH TAGS (COMMA SEPARATED)", color = GTTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = tagsInput,
            onValueChange = { tagsInput = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = GTSurfaceCard, unfocusedContainerColor = GTSurfaceCard,
                focusedBorderColor = GTRedPrimary, unfocusedBorderColor = GTBorderDark,
                focusedTextColor = GTTextPrimary, unfocusedTextColor = GTTextPrimary
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                streamViewModel.updateBroadcastDetails(
                    title = title,
                    desc = description,
                    privacy = privacy,
                    category = category,
                    scheduledTime = scheduledTime,
                    tagsStr = tagsInput
                )
                navController.navigate("go_live")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GTRedPrimary)
        ) {
            Text(text = "SAVE METADATA & GO TO LIVE STUDIO", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}
