package com.example.model

enum class MediaType {
    AUDIO,
    VIDEO
}

enum class RepeatMode {
    OFF,
    ALL,
    ONE
}

enum class VideoAspectRatio(val label: String) {
    FIT("Fit"),
    FILL("Fill"),
    SIXTEEN_NINE("16:9"),
    FOUR_THREE("4:3")
}

data class LyricLine(
    val timestampMs: Long,
    val text: String
)

data class MediaItem(
    val id: String,
    val title: String,
    val artist: String = "Unknown Artist",
    val album: String = "Unknown Album",
    val durationMs: Long = 0L,
    val mediaUri: String,
    val type: MediaType = MediaType.AUDIO,
    val thumbnailUri: String? = null,
    val sizeBytes: Long = 0L,
    val resolution: String? = null,
    val folder: String = "Internal",
    val isFavorite: Boolean = false,
    val lyrics: List<LyricLine> = emptyList()
) {
    val formattedDuration: String
        get() {
            val totalSeconds = (durationMs / 1000).coerceAtLeast(0)
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            val hours = minutes / 60
            return if (hours > 0) {
                String.format("%d:%02d:%02d", hours, minutes % 60, seconds)
            } else {
                String.format("%02d:%02d", minutes, seconds)
            }
        }

    val formattedSize: String
        get() {
            if (sizeBytes <= 0) return ""
            val mb = sizeBytes / (1024.0 * 1024.0)
            return if (mb >= 1.0) {
                String.format("%.1f MB", mb)
            } else {
                val kb = sizeBytes / 1024
                "$kb KB"
            }
        }
}

data class Playlist(
    val id: String,
    val name: String,
    val songIds: List<String> = emptyList(),
    val coverEmoji: String = "🎵"
)

data class EqualizerBand(
    val bandIndex: Int,
    val frequencyHz: Int,
    val gainDb: Int // e.g. -10 to +10 dB
)

data class EqualizerPreset(
    val name: String,
    val gainsDb: List<Int>
)
