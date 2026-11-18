package com.example.data

import com.example.model.EqualizerPreset
import com.example.model.LyricLine
import com.example.model.MediaItem
import com.example.model.MediaType

object SampleMediaData {

    val sampleLyricsSunset = listOf(
        LyricLine(0L, "♪ Instrumental Intro ♪"),
        LyricLine(4000L, "Golden hour fades into the twilight sky"),
        LyricLine(8500L, "City lights are shining as the moments pass us by"),
        LyricLine(13000L, "Feel the rhythm pulsing through the evening air"),
        LyricLine(18000L, "Every heartbeat echoes like there's magic everywhere"),
        LyricLine(23000L, "♪ Synthwave drop ♪"),
        LyricLine(28000L, "Hold on to the feeling, don't let it drift away"),
        LyricLine(33500L, "We'll dance until tomorrow turns into yesterday"),
        LyricLine(39000L, "Neon shadows dancing on the boulevard"),
        LyricLine(45000L, "Lost inside the melody, reaching for the stars"),
        LyricLine(52000L, "♪ Guitar solo & outro ♪"),
        LyricLine(60000L, "Lark Player vibes forever in your heart...")
    )

    val sampleLyricsCyberpunk = listOf(
        LyricLine(0L, "♪ Digital Bassline Rising ♪"),
        LyricLine(3500L, "Midnight cruisers on the cyber highway"),
        LyricLine(7800L, "Glowing neon reflections on the rain-slick streets"),
        LyricLine(12500L, "Turn the frequency high, turn the bass down low"),
        LyricLine(17000L, "Feel the subwoofer vibration let it overflow"),
        LyricLine(22000L, "♪ Heavy Bass Drop ♪"),
        LyricLine(27500L, "Electronic dreams in a virtual night"),
        LyricLine(33000L, "Lark equalizer keeping every beat bright"),
        LyricLine(40000L, "Pure soundwaves flowing without a pause"),
        LyricLine(48000L, "Living for the music, living for the cause")
    )

    val sampleLyricsAcoustic = listOf(
        LyricLine(0L, "♪ Soft Acoustic Arpeggio ♪"),
        LyricLine(3000L, "Morning dew upon the meadow green"),
        LyricLine(7000L, "The sweetest melody you've ever seen"),
        LyricLine(11500L, "Strumming gentle chords beneath the pine"),
        LyricLine(16000L, "Lost in quiet thoughts where peace is mine"),
        LyricLine(21000L, "Take a breath and let the worries cease"),
        LyricLine(26000L, "In the harmony of sweet release")
    )

    val sampleAudioTracks = listOf(
        MediaItem(
            id = "sample_audio_1",
            title = "Midnight Horizon (Synthwave)",
            artist = "Aura Wave",
            album = "Neon Dreamscape",
            durationMs = 68000L,
            mediaUri = "https://commondatastorage.googleapis.com/codeskulptor-demos/riceracer_assets/music/race.ogg",
            type = MediaType.AUDIO,
            thumbnailUri = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=400",
            sizeBytes = 4250000L,
            folder = "Music/Synthwave",
            isFavorite = true,
            lyrics = sampleLyricsSunset
        ),
        MediaItem(
            id = "sample_audio_2",
            title = "Cyber Beat Pulse",
            artist = "Neon Matrix",
            album = "Future City 2077",
            durationMs = 54000L,
            mediaUri = "https://commondatastorage.googleapis.com/codeskulptor-demos/DDR_assets/Sevish_-__nbsp_.mp3",
            type = MediaType.AUDIO,
            thumbnailUri = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400",
            sizeBytes = 3890000L,
            folder = "Music/Electronic",
            isFavorite = true,
            lyrics = sampleLyricsCyberpunk
        ),
        MediaItem(
            id = "sample_audio_3",
            title = "Golden Sunset Serenade",
            artist = "Oliver Reed",
            album = "Acoustic Tales",
            durationMs = 45000L,
            mediaUri = "https://commondatastorage.googleapis.com/codeskulptor-demos/DDR_assets/Kangaroo_MusiQue_-_The_Neverending_Story.mp3",
            type = MediaType.AUDIO,
            thumbnailUri = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400",
            sizeBytes = 3120000L,
            folder = "Music/Acoustic",
            isFavorite = false,
            lyrics = sampleLyricsAcoustic
        ),
        MediaItem(
            id = "sample_audio_4",
            title = "Tropical Chill Beats",
            artist = "Sunset Boulevard",
            album = "Island Breeze",
            durationMs = 72000L,
            mediaUri = "https://commondatastorage.googleapis.com/codeskulptor-assets/Epoq-Lepidoptera.ogg",
            type = MediaType.AUDIO,
            thumbnailUri = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400",
            sizeBytes = 5120000L,
            folder = "Music/Chillout",
            isFavorite = true,
            lyrics = sampleLyricsSunset
        ),
        MediaItem(
            id = "sample_audio_5",
            title = "Urban Trap Symphony",
            artist = "Metro Flow",
            album = "High Octane",
            durationMs = 60000L,
            mediaUri = "https://commondatastorage.googleapis.com/codeskulptor-demos/riceracer_assets/music/race.ogg",
            type = MediaType.AUDIO,
            thumbnailUri = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400",
            sizeBytes = 4100000L,
            folder = "Music/Hip-Hop",
            isFavorite = false,
            lyrics = sampleLyricsCyberpunk
        )
    )

