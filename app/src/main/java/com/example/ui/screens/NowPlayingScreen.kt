package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode as AnimRepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Videocam
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.MediaItem
import com.example.model.MediaType
import com.example.model.RepeatMode
import com.example.player.PlayerState
import com.example.ui.components.SongCoverArt
import com.example.ui.theme.LarkBorder
import com.example.ui.theme.LarkDarkBackground
import com.example.ui.theme.LarkDarkCard
import com.example.ui.theme.LarkPurpleAccent
import com.example.ui.theme.LarkRed
import com.example.ui.theme.LarkTealPrimary
import com.example.ui.theme.LarkTealSecondary
import com.example.ui.theme.LarkTextMuted
import com.example.ui.theme.LarkTextPrimary
import com.example.ui.theme.LarkTextSecondary
import kotlin.math.sin

@Composable
fun NowPlayingScreen(
    playerState: PlayerState,
    isFavorite: Boolean,
    onToggleFavorite: (MediaItem) -> Unit,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    onSeekRelative: (Long) -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onOpenEqualizer: () -> Unit,
    onOpenSleepTimer: () -> Unit,
    onRestoreVideo: () -> Unit,
    onCollapse: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentMedia = playerState.currentMedia ?: return
    var selectedTab by remember { mutableStateOf(0) } // 0: Disc & Wave, 1: Lyrics
    val lyrics = currentMedia.lyrics
    val lyricsListState = rememberLazyListState()

    // Find active lyric index
    val activeLyricIndex = remember(playerState.currentPositionMs, lyrics) {
        val idx = lyrics.indexOfLast { it.timestampMs <= playerState.currentPositionMs }
        if (idx >= 0) idx else 0
    }

    // Auto-scroll lyrics
    LaunchedEffect(activeLyricIndex, selectedTab) {
        if (selectedTab == 1 && lyrics.isNotEmpty() && activeLyricIndex >= 0) {
            lyricsListState.animateScrollToItem((activeLyricIndex - 2).coerceAtLeast(0))
        }
    }

    // Vinyl Disc Rotation Animation
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl_rotate")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = AnimRepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        LarkDarkBackground,
                        Color(0xFF070B12)
                    )
                )
            )
            .statusBarsPadding()
            .testTag("now_playing_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onCollapse) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Collapse",
                        tint = LarkTextPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Disc / Lyrics toggle pills
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(LarkDarkCard)
                        .border(1.dp, LarkBorder, RoundedCornerShape(20.dp))
                        .padding(3.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedTab = 0 },
                        color = if (selectedTab == 0) LarkTealPrimary else Color.Transparent
                    ) {
                        Text(
                            text = "Playing",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (selectedTab == 0) Color.White else LarkTextSecondary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedTab = 1 },
                        color = if (selectedTab == 1) LarkTealPrimary else Color.Transparent
                    ) {
                        Text(
                            text = "Lyrics",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (selectedTab == 1) Color.White else LarkTextSecondary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onOpenSleepTimer) {
                        Icon(
                            imageVector = Icons.Default.Bedtime,
                            contentDescription = "Sleep Timer",
                            tint = if (playerState.sleepTimerActive) LarkPurpleAccent else LarkTextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    IconButton(onClick = onOpenEqualizer) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Equalizer",
                            tint = LarkTealPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Center Content: Vinyl Disc & Wave or Synced Lyrics
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (selectedTab == 0) {
                    // DISC VIEW
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Vinyl Record
                        Box(
                            modifier = Modifier
                                .size(240.dp)
                                .rotate(if (playerState.isPlaying) rotationAngle else 0f)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(
                                            Color(0xFF2A3447),
                                            Color(0xFF141C2B),
                                            Color(0xFF080C14),
                                            Color(0xFF1F2937),
                                            Color(0xFF0D131F)
                                        )
                                    )
                                )
                                .border(
                                    BorderStroke(
                                        3.dp,
                                        Brush.sweepGradient(
                                            listOf(
                                                LarkTealPrimary,
                                                LarkPurpleAccent,
                                                LarkTealSecondary,
                                                LarkTealPrimary
                                            )
                                        )
                                    ),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            // Subtle vinyl grooves
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val radiusList = listOf(0.4f, 0.55f, 0.7f, 0.85f)
                                radiusList.forEach { r ->
                                    drawCircle(
                                        color = Color(0x33FFFFFF),
                                        radius = size.minDimension / 2 * r,
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f)
                                    )
                                }
                            }

                            // Center Album Art
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, LarkDarkBackground, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                SongCoverArt(
                                    mediaItem = currentMedia,
                                    modifier = Modifier.fillMaxSize(),
                                    shape = CircleShape,
                                    isVinylCenter = true
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Real-time Waveform Audio Visualizer
                        WaveformVisualizer(
                            isPlaying = playerState.isPlaying,
                            currentPos = playerState.currentPositionMs,
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(44.dp)
                        )
                    }
                } else {
                    // LYRICS VIEW
                    if (lyrics.isEmpty()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = LarkTextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No synced lyrics available",
                                style = MaterialTheme.typography.bodyMedium,
                                color = LarkTextMuted
                            )
                            Text(
                                text = "Enjoy the rhythm and high quality audio",
                                style = MaterialTheme.typography.bodySmall,
                                color = LarkTextMuted.copy(alpha = 0.7f)
                            )
                        }
                    } else {
                        LazyColumn(
                            state = lyricsListState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            itemsIndexed(lyrics) { index, line ->
                                val isActive = index == activeLyricIndex
                                Text(
                                    text = line.text,
                                    style = if (isActive) {
                                        MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                    } else {
                                        MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 15.sp
                                        )
                                    },
                                    color = if (isActive) LarkTealPrimary else LarkTextMuted,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onSeek(line.timestampMs) }
                                        .padding(vertical = 6.dp, horizontal = 12.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Track Information & Favorite Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentMedia.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = LarkTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "${currentMedia.artist} • ${currentMedia.album}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LarkTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // If video is playing in background audio mode, button to restore full video!
                if (playerState.isBackgroundAudioMode) {
                    IconButton(
                        onClick = onRestoreVideo,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(LarkPurpleAccent.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Restore Video Player",
                            tint = LarkPurpleAccent
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                IconButton(
                    onClick = { onToggleFavorite(currentMedia) }
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) LarkRed else LarkTextSecondary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Slider
            val progress = if (playerState.durationMs > 0) {
                (playerState.currentPositionMs.toFloat() / playerState.durationMs.toFloat()).coerceIn(0f, 1f)
            } else 0f

            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = progress,
                    onValueChange = { frac ->
                        onSeek((frac * playerState.durationMs).toLong())
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = LarkTealPrimary,
                        activeTrackColor = LarkTealPrimary,
                        inactiveTrackColor = LarkBorder
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(playerState.currentPositionMs),
                        style = MaterialTheme.typography.labelSmall,
                        color = LarkTextMuted
                    )
                    Text(
                        text = formatTime(playerState.durationMs),
                        style = MaterialTheme.typography.labelSmall,
                        color = LarkTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Playback Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle button
                IconButton(onClick = onToggleShuffle) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (playerState.isShuffled) LarkTealPrimary else LarkTextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Previous button
                IconButton(
                    onClick = onPrevious,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = LarkTextPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // -10s Jump
                IconButton(
                    onClick = { onSeekRelative(-10000L) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay10,
                        contentDescription = "Back 10s",
                        tint = LarkTextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Large Central Glowing Play/Pause
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(LarkTealPrimary, LarkTealSecondary)
                            )
                        )
                        .clickable { onTogglePlay() }
                        .testTag("now_playing_play_pause"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }

                // Next button
                IconButton(
                    onClick = onNext,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = LarkTextPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Repeat Mode Button
                IconButton(onClick = onToggleRepeat) {
                    Icon(
                        imageVector = when (playerState.repeatMode) {
                            RepeatMode.ONE -> Icons.Default.RepeatOne
                            RepeatMode.ALL -> Icons.Default.Repeat
                            RepeatMode.OFF -> Icons.Default.Repeat
                        },
                        contentDescription = "Repeat",
                        tint = if (playerState.repeatMode != RepeatMode.OFF) LarkTealPrimary else LarkTextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun WaveformVisualizer(
    isPlaying: Boolean,
    currentPos: Long,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave_anim")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = AnimRepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(modifier = modifier) {
        val barCount = 36
        val barSpacing = size.width / barCount
        val barWidth = barSpacing * 0.55f

        for (i in 0 until barCount) {
            val normalizedIdx = i.toFloat() / barCount
            val dynamicHeight = if (isPlaying) {
                val wave1 = sin(normalizedIdx * 8f + phase)
                val wave2 = sin(normalizedIdx * 4f - phase * 1.5f)
                val combined = (wave1 + wave2 + 2f) / 4f
                (size.height * 0.15f) + (size.height * 0.85f * combined)
            } else {
                size.height * 0.15f
            }

            val x = i * barSpacing + (barSpacing - barWidth) / 2
            val y = (size.height - dynamicHeight) / 2

            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(LarkTealPrimary, LarkPurpleAccent)
                ),
                topLeft = Offset(x, y),
                size = Size(barWidth, dynamicHeight),
                cornerRadius = CornerRadius(4f, 4f)
            )
        }
    }
}

private fun formatTime(millis: Long): String {
    val totalSec = (millis / 1000).coerceAtLeast(0)
    val mins = totalSec / 60
    val secs = totalSec % 60
    return String.format("%02d:%02d", mins, secs)
}
