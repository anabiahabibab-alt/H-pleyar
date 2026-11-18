package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Nightlife
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.model.MediaItem
import com.example.ui.theme.HPlayerDarkBackground
import com.example.ui.theme.HPlayerRedPrimary
import kotlin.math.abs

/**
 * Universal Cover Art Component for H Player.
 * Loads device/network album art if available, or renders an offline,
 * stylized music cover inspired by dynamic stage, bokeh, synth, wave,
 * and concert aesthetics so that all songs on the phone have vivid,
 * colorful picture thumbnails.
 */
@Composable
fun SongCoverArt(
    mediaItem: MediaItem,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(10.dp),
    isVinylCenter: Boolean = false
) {
    val styleIndex = remember(mediaItem.id, mediaItem.title) {
        val seed = (mediaItem.id + mediaItem.title).ifEmpty { "hplayer" }
        abs(seed.hashCode()) % 5
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(HPlayerDarkBackground),
        contentAlignment = Alignment.Center
    ) {
        if (!mediaItem.thumbnailUri.isNullOrBlank()) {
            SubcomposeAsyncImage(
                model = mediaItem.thumbnailUri,
                contentDescription = mediaItem.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    CuratedMusicArtwork(styleIndex = styleIndex, modifier = Modifier.fillMaxSize())
                },
                error = {
                    CuratedMusicArtwork(styleIndex = styleIndex, modifier = Modifier.fillMaxSize())
                },
                success = {
                    SubcomposeAsyncImageContent(
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            )
        } else {
            CuratedMusicArtwork(styleIndex = styleIndex, modifier = Modifier.fillMaxSize())
        }

        if (isVinylCenter) {
            // Center spindle hole for vinyl disc mode
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(HPlayerDarkBackground)
                    .border(1.5.dp, Color(0x66FFFFFF), CircleShape)
            )
        }
    }
}

@Composable
fun CuratedMusicArtwork(
    styleIndex: Int,
    modifier: Modifier = Modifier
) {
    when (styleIndex) {
        0 -> FluidWavesCover(modifier)
        1 -> VintageMicBokehCover(modifier)
        2 -> NeonDjSynthCover(modifier)
        3 -> ConcertStageCover(modifier)
        else -> RaveLaserCrowdCover(modifier)
    }
}

/** Style 0: Fluid Cosmic Waves (Magenta, Cyan & Deep Violet) */
@Composable
fun FluidWavesCover(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(
            Brush.linearGradient(
                listOf(Color(0xFF3B0764), Color(0xFF1E1B4B), Color(0xFF0F172A))
            )
        ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Layered waves
            val path1 = Path().apply {
                moveTo(0f, h * 0.35f)
                cubicTo(w * 0.3f, h * 0.15f, w * 0.7f, h * 0.6f, w, h * 0.3f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = path1,
                brush = Brush.linearGradient(
                    listOf(Color(0xBBEC4899), Color(0x888B5CF6)),
                    start = Offset(0f, h * 0.2f),
                    end = Offset(w, h)
                )
            )

            val path2 = Path().apply {
                moveTo(0f, h * 0.55f)
                cubicTo(w * 0.25f, h * 0.75f, w * 0.65f, h * 0.35f, w, h * 0.6f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = path2,
                brush = Brush.linearGradient(
                    listOf(Color(0xCC06B6D4), Color(0x993B82F6)),
                    start = Offset(0f, h * 0.4f),
                    end = Offset(w, h)
                )
            )

            val path3 = Path().apply {
                moveTo(0f, h * 0.7f)
                cubicTo(w * 0.4f, h * 0.55f, w * 0.7f, h * 0.85f, w, h * 0.75f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = path3,
                brush = Brush.linearGradient(
                    listOf(Color(0xDDF43F5E), Color(0x88EC4899))
                )
            )

            // Cosmic light orbs
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0x88FFFFFF), Color.Transparent),
                    center = Offset(w * 0.75f, h * 0.25f),
                    radius = w * 0.3f
                ),
                center = Offset(w * 0.75f, h * 0.25f),
                radius = w * 0.3f
            )
        }

        Icon(
            imageVector = Icons.Default.SurroundSound,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.85f),
            modifier = Modifier.size(24.dp)
        )
    }
}

