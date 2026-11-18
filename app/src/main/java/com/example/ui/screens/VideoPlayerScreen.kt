package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.provider.Settings
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.VideoAspectRatio
import com.example.player.MediaPlayerController
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VideoPlayerScreen(
    playerState: PlayerState,
    playerController: MediaPlayerController,
    onBack: () -> Unit,
    onPlayAsAudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentMedia = playerState.currentMedia ?: return

    var controlsVisible by remember { mutableStateOf(true) }
    var showSpeedMenu by remember { mutableStateOf(false) }

    // Gesture feedback HUD states
    var brightnessPercent by remember { mutableIntStateOf(70) }
    var showBrightnessHud by remember { mutableStateOf(false) }

    var volumePercent by remember { mutableIntStateOf(80) }
    var showVolumeHud by remember { mutableStateOf(false) }

    var seekDeltaSeconds by remember { mutableLongStateOf(0L) }
    var showSeekHud by remember { mutableStateOf(false) }

    val audioManager = remember {
        context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    }

    // Auto-hide controls after 4 seconds of inactivity
    LaunchedEffect(controlsVisible, playerState.isPlaying) {
        if (controlsVisible && playerState.isPlaying && !playerState.isControlsLocked) {
            delay(4000)
            controlsVisible = false
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("video_player_screen")
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        // Video Surface
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val aspectModifier = when (playerState.videoAspectRatio) {
                VideoAspectRatio.FIT -> Modifier.fillMaxSize()
                VideoAspectRatio.FILL -> Modifier.fillMaxSize()
                VideoAspectRatio.SIXTEEN_NINE -> Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                VideoAspectRatio.FOUR_THREE -> Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
            }

            AndroidView(
                factory = { ctx ->
                    SurfaceView(ctx).apply {
                        holder.addCallback(object : SurfaceHolder.Callback {
                            override fun surfaceCreated(holder: SurfaceHolder) {
                                playerController.setSurface(holder.surface)
                            }
                            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
                            override fun surfaceDestroyed(holder: SurfaceHolder) {
                                playerController.setSurface(null)
                            }
                        })
                    }
                },
                modifier = aspectModifier
            )
        }

        // Gesture Touch Layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(playerState.isControlsLocked) {
                    if (playerState.isControlsLocked) {
                        detectTapGestures {
                            controlsVisible = !controlsVisible
                        }
                    } else {
                        detectTapGestures(
                            onTap = { controlsVisible = !controlsVisible },
                            onDoubleTap = { offset ->
                                if (offset.x < size.width / 2) {
                                    playerController.seekRelative(-10000L)
                                } else {
                                    playerController.seekRelative(10000L)
                                }
                            }
                        )
                    }
                }
                .pointerInput(playerState.isControlsLocked) {
                    if (!playerState.isControlsLocked) {
                        detectDragGestures(
                            onDragStart = { },
                            onDragEnd = {
                                if (showSeekHud) {
                                    playerController.seekRelative(seekDeltaSeconds * 1000L)
                                }
                                showBrightnessHud = false
                                showVolumeHud = false
                                showSeekHud = false
                                seekDeltaSeconds = 0L
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val (dx, dy) = dragAmount
                                if (kotlin.math.abs(dx) > kotlin.math.abs(dy) * 1.5) {
                                    // Horizontal drag -> Seek
                                    showSeekHud = true
                                    seekDeltaSeconds += (dx / 10).toLong()
                                } else if (kotlin.math.abs(dy) > kotlin.math.abs(dx)) {
                                    // Vertical drag
                                    if (change.position.x < size.width / 2) {
                                        // Left side -> Brightness
                                        showBrightnessHud = true
                                        val delta = (-dy / 5).toInt()
                                        brightnessPercent = (brightnessPercent + delta).coerceIn(0, 100)
                                        (context as? Activity)?.window?.attributes = (context as? Activity)?.window?.attributes?.apply {
                                            screenBrightness = brightnessPercent / 100f
                                        }
                                    } else {
                                        // Right side -> Volume
                                        showVolumeHud = true
                                        val delta = (-dy / 5).toInt()
                                        volumePercent = (volumePercent + delta).coerceIn(0, 100)
                                        audioManager?.let { am ->
                                            val maxVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                                            val targetVol = (volumePercent * maxVol / 100).coerceIn(0, maxVol)
                                            am.setStreamVolume(AudioManager.STREAM_MUSIC, targetVol, 0)
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
        )

        // Gesture HUD Overlays (Brightness / Volume / Seek)
        if (showBrightnessHud) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                HudCard(
                    icon = Icons.Default.BrightnessHigh,
                    text = "Brightness $brightnessPercent%",
                    accentColor = LarkTealPrimary
                )
            }
        }

        if (showVolumeHud) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                HudCard(
                    icon = Icons.Default.VolumeUp,
                    text = "Volume $volumePercent%",
                    accentColor = LarkTealSecondary
                )
            }
        }

        if (showSeekHud) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val targetMs = (playerState.currentPositionMs + seekDeltaSeconds * 1000L).coerceIn(0L, playerState.durationMs)
                val sign = if (seekDeltaSeconds >= 0) "+" else ""
                HudCard(
                    icon = if (seekDeltaSeconds >= 0) Icons.Default.Forward10 else Icons.Default.Replay10,
                    text = "$sign${seekDeltaSeconds}s (${formatTime(targetMs)} / ${formatTime(playerState.durationMs)})",
                    accentColor = LarkPurpleAccent
                )
            }
        }

        // Locked Screen Indicator (floating unlock icon)
        if (playerState.isControlsLocked) {
            AnimatedVisibility(
                visible = controlsVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 24.dp)
            ) {
                IconButton(
                    onClick = { playerController.toggleLockControls() },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.7f))
                        .border(1.dp, LarkTealPrimary, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Unlock Controls",
                        tint = LarkTealPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }

        // Main Controls Overlay (Top bar & Bottom bar)
        AnimatedVisibility(
            visible = controlsVisible && !playerState.isControlsLocked,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // TOP BAR
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Black.copy(alpha = 0.85f), Color.Transparent)
                            )
                        )
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentMedia.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (!currentMedia.resolution.isNullOrBlank()) {
                            Text(
                                text = "${currentMedia.resolution} • ${currentMedia.folder}",
                                style = MaterialTheme.typography.labelSmall,
                                color = LarkTealPrimary
                            )
                        }
                    }

                    // Background Audio Playback ("Play as Audio" - Lark Signature Feature)
                    IconButton(
                        onClick = onPlayAsAudio,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(LarkPurpleAccent.copy(alpha = 0.3f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = "Play as Audio in Background",
                            tint = LarkPurpleAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Aspect Ratio Button
                    IconButton(
                        onClick = {
                            val nextRatio = when (playerState.videoAspectRatio) {
                                VideoAspectRatio.FIT -> VideoAspectRatio.FILL
                                VideoAspectRatio.FILL -> VideoAspectRatio.SIXTEEN_NINE
                                VideoAspectRatio.SIXTEEN_NINE -> VideoAspectRatio.FOUR_THREE
                                VideoAspectRatio.FOUR_THREE -> VideoAspectRatio.FIT
                            }
                            playerController.setVideoAspectRatio(nextRatio)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AspectRatio,
                            contentDescription = "Aspect Ratio: ${playerState.videoAspectRatio.label}",
                            tint = LarkTealPrimary
                        )
                    }

                    // Lock Screen Button
                    IconButton(
                        onClick = { playerController.toggleLockControls() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = "Lock Screen",
                            tint = Color.White
                        )
                    }
                }

                // BOTTOM BAR
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Time and Progress Bar
                    val progress = if (playerState.durationMs > 0) {
                        (playerState.currentPositionMs.toFloat() / playerState.durationMs.toFloat()).coerceIn(0f, 1f)
                    } else 0f

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatTime(playerState.currentPositionMs),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )

                        Slider(
                            value = progress,
                            onValueChange = { frac ->
                                playerController.seekTo((frac * playerState.durationMs).toLong())
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = LarkTealPrimary,
                                activeTrackColor = LarkTealPrimary,
                                inactiveTrackColor = LarkBorder
                            )
                        )

                        Text(
                            text = formatTime(playerState.durationMs),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }

                    // Control Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Speed dropdown button
                        Box {
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { showSpeedMenu = true },
                                color = LarkDarkCard,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, LarkBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Speed,
                                        contentDescription = null,
                                        tint = LarkTealPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${playerState.playbackSpeed}x",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = LarkTextPrimary
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = showSpeedMenu,
                                onDismissRequest = { showSpeedMenu = false },
                                modifier = Modifier.background(LarkDarkCard)
                            ) {
                                listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "${speed}x",
                                                color = if (playerState.playbackSpeed == speed) LarkTealPrimary else LarkTextPrimary
                                            )
                                        },
                                        onClick = {
                                            playerController.setPlaybackSpeed(speed)
                                            showSpeedMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        // Playback Center Controls
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { playerController.playPrevious() }) {
                                Icon(
                                    imageVector = Icons.Default.SkipPrevious,
                                    contentDescription = "Previous",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            IconButton(onClick = { playerController.seekRelative(-10000L) }) {
                                Icon(
                                    imageVector = Icons.Default.Replay10,
                                    contentDescription = "-10s",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(LarkTealPrimary)
                                    .clickable { playerController.togglePlayPause() }
                                    .testTag("video_play_pause"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            IconButton(onClick = { playerController.seekRelative(10000L) }) {
                                Icon(
                                    imageVector = Icons.Default.Forward10,
                                    contentDescription = "+10s",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            IconButton(onClick = { playerController.playNext() }) {
                                Icon(
                                    imageVector = Icons.Default.SkipNext,
                                    contentDescription = "Next",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        // Aspect ratio badge
                        Text(
                            text = playerState.videoAspectRatio.label,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = LarkTealPrimary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(LarkDarkCard)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HudCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    accentColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.75f)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, LarkBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }
    }
}

private fun formatTime(millis: Long): String {
    val totalSec = (millis / 1000).coerceAtLeast(0)
    val mins = totalSec / 60
    val secs = totalSec % 60
    val hours = mins / 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, mins % 60, secs)
    } else {
        String.format("%02d:%02d", mins, secs)
    }
}
