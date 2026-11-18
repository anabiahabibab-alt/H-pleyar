package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MediaType
import com.example.player.PlayerState
import com.example.ui.theme.LarkBorder
import com.example.ui.theme.LarkDarkBackground
import com.example.ui.theme.LarkDarkCard
import com.example.ui.theme.LarkPurpleAccent
import com.example.ui.theme.LarkTealPrimary
import com.example.ui.theme.LarkTealSecondary
import com.example.ui.theme.LarkTextMuted
import com.example.ui.theme.LarkTextPrimary
import com.example.ui.theme.LarkTextSecondary

@Composable
fun ToolsScreen(
    playerState: PlayerState,
    onOpenEqualizer: () -> Unit,
    onOpenSleepTimer: () -> Unit,
    onScanStorage: () -> Unit,
    onAddCustomStream: (String, String, MediaType) -> Unit,
    modifier: Modifier = Modifier
) {
    var showStreamDialog by remember { mutableStateOf(false) }
    var streamTitle by remember { mutableStateOf("") }
    var streamUrl by remember { mutableStateOf("") }
    var streamType by remember { mutableStateOf(MediaType.VIDEO) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LarkDarkBackground)
            .padding(horizontal = 16.dp)
            .testTag("tools_screen")
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "H Player Tools & Features",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = LarkTextPrimary
        )
        Text(
            text = "Dark & Red edition • Enhance your audio & video experience",
            style = MaterialTheme.typography.bodySmall,
            color = LarkTextMuted
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Equalizer Tool Card
            item {
                ToolActionCard(
                    icon = Icons.Default.GraphicEq,
                    iconBg = LarkTealPrimary,
                    title = "Graphic Equalizer",
                    subtitle = "Current preset: ${playerState.selectedPresetName} • Bass & 3D Surround",
                    onClick = onOpenEqualizer
                )
            }

            // Sleep Timer Tool Card
            item {
                ToolActionCard(
                    icon = Icons.Default.Bedtime,
                    iconBg = LarkPurpleAccent,
                    title = "Sleep Timer",
                    subtitle = if (playerState.sleepTimerActive) {
                        "Active: ${playerState.sleepTimerMinutesLeft} min remaining"
                    } else "Auto-turn off music & video playback",
                    onClick = onOpenSleepTimer
                )
            }

            // Network Stream / URL Player
            item {
                ToolActionCard(
                    icon = Icons.Default.Link,
                    iconBg = LarkTealSecondary,
                    title = "Play Network Stream",
                    subtitle = "Stream online MP4 videos or MP3 audio from URL",
                    onClick = { showStreamDialog = true }
                )
            }

            // Scan Device Media
            item {
                ToolActionCard(
                    icon = Icons.Default.Refresh,
                    iconBg = Color(0xFF10B981),
                    title = "Scan Device Storage",
                    subtitle = "Refresh videos & music from device folders",
                    onClick = onScanStorage
                )
            }

            // Lark Player Feature Highlights
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = LarkDarkCard),
                    border = BorderStroke(1.dp, LarkBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "H PLAYER SIGNATURE FEATURES",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = LarkTealPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        FeatureBullet(
                            title = "Background Video Audio Play",
                            desc = "Tap headphones icon in video player to keep listening while browsing or with screen dimmed."
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FeatureBullet(
                            title = "Smart Gesture Controls",
                            desc = "Swipe left for brightness, right for volume, and horizontal to seek."
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FeatureBullet(
                            title = "Floating Mini-Player",
                            desc = "Persistent bottom player with progress tracking across all views."
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FeatureBullet(
                            title = "Synchronized Karaoke Lyrics",
                            desc = "Real-time scrolling lyrics that highlight and seek with taps."
                        )
                    }
                }
            }
        }
    }

    if (showStreamDialog) {
        AlertDialog(
            onDismissRequest = { showStreamDialog = false },
            containerColor = LarkDarkCard,
            title = {
                Text(
                    text = "Play Network Stream",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = LarkTextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = streamTitle,
                        onValueChange = { streamTitle = it },
                        label = { Text("Stream Title", color = LarkTextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LarkTealPrimary,
                            unfocusedBorderColor = LarkBorder,
                            focusedTextColor = LarkTextPrimary,
                            unfocusedTextColor = LarkTextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = streamUrl,
                        onValueChange = { streamUrl = it },
                        label = { Text("Stream URL (MP4 / MP3)", color = LarkTextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LarkTealPrimary,
                            unfocusedBorderColor = LarkBorder,
                            focusedTextColor = LarkTextPrimary,
                            unfocusedTextColor = LarkTextPrimary
                        )
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { streamType = MediaType.VIDEO }
                        ) {
                            RadioButton(
                                selected = streamType == MediaType.VIDEO,
                                onClick = { streamType = MediaType.VIDEO },
                                colors = RadioButtonDefaults.colors(selectedColor = LarkTealPrimary)
                            )
                            Text("Video", color = LarkTextPrimary, style = MaterialTheme.typography.bodySmall)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { streamType = MediaType.AUDIO }
                        ) {
                            RadioButton(
                                selected = streamType == MediaType.AUDIO,
                                onClick = { streamType = MediaType.AUDIO },
                                colors = RadioButtonDefaults.colors(selectedColor = LarkTealPrimary)
                            )
                            Text("Audio", color = LarkTextPrimary, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (streamUrl.isNotBlank()) {
                            onAddCustomStream(streamTitle, streamUrl, streamType)
                            streamTitle = ""
                            streamUrl = ""
                            showStreamDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LarkTealPrimary)
                ) {
                    Text("Play Now", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showStreamDialog = false }) {
                    Text("Cancel", color = LarkTextSecondary)
                }
            }
        )
    }
}

@Composable
fun ToolActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = LarkDarkCard),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, LarkBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconBg,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = LarkTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = LarkTextMuted
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = LarkTextSecondary
            )
        }
    }
}

@Composable
fun FeatureBullet(
    title: String,
    desc: String
) {
    Column {
        Text(
            text = "• $title",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = LarkTextPrimary
        )
        Text(
            text = desc,
            style = MaterialTheme.typography.bodySmall,
            color = LarkTextMuted,
            modifier = Modifier.padding(start = 12.dp, top = 2.dp)
        )
    }
}