/** Style 1: Vintage Stage Microphone with Warm Golden/Amber Bokeh */
@Composable
fun VintageMicBokehCover(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(
            Brush.linearGradient(
                listOf(Color(0xFF1E1005), Color(0xFF120803), Color(0xFF0A0402))
            )
        ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Amber bokeh orbs
            val bokeh = listOf(
                Triple(Offset(w * 0.2f, h * 0.3f), w * 0.22f, Color(0x66F59E0B)),
                Triple(Offset(w * 0.8f, h * 0.25f), w * 0.28f, Color(0x55EF4444)),
                Triple(Offset(w * 0.3f, h * 0.75f), w * 0.25f, Color(0x66FBBF24)),
                Triple(Offset(w * 0.7f, h * 0.7f), w * 0.3f, Color(0x55D97706)),
                Triple(Offset(w * 0.5f, h * 0.5f), w * 0.4f, Color(0x44FF2A43))
            )

            bokeh.forEach { (pos, radius, color) ->
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(color, Color.Transparent),
                        center = pos,
                        radius = radius
                    ),
                    center = pos,
                    radius = radius
                )
            }

            // Warm golden halo in center
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0x88FFE082), Color.Transparent),
                    center = Offset(w * 0.5f, h * 0.45f),
                    radius = w * 0.35f
                ),
                center = Offset(w * 0.5f, h * 0.45f),
                radius = w * 0.35f
            )
        }

        // Vintage Mic Symbol
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = null,
            tint = Color(0xFFFFE082),
            modifier = Modifier.size(24.dp)
        )
    }
}

/** Style 2: Neon DJ Synth / Synthesizer & Turntable */
@Composable
fun NeonDjSynthCover(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(
            Brush.linearGradient(
                listOf(Color(0xFF1E0738), Color(0xFF0F041D), Color(0xFF090212))
            )
        ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Neon radial glow
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0x66A855F7), Color(0x333B82F6), Color.Transparent),
                    center = Offset(w * 0.5f, h * 0.4f),
                    radius = w * 0.55f
                ),
                center = Offset(w * 0.5f, h * 0.4f),
                radius = w * 0.55f
            )

            // Neon turntable rings
            drawCircle(
                color = Color(0x5506B6D4),
                center = Offset(w * 0.5f, h * 0.5f),
                radius = w * 0.38f,
                style = Stroke(width = 2f)
            )
            drawCircle(
                color = Color(0x44EC4899),
                center = Offset(w * 0.5f, h * 0.5f),
                radius = w * 0.28f,
                style = Stroke(width = 1.5f)
            )

            // Equalizer neon bar lines at bottom
            val barCount = 7
            val barWidth = w * 0.08f
            val spacing = w * 0.035f
            val startX = (w - (barCount * barWidth + (barCount - 1) * spacing)) / 2f
            val heights = listOf(0.25f, 0.45f, 0.7f, 0.9f, 0.6f, 0.4f, 0.2f)

            heights.forEachIndexed { i, factor ->
                val x = startX + i * (barWidth + spacing)
                val barH = h * 0.22f * factor
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFF00F2FE), Color(0xFF4FACFE)),
                        startY = h * 0.88f - barH,
                        endY = h * 0.88f
                    ),
                    topLeft = Offset(x, h * 0.88f - barH),
                    size = Size(barWidth, barH)
                )
            }
        }

        Icon(
            imageVector = Icons.Default.GraphicEq,
            contentDescription = null,
            tint = Color(0xFF38BDF8),
            modifier = Modifier.size(24.dp)
        )
    }
}

