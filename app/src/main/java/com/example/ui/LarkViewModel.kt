package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MediaStoreHelper
import com.example.data.SampleMediaData
import com.example.model.EqualizerPreset
import com.example.model.MediaItem
import com.example.model.MediaType
import com.example.model.Playlist
import com.example.model.VideoAspectRatio
import com.example.player.MediaPlayerController
import com.example.player.PlayerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab(val title: String) {
    VIDEO("Video"),
    MUSIC("Music"),
    PLAYLISTS("Playlists"),
    TOOLS("Tools")
}

data class LarkUiState(
    val selectedTab: MainTab = MainTab.MUSIC,
    val videoSubTab: Int = 0, // 0: All Videos, 1: Folders
    val musicSubTab: Int = 0, // 0: Songs, 1: Artists, 2: Albums, 3: Folders
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val showNowPlaying: Boolean = false,
    val showEqualizer: Boolean = false,
    val showSleepTimer: Boolean = false,
    val showCreatePlaylist: Boolean = false,
    val showAddUrlDialog: Boolean = false,
    val isScanningDevice: Boolean = false,
    val playlists: List<Playlist> = listOf(
        Playlist("fav", "Favorites", emptyList(), "❤️"),
        Playlist("recent", "Recently Played", emptyList(), "⏱️"),
        Playlist("chill", "Night Chill Vibes", emptyList(), "🌙"),
        Playlist("party", "Upbeat Energy", emptyList(), "⚡")
    ),
    val favoriteIds: Set<String> = setOf("sample_audio_1", "sample_audio_2", "sample_video_1"),
    val recentMediaIds: List<String> = listOf("sample_audio_1", "sample_video_1"),
    val hiddenMediaIds: Set<String> = emptySet(),
    val showHiddenVault: Boolean = false,
    val itemToDelete: MediaItem? = null,
    val snackbarMessage: String? = null
)

class LarkViewModel(application: Application) : AndroidViewModel(application) {

    val playerController = MediaPlayerController(application)
    val playerState: StateFlow<PlayerState> = playerController.state

    private val _uiState = MutableStateFlow(LarkUiState())
    val uiState: StateFlow<LarkUiState> = _uiState.asStateFlow()

    private val _audioList = MutableStateFlow<List<MediaItem>>(SampleMediaData.sampleAudioTracks)
    val audioList: StateFlow<List<MediaItem>> = _audioList.asStateFlow()

    private val _videoList = MutableStateFlow<List<MediaItem>>(SampleMediaData.sampleVideoTracks)
    val videoList: StateFlow<List<MediaItem>> = _videoList.asStateFlow()

    init {
        // Prepare initial playlist
        playerController.setPlaylist(_audioList.value)
    }

    fun selectTab(tab: MainTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun setVideoSubTab(index: Int) {
        _uiState.value = _uiState.value.copy(videoSubTab = index)
    }

    fun setMusicSubTab(index: Int) {
        _uiState.value = _uiState.value.copy(musicSubTab = index)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun toggleSearchActive(active: Boolean) {
        _uiState.value = _uiState.value.copy(isSearchActive = active, searchQuery = if (!active) "" else _uiState.value.searchQuery)
    }

    fun setShowNowPlaying(show: Boolean) {
        _uiState.value = _uiState.value.copy(showNowPlaying = show)
    }

    fun setShowEqualizer(show: Boolean) {
        _uiState.value = _uiState.value.copy(showEqualizer = show)
    }

    fun setShowSleepTimer(show: Boolean) {
        _uiState.value = _uiState.value.copy(showSleepTimer = show)
    }

    fun setShowCreatePlaylist(show: Boolean) {
        _uiState.value = _uiState.value.copy(showCreatePlaylist = show)
    }

    fun setShowAddUrlDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showAddUrlDialog = show)
    }

    fun playMediaItem(item: MediaItem, asBackgroundAudio: Boolean = false) {
        // Update recents
        val updatedRecents = listOf(item.id) + _uiState.value.recentMediaIds.filter { it != item.id }
        _uiState.value = _uiState.value.copy(recentMediaIds = updatedRecents.take(30))

        if (item.type == MediaType.AUDIO) {
            playerController.setPlaylist(_audioList.value)
            _uiState.value = _uiState.value.copy(showNowPlaying = true)
        } else {
            playerController.setPlaylist(_videoList.value)
            if (asBackgroundAudio) {
                _uiState.value = _uiState.value.copy(showNowPlaying = true)
            }
        }
        playerController.playMedia(item, asBackgroundAudio = asBackgroundAudio)
    }

