package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.MediaType
import com.example.ui.LarkUiState
import com.example.ui.LarkViewModel
import com.example.ui.MainTab
import com.example.ui.components.DeleteConfirmDialog
import com.example.ui.components.HiddenVaultDialog
import com.example.ui.components.MiniPlayer
import com.example.ui.screens.EqualizerDialog
import com.example.ui.screens.MusicListScreen
import com.example.ui.screens.NowPlayingScreen
import com.example.ui.screens.PlaylistScreen
import com.example.ui.screens.SleepTimerDialog
import com.example.ui.screens.ToolsScreen
import com.example.ui.screens.VideoListScreen
import com.example.ui.screens.VideoPlayerScreen
import com.example.ui.theme.LarkBorder
import com.example.ui.theme.LarkDarkBackground
import com.example.ui.theme.LarkDarkCard
import com.example.ui.theme.LarkDarkSurface
import com.example.ui.theme.LarkPurpleAccent
import com.example.ui.theme.LarkTealPrimary
import com.example.ui.theme.LarkTealSecondary
import com.example.ui.theme.LarkTextMuted
import com.example.ui.theme.LarkTextPrimary
import com.example.ui.theme.LarkTextSecondary
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: LarkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                LarkPlayerApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun LarkPlayerApp(viewModel: LarkViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val audioList by viewModel.audioList.collectAsStateWithLifecycle()
    val videoList by viewModel.videoList.collectAsStateWithLifecycle()

    // Permission launcher for media storage
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.any { it }
        if (granted) {
            viewModel.scanDeviceStorage()
        }
    }

    LaunchedEffect(Unit) {
        val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        val hasAnyPermission = permissionsToRequest.any {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        if (hasAnyPermission) {
            viewModel.scanDeviceStorage()
        } else {
            permissionLauncher.launch(permissionsToRequest)
        }
    }

    // Back button behavior
    BackHandler(enabled = playerState.isVideoPlaying || uiState.showNowPlaying) {
        if (playerState.isVideoPlaying) {
            viewModel.playerController.switchToBackgroundAudio()
        } else if (uiState.showNowPlaying) {
            viewModel.setShowNowPlaying(false)
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbarMessage()
        }
    }

    val visibleAudioList = remember(audioList, uiState.hiddenMediaIds) {
        audioList.filter { !uiState.hiddenMediaIds.contains(it.id) }
    }
    val visibleVideoList = remember(videoList, uiState.hiddenMediaIds) {
        videoList.filter { !uiState.hiddenMediaIds.contains(it.id) }
    }
    val combinedMedia = remember(audioList, videoList, uiState.hiddenMediaIds) {
        (audioList + videoList).filter { !uiState.hiddenMediaIds.contains(it.id) }
    }
    val allMediaForVault = remember(audioList, videoList) {
        audioList + videoList
    }

    Box(modifier = Modifier.fillMaxSize().background(LarkDarkBackground)) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = LarkDarkBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                LarkTopAppBar(
                    uiState = uiState,
                    playerActive = playerState.currentMedia != null,
                    sleepTimerActive = playerState.sleepTimerActive,
                    onSearchChange = { viewModel.setSearchQuery(it) },
                    onToggleSearch = { viewModel.toggleSearchActive(it) },
                    onOpenEqualizer = { viewModel.setShowEqualizer(true) },
                    onOpenSleepTimer = { viewModel.setShowSleepTimer(true) }
                )
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    // Persistent Mini Player above bottom tabs
                    MiniPlayer(
                        playerState = playerState,
                        onExpandClick = {
                            if (playerState.isBackgroundAudioMode || playerState.currentMedia?.type == MediaType.AUDIO) {
                                viewModel.setShowNowPlaying(true)
                            } else {
                                viewModel.playerController.restoreVideoView()
                            }
                        },
                        onTogglePlay = { viewModel.playerController.togglePlayPause() },
                        onNextClick = { viewModel.playerController.playNext() },
                        onCloseClick = { viewModel.playerController.closePlayer() }
                    )

                    // Material 3 Navigation Bar
                    NavigationBar(
                        containerColor = LarkDarkSurface,
                        contentColor = LarkTealPrimary,
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            selected = uiState.selectedTab == MainTab.VIDEO,
                            onClick = { viewModel.selectTab(MainTab.VIDEO) },
                            icon = {
                                Icon(Icons.Default.Videocam, contentDescription = "Video")
                            },
                            label = { Text("Video", fontSize = 12.sp) },
                            colors = navigationItemColors()
                        )

                        NavigationBarItem(
                            selected = uiState.selectedTab == MainTab.MUSIC,
                            onClick = { viewModel.selectTab(MainTab.MUSIC) },
                            icon = {
                                Icon(Icons.Default.MusicNote, contentDescription = "Music")
                            },
                            label = { Text("Music", fontSize = 12.sp) },
                            colors = navigationItemColors()
                        )

                        NavigationBarItem(
                            selected = uiState.selectedTab == MainTab.PLAYLISTS,
                            onClick = { viewModel.selectTab(MainTab.PLAYLISTS) },
                            icon = {
                                Icon(Icons.Default.QueueMusic, contentDescription = "Playlists")
                            },
                            label = { Text("Playlists", fontSize = 12.sp) },
                            colors = navigationItemColors()
                        )

                        NavigationBarItem(
                            selected = uiState.selectedTab == MainTab.TOOLS,
                            onClick = { viewModel.selectTab(MainTab.TOOLS) },
                            icon = {
                                Icon(Icons.Default.Tune, contentDescription = "Tools")
                            },
                            label = { Text("Tools", fontSize = 12.sp) },
                            colors = navigationItemColors()
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (uiState.selectedTab) {
                    MainTab.VIDEO -> {
                        VideoListScreen(
                            videos = visibleVideoList,
                            subTabIndex = uiState.videoSubTab,
                            searchQuery = uiState.searchQuery,
                            favoriteIds = uiState.favoriteIds,
                            onSelectSubTab = { viewModel.setVideoSubTab(it) },
                            onVideoClick = { video -> viewModel.playMediaItem(video) },
                            onPlayAsAudio = { video -> viewModel.playMediaItem(video, asBackgroundAudio = true) },
                            onToggleFavorite = { video -> viewModel.toggleFavorite(video) },
                            onScanStorage = { viewModel.scanDeviceStorage() },
                            onHideVideo = { video -> viewModel.hideMedia(video) },
                            onDeleteVideo = { video -> viewModel.setItemToDelete(video) }
                        )
                    }
                    MainTab.MUSIC -> {
                        MusicListScreen(
                            songs = visibleAudioList,
                            subTabIndex = uiState.musicSubTab,
                            searchQuery = uiState.searchQuery,
                            favoriteIds = uiState.favoriteIds,
                            currentPlayingId = playerState.currentMedia?.id,
                            onSelectSubTab = { viewModel.setMusicSubTab(it) },
                            onSongClick = { song -> viewModel.playMediaItem(song) },
                            onShuffleAll = {
                                viewModel.playerController.setPlaylist(visibleAudioList.shuffled())
                                visibleAudioList.shuffled().firstOrNull()?.let { viewModel.playMediaItem(it) }
                            },
                            onToggleFavorite = { song -> viewModel.toggleFavorite(song) },
                            onScanStorage = { viewModel.scanDeviceStorage() },
                            onAddToPlaylistClick = { song ->
                                uiState.playlists.firstOrNull()?.let { pl ->
                                    viewModel.addMediaToPlaylist(pl.id, song.id)
                                }
                            },
                            onHideSong = { song -> viewModel.hideMedia(song) },
                            onDeleteSong = { song -> viewModel.setItemToDelete(song) }
                        )
                    }
                    MainTab.PLAYLISTS -> {
                        PlaylistScreen(
                            playlists = uiState.playlists,
                            allMedia = combinedMedia,
                            favoriteIds = uiState.favoriteIds,
                            recentIds = uiState.recentMediaIds,
                            hiddenCount = uiState.hiddenMediaIds.size,
                            onPlayMedia = { item -> viewModel.playMediaItem(item) },
                            onCreatePlaylist = { name -> viewModel.createPlaylist(name) },
                            onOpenHiddenVault = { viewModel.setShowHiddenVault(true) }
                        )
                    }
                    MainTab.TOOLS -> {
                        ToolsScreen(
                            playerState = playerState,
                            onOpenEqualizer = { viewModel.setShowEqualizer(true) },
                            onOpenSleepTimer = { viewModel.setShowSleepTimer(true) },
                            onScanStorage = { viewModel.scanDeviceStorage() },
                            onAddCustomStream = { title, url, type ->
                                viewModel.addCustomMedia(title, url, type)
                            }
                        )
                    }
                }
            }
        }

        // Fullscreen Video Player Overlay
        AnimatedVisibility(
            visible = playerState.isVideoPlaying,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            VideoPlayerScreen(
                playerState = playerState,
                playerController = viewModel.playerController,
                onBack = {
                    viewModel.playerController.switchToBackgroundAudio()
                },
                onPlayAsAudio = {
                    viewModel.playerController.switchToBackgroundAudio()
                    viewModel.setShowNowPlaying(true)
                }
            )
        }

        // Fullscreen Now Playing Music Sheet
        AnimatedVisibility(
            visible = uiState.showNowPlaying && playerState.currentMedia != null && !playerState.isVideoPlaying,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            NowPlayingScreen(
                playerState = playerState,
                isFavorite = viewModel.isFavorite(playerState.currentMedia?.id ?: ""),
                onToggleFavorite = { item -> viewModel.toggleFavorite(item) },
                onTogglePlay = { viewModel.playerController.togglePlayPause() },
                onNext = { viewModel.playerController.playNext() },
                onPrevious = { viewModel.playerController.playPrevious() },
                onSeek = { pos -> viewModel.playerController.seekTo(pos) },
                onSeekRelative = { delta -> viewModel.playerController.seekRelative(delta) },
                onToggleShuffle = { viewModel.playerController.toggleShuffle() },
                onToggleRepeat = { viewModel.playerController.toggleRepeat() },
                onOpenEqualizer = { viewModel.setShowEqualizer(true) },
                onOpenSleepTimer = { viewModel.setShowSleepTimer(true) },
                onRestoreVideo = { viewModel.playerController.restoreVideoView() },
                onCollapse = { viewModel.setShowNowPlaying(false) }
            )
        }

        // Equalizer Dialog
        if (uiState.showEqualizer) {
            EqualizerDialog(
                playerState = playerState,
                onBandChange = { index, gain -> viewModel.playerController.setBandGain(index, gain) },
                onBassBoostChange = { percent -> viewModel.playerController.setBassBoost(percent) },
                onVirtualizerChange = { percent -> viewModel.playerController.setVirtualizer(percent) },
                onPresetSelect = { preset -> viewModel.playerController.applyPreset(preset) },
                onDismiss = { viewModel.setShowEqualizer(false) }
            )
        }

        // Sleep Timer Dialog
        if (uiState.showSleepTimer) {
            SleepTimerDialog(
                playerState = playerState,
                onSetTimer = { mins -> viewModel.playerController.startSleepTimer(mins) },
                onCancelTimer = { viewModel.playerController.cancelSleepTimer() },
                onDismiss = { viewModel.setShowSleepTimer(false) }
            )
        }

        // Delete Confirmation Dialog
        uiState.itemToDelete?.let { item ->
            DeleteConfirmDialog(
                item = item,
                onConfirm = { viewModel.deleteMedia(item) },
                onDismiss = { viewModel.setItemToDelete(null) }
            )
        }

        // Hidden Media Vault Dialog
        if (uiState.showHiddenVault) {
            val hiddenItems = allMediaForVault.filter { uiState.hiddenMediaIds.contains(it.id) }
            HiddenVaultDialog(
                hiddenItems = hiddenItems,
                onPlay = { item ->
                    viewModel.setShowHiddenVault(false)
                    viewModel.playMediaItem(item)
                },
                onUnhide = { item -> viewModel.unhideMedia(item) },
                onDelete = { item -> viewModel.setItemToDelete(item) },
                onDismiss = { viewModel.setShowHiddenVault(false) }
            )
        }
    }
}