/** Style 3: Concert Stage & Spotlight with Performer */
@Composable
fun ConcertStageCover(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(
            Brush.linearGradient(
                listOf(Color(0xFF26050C), Color(0xFF140206), Color(0xFF0A0103))
            )
        ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Left spotlight cone
            val leftCone = Path().apply {
                moveTo(w * 0.1f, 0f)
                lineTo(w * 0.2f, 0f)
                lineTo(w * 0.8f, h)
                lineTo(w * 0.3f, h)
                close()
            }
            drawPath(
                path = leftCone,
                brush = Brush.linearGradient(
                    listOf(Color(0x44FFFFFF), Color(0x22FF2A43), Color.Transparent),
                    start = Offset(w * 0.15f, 0f),
                    end = Offset(w * 0.55f, h)
                )
            )

            // Right spotlight cone
            val rightCone = Path().apply {
                moveTo(w * 0.9f, 0f)
                lineTo(w * 0.8f, 0f)
                lineTo(w * 0.2f, h)
                lineTo(w * 0.7f, h)
                close()
            }
            drawPath(
                path = rightCone,
                brush = Brush.linearGradient(
                    listOf(Color(0x44FFFFFF), Color(0x22FF2A43), Color.Transparent),
                    start = Offset(w * 0.85f, 0f),
                    end = Offset(w * 0.45f, h)
                )
            )

            // Atmospheric smoke / stage fog glow
            drawOval(
                brush = Brush.radialGradient(
                    listOf(Color(0x66FF2A43), Color(0x33E50914), Color.Transparent),
                    center = Offset(w * 0.5f, h * 0.75f),
                    radius = w * 0.5f
                ),
                topLeft = Offset(0f, h * 0.5f),
                size = Size(w, h * 0.5f)
            )
        }

        Icon(
            imageVector = Icons.Default.Nightlife,
            contentDescription = null,
            tint = Color(0xFFFFD9DD),
            modifier = Modifier.size(24.dp)
        )
    }
}

/** Style 4: Festival Rave & Cosmic Lasers */
@Composable
fun RaveLaserCrowdCover(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(
            Brush.linearGradient(
                listOf(Color(0xFF042F2E), Color(0xFF031D1C), Color(0xFF020C12))
            )
        ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Cosmic Aurora banner
            drawOval(
                brush = Brush.radialGradient(
                    listOf(Color(0x8810B981), Color(0x5506B6D4), Color.Transparent),
                    center = Offset(w * 0.5f, h * 0.35f),
                    radius = w * 0.5f
                ),
                topLeft = Offset(-w * 0.1f, 0f),
                size = Size(w * 1.2f, h * 0.6f)
            )

            // Radiating laser beam rays
            val laserSource = Offset(w * 0.5f, h * 0.15f)
            val angles = listOf(
                Offset(0f, h * 0.65f),
                Offset(w * 0.2f, h * 0.85f),
                Offset(w * 0.5f, h * 0.95f),
                Offset(w * 0.8f, h * 0.85f),
                Offset(w, h * 0.65f)
            )
            val colors = listOf(
                Color(0xAA10B981),
                Color(0xAA06B6D4),
                Color(0xAA38BDF8),
                Color(0xAAA855F7),
                Color(0xAA10B981)
            )

            angles.forEachIndexed { i, target ->
                drawLine(
                    color = colors[i % colors.size],
                    start = laserSource,
                    end = target,
                    strokeWidth = 2f
                )
            }

            // Crowd silhouette along bottom edge
            val crowdPath = Path().apply {
                moveTo(0f, h)
                lineTo(0f, h * 0.82f)
                var cx = 0f
                val step = w / 10f
                while (cx <= w) {
                    val bumpH = if ((cx / step).toInt() % 2 == 0) h * 0.77f else h * 0.81f
                    quadraticBezierTo(cx + step * 0.5f, bumpH, cx + step, h * 0.82f)
                    cx += step
                }
                lineTo(w, h)
                close()
            }
            drawPath(crowdPath, color = Color(0xEE020C12), style = Fill)
        }

        Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = null,
            tint = Color(0xFF6EE7B7),
            modifier = Modifier.size(24.dp)
        )
    }
}