    fun toggleFavorite(item: MediaItem) {
        val currentFavs = _uiState.value.favoriteIds.toMutableSet()
        if (currentFavs.contains(item.id)) {
            currentFavs.remove(item.id)
        } else {
            currentFavs.add(item.id)
        }
        _uiState.value = _uiState.value.copy(favoriteIds = currentFavs)
    }

    fun isFavorite(id: String): Boolean {
        return _uiState.value.favoriteIds.contains(id)
    }

    fun createPlaylist(name: String) {
        if (name.isBlank()) return
        val newPlaylist = Playlist(
            id = "pl_${System.currentTimeMillis()}",
            name = name.trim(),
            songIds = emptyList(),
            coverEmoji = listOf("🎧", "✨", "🔥", "🚀", "🎶", "💫").random()
        )
        _uiState.value = _uiState.value.copy(
            playlists = _uiState.value.playlists + newPlaylist,
            showCreatePlaylist = false
        )
    }

    fun addMediaToPlaylist(playlistId: String, mediaId: String) {
        val updated = _uiState.value.playlists.map { pl ->
            if (pl.id == playlistId && !pl.songIds.contains(mediaId)) {
                pl.copy(songIds = pl.songIds + mediaId)
            } else pl
        }
        _uiState.value = _uiState.value.copy(playlists = updated)
    }

    fun addCustomMedia(title: String, url: String, type: MediaType) {
        val item = MediaItem(
            id = "custom_${System.currentTimeMillis()}",
            title = title.ifBlank { if (type == MediaType.VIDEO) "Custom Video" else "Custom Track" },
            artist = "Imported Media",
            album = "Custom Stream",
            durationMs = 180000L,
            mediaUri = url,
            type = type,
            resolution = if (type == MediaType.VIDEO) "Stream HD" else null,
            folder = "Network Stream"
        )
        if (type == MediaType.VIDEO) {
            _videoList.value = listOf(item) + _videoList.value
        } else {
            _audioList.value = listOf(item) + _audioList.value
        }
        playMediaItem(item)
    }

    fun scanDeviceStorage() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isScanningDevice = true)
            val context = getApplication<Application>()
            val deviceAudio = MediaStoreHelper.queryDeviceAudio(context)
            val deviceVideo = MediaStoreHelper.queryDeviceVideo(context)

            if (deviceAudio.isNotEmpty()) {
                _audioList.value = deviceAudio + SampleMediaData.sampleAudioTracks
            }
            if (deviceVideo.isNotEmpty()) {
                _videoList.value = deviceVideo + SampleMediaData.sampleVideoTracks
            }
            _uiState.value = _uiState.value.copy(isScanningDevice = false)
        }
    }

    fun hideMedia(item: MediaItem) {
        _uiState.value = _uiState.value.copy(
            hiddenMediaIds = _uiState.value.hiddenMediaIds + item.id,
            snackbarMessage = "\"${item.title}\" hidden from library"
        )
    }

    fun unhideMedia(item: MediaItem) {
        _uiState.value = _uiState.value.copy(
            hiddenMediaIds = _uiState.value.hiddenMediaIds - item.id,
            snackbarMessage = "\"${item.title}\" restored to library"
        )
    }

    fun toggleHideMedia(item: MediaItem) {
        if (_uiState.value.hiddenMediaIds.contains(item.id)) {
            unhideMedia(item)
        } else {
            hideMedia(item)
        }
    }

    fun isHidden(id: String): Boolean {
        return _uiState.value.hiddenMediaIds.contains(id)
    }

    fun setItemToDelete(item: MediaItem?) {
        _uiState.value = _uiState.value.copy(itemToDelete = item)
    }

    fun deleteMedia(item: MediaItem) {
        val current = playerState.value.currentMedia
        if (current?.id == item.id) {
            playerController.closePlayer()
        }

        if (item.type == MediaType.VIDEO) {
            _videoList.value = _videoList.value.filter { it.id != item.id }
            playerController.setPlaylist(_videoList.value)
        } else {
            _audioList.value = _audioList.value.filter { it.id != item.id }
            playerController.setPlaylist(_audioList.value)
        }

        _uiState.value = _uiState.value.copy(
            itemToDelete = null,
            favoriteIds = _uiState.value.favoriteIds - item.id,
            recentMediaIds = _uiState.value.recentMediaIds.filter { it != item.id },
            hiddenMediaIds = _uiState.value.hiddenMediaIds - item.id,
            playlists = _uiState.value.playlists.map { pl ->
                pl.copy(songIds = pl.songIds.filter { it != item.id })
            },
            snackbarMessage = "\"${item.title}\" deleted"
        )
    }

    fun clearSnackbarMessage() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    fun setShowHiddenVault(show: Boolean) {
        _uiState.value = _uiState.value.copy(showHiddenVault = show)
    }

    override fun onCleared() {
        super.onCleared()
        playerController.release()
    }
}
