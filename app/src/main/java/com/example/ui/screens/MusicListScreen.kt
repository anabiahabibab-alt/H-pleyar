package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.MediaItem
import com.example.ui.components.SongCoverArt
import com.example.ui.theme.LarkBorder
import com.example.ui.theme.LarkDarkBackground
import com.example.ui.theme.LarkDarkCard
import com.example.ui.theme.LarkDarkSurface
import com.example.ui.theme.LarkPurpleAccent
import com.example.ui.theme.LarkRed
import com.example.ui.theme.LarkTealPrimary
import com.example.ui.theme.LarkTealSecondary
import com.example.ui.theme.LarkTextMuted
import com.example.ui.theme.LarkTextPrimary
import com.example.ui.theme.LarkTextSecondary

@Composable
fun MusicListScreen(
    songs: List<MediaItem>,
    subTabIndex: Int,
    searchQuery: String,
    favoriteIds: Set<String>,
    currentPlayingId: String?,
    onSelectSubTab: (Int) -> Unit,
    onSongClick: (MediaItem) -> Unit,
    onShuffleAll: () -> Unit,
    onToggleFavorite: (MediaItem) -> Unit,
    onScanStorage: () -> Unit,
    onAddToPlaylistClick: (MediaItem) -> Unit,
    onHideSong: (MediaItem) -> Unit = {},
    onDeleteSong: (MediaItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val filteredSongs = remember(songs, searchQuery) {
        if (searchQuery.isBlank()) songs
        else songs.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.artist.contains(searchQuery, ignoreCase = true) ||
            it.album.contains(searchQuery, ignoreCase = true)
        }
    }

    val artists = remember(filteredSongs) {
        filteredSongs.groupBy { it.artist }
    }

    val albums = remember(filteredSongs) {
        filteredSongs.groupBy { it.album }
    }

    val folders = remember(filteredSongs) {
        filteredSongs.groupBy { it.folder }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LarkDarkBackground)
            .testTag("music_list_screen")
    ) {
        // Subtab row: Tracks, Artists, Albums, Folders
        TabRow(
            selectedTabIndex = subTabIndex,
            containerColor = LarkDarkSurface,
            contentColor = LarkTealPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[subTabIndex]),
                    color = LarkTealPrimary,
                    height = 3.dp
                )
            },
            divider = { Spacer(modifier = Modifier.height(1.dp).background(LarkBorder)) }
        ) {
            val tabs = listOf("Tracks", "Artists", "Albums", "Folders")
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = subTabIndex == index,
                    onClick = { onSelectSubTab(index) },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (subTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                            color = if (subTabIndex == index) LarkTealPrimary else LarkTextSecondary
                        )
                    }
                )
            }
        }

        // Shuffle All & Scan Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Shuffle All Pill Button
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onShuffleAll() },
                color = LarkTealPrimary,
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Shuffle All (${filteredSongs.size})",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }

            // Quick scan icon
            IconButton(
                onClick = onScanStorage,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(LarkDarkCard)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Scan Audio",
                    tint = LarkTealPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        when (subTabIndex) {
            0 -> {
                // TRACKS VIEW
                if (filteredSongs.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No songs found",
                            color = LarkTextMuted,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 90.dp)
                    ) {
                        items(filteredSongs, key = { it.id }) { song ->
                            SongListItem(
                                song = song,
                                isPlaying = song.id == currentPlayingId,
                                isFavorite = favoriteIds.contains(song.id),
                                onSongClick = { onSongClick(song) },
                                onToggleFavorite = { onToggleFavorite(song) },
                                onAddToPlaylist = { onAddToPlaylistClick(song) },
                                onHideSong = { onHideSong(song) },
                                onDeleteSong = { onDeleteSong(song) }
                            )
                        }
                    }
                }
            }
            1 -> {
                // ARTISTS VIEW
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    artists.forEach { (artistName, artistSongs) ->
                        item(key = artistName) {
                            GroupCard(
                                icon = Icons.Default.Person,
                                title = artistName,
                                subtitle = "${artistSongs.size} tracks",
                                onClick = {
                                    if (artistSongs.isNotEmpty()) onSongClick(artistSongs.first())
                                }
                            )
                        }
                    }
                }
            }
            2 -> {
                // ALBUMS VIEW
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    albums.forEach { (albumName, albumSongs) ->
                        item(key = albumName) {
                            GroupCard(
                                icon = Icons.Default.Album,
                                title = albumName,
                                subtitle = "${albumSongs.firstOrNull()?.artist} • ${albumSongs.size} tracks",
                                onClick = {
                                    if (albumSongs.isNotEmpty()) onSongClick(albumSongs.first())
                                }
                            )
                        }
                    }
                }
            }
            3 -> {
                // FOLDERS VIEW
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    folders.forEach { (folderName, folderSongs) ->
                        item(key = folderName) {
                            GroupCard(
                                icon = Icons.Default.Folder,
                                title = folderName,
                                subtitle = "${folderSongs.size} audio files",
                                onClick = {
                                    if (folderSongs.isNotEmpty()) onSongClick(folderSongs.first())
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SongListItem(
    song: MediaItem,
    isPlaying: Boolean,
    isFavorite: Boolean,
    onSongClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onHideSong: () -> Unit = {},
    onDeleteSong: () -> Unit = {}
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSongClick() }
            .background(if (isPlaying) LarkTealPrimary.copy(alpha = 0.08f) else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cover Art Thumbnail
        Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            SongCoverArt(
                mediaItem = song,
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(10.dp)
            )
            if (isPlaying) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(2.dp, LarkTealPrimary, RoundedCornerShape(10.dp))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Title and Artist
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 15.sp
                ),
                color = if (isPlaying) LarkTealPrimary else LarkTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${song.artist} • ${song.formattedDuration}",
                style = MaterialTheme.typography.bodySmall,
                color = LarkTextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Favorite Heart
        IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isFavorite) LarkRed else LarkTextMuted,
                modifier = Modifier.size(20.dp)
            )
        }

        // Options dropdown
        Box {
            IconButton(
                onClick = { menuExpanded = true },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = LarkTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                modifier = Modifier.background(LarkDarkCard)
            ) {
                DropdownMenuItem(
                    text = { Text("Play", color = LarkTextPrimary) },
                    onClick = {
                        menuExpanded = false
                        onSongClick()
                    },
                    leadingIcon = {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = LarkTealPrimary)
                    }
                )
                DropdownMenuItem(
                    text = { Text("Add to Playlist", color = LarkTextPrimary) },
                    onClick = {
                        menuExpanded = false
                        onAddToPlaylist()
                    },
                    leadingIcon = {
                        Icon(Icons.Default.PlaylistAdd, contentDescription = null, tint = LarkTealPrimary)
                    }
                )
                DropdownMenuItem(
                    text = { Text("Hide Song", color = LarkTextPrimary) },
                    onClick = {
                        menuExpanded = false
                        onHideSong()
                    },
                    leadingIcon = {
                        Icon(Icons.Default.VisibilityOff, contentDescription = null, tint = LarkTextSecondary)
                    }
                )
                HorizontalDivider(
                    color = LarkBorder,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                DropdownMenuItem(
                    text = { Text("Delete", color = LarkRed) },
                    onClick = {
                        menuExpanded = false
                        onDeleteSong()
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = LarkRed)
                    }
                )
            }
        }
    }
}

@Composable
fun GroupCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = LarkDarkCard),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, LarkBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(LarkTealPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = LarkTealPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = LarkTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = LarkTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