@Composable
fun LarkTopAppBar(
    uiState: LarkUiState,
    playerActive: Boolean,
    sleepTimerActive: Boolean,
    onSearchChange: (String) -> Unit,
    onToggleSearch: (Boolean) -> Unit,
    onOpenEqualizer: () -> Unit,
    onOpenSleepTimer: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = LarkDarkSurface,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, LarkBorder)
    ) {
        if (uiState.isSearchActive) {
            // Expanded Search View
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Search songs, artists, videos...", color = LarkTextMuted) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LarkTealPrimary,
                        unfocusedBorderColor = LarkBorder,
                        focusedTextColor = LarkTextPrimary,
                        unfocusedTextColor = LarkTextPrimary
                    )
                )

                IconButton(onClick = { onToggleSearch(false) }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Search",
                        tint = LarkTextSecondary
                    )
                }
            }
        } else {
            // Standard Brand Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // H Player Dragon Emblem Icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(LarkTealPrimary, LarkTealSecondary)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dragon_logo),
                        contentDescription = "H Player Dragon Emblem",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Title: H Player
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "H",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        ),
                        color = LarkTealPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Player",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = LarkTextPrimary
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Search Icon
                IconButton(onClick = { onToggleSearch(true) }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = LarkTextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Sleep Timer Icon
                IconButton(onClick = onOpenSleepTimer) {
                    Icon(
                        imageVector = Icons.Default.Bedtime,
                        contentDescription = "Sleep Timer",
                        tint = if (sleepTimerActive) LarkPurpleAccent else LarkTextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Equalizer Icon
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
    }
}

@Composable
private fun navigationItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = Color.White,
    selectedTextColor = LarkTealPrimary,
    indicatorColor = LarkTealPrimary.copy(alpha = 0.25f),
    unselectedIconColor = LarkTextMuted,
    unselectedTextColor = LarkTextMuted
)

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier, color = LarkTextPrimary)
}