    val sampleVideoTracks = listOf(
        MediaItem(
            id = "sample_video_1",
            title = "Big Buck Bunny (Animation Movie)",
            artist = "Blender Foundation",
            album = "Open Cinema Project",
            durationMs = 596000L,
            mediaUri = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            type = MediaType.VIDEO,
            thumbnailUri = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=400",
            sizeBytes = 45000000L,
            resolution = "1080p Full HD",
            folder = "Movies/Animations",
            isFavorite = true
        ),
        MediaItem(
            id = "sample_video_2",
            title = "Nature & Forest Drone 4K",
            artist = "Earth Explorer",
            album = "Wild Planet",
            durationMs = 120000L,
            mediaUri = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            type = MediaType.VIDEO,
            thumbnailUri = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400",
            sizeBytes = 28000000L,
            resolution = "4K UHD",
            folder = "Downloads/Nature",
            isFavorite = true
        ),
        MediaItem(
            id = "sample_video_3",
            title = "Tears of Steel (Sci-Fi Short)",
            artist = "Mango Open Movie",
            album = "Sci-Fi Collection",
            durationMs = 734000L,
            mediaUri = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
            type = MediaType.VIDEO,
            thumbnailUri = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400",
            sizeBytes = 62000000L,
            resolution = "1080p FHD",
            folder = "Movies/Sci-Fi",
            isFavorite = false
        ),
        MediaItem(
            id = "sample_video_4",
            title = "Sintel Fantasy Quest (Clip)",
            artist = "Durian Animation",
            album = "Legends of Sintel",
            durationMs = 52000L,
            mediaUri = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
            type = MediaType.VIDEO,
            thumbnailUri = "https://images.unsplash.com/photo-1579783902614-a3fb3927b675?w=400",
            sizeBytes = 18500000L,
            resolution = "720p HD",
            folder = "Camera/Videos",
            isFavorite = false
        ),
        MediaItem(
            id = "sample_video_5",
            title = "Subaru Motorsport Racing",
            artist = "Speed Champions",
            album = "Rally World",
            durationMs = 85000L,
            mediaUri = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4",
            type = MediaType.VIDEO,
            thumbnailUri = "https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?w=400",
            sizeBytes = 22000000L,
            resolution = "1080p",
            folder = "Downloads/Racing",
            isFavorite = false
        )
    )

    val equalizerPresets = listOf(
        EqualizerPreset("Normal", listOf(0, 0, 0, 0, 0)),
        EqualizerPreset("Bass Boost", listOf(8, 6, 2, 0, 1)),
        EqualizerPreset("Pop", listOf(-1, 2, 5, 2, -2)),
        EqualizerPreset("Rock", listOf(5, 3, -1, 3, 6)),
        EqualizerPreset("Electronic", listOf(6, 4, 0, 3, 5)),
        EqualizerPreset("Hip Hop", listOf(7, 4, 1, 3, 4)),
        EqualizerPreset("Jazz", listOf(3, 2, -2, 2, 4)),
        EqualizerPreset("Classical", listOf(4, 3, 0, 3, 5)),
        EqualizerPreset("Vocal Boost", listOf(-2, 1, 6, 4, -1))
    )
}
