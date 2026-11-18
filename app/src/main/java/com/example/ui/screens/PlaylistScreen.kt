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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.model.MediaItem
import com.example.model.Playlist
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

@Composable
fun PlaylistScreen(
    playlists: List<Playlist>,
    allMedia: List<MediaItem>,
    favoriteIds: Set<String>,
    recentIds: List<String>,
    hiddenCount: Int = 0,
    onPlayMedia: (MediaItem) -> Unit,
    onCreatePlaylist: (String) -> Unit,
    onOpenHiddenVault: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedPlaylist by remember { mutableStateOf<Playlist?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }

    if (selectedPlaylist != null) {
        // Detailed Playlist Track View
        val playlist = selectedPlaylist!!
        val playlistSongs = remember(playlist, allMedia, favoriteIds, recentIds) {
            when (playlist.id) {
                "fav" -> allMedia.filter { favoriteIds.contains(it.id) }
                "recent" -> recentIds.mapNotNull { id -> allMedia.find { it.id == id } }
                else -> playlist.songIds.mapNotNull { id -> allMedia.find { it.id == id } }
            }
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(LarkDarkBackground)
                .padding(bottom = 80.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { selectedPlaylist = null }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = LarkTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = playlist.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = LarkTextPrimary
                    )
                    Text(
                        text = "${playlistSongs.size} tracks",
                        style = MaterialTheme.typography.bodySmall,
                        color = LarkTealPrimary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                if (playlistSongs.isNotEmpty()) {
                    IconButton(
                        onClick = { onPlayMedia(playlistSongs.first()) },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(LarkTealPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play All",
                            tint = Color.White
                        )
                    }
                }
            }

            if (playlistSongs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "This playlist is currently empty",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LarkTextMuted
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(playlistSongs, key = { it.id }) { item ->
                        SongListItem(
                            song = item,
                            isPlaying = false,
                            isFavorite = favoriteIds.contains(item.id),
                            onSongClick = { onPlayMedia(item) },
                            onToggleFavorite = {},
                            onAddToPlaylist = {}
                        )
                    }
                }
            }
        }
    } else {
        // PLAYLISTS LIST VIEW
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(LarkDarkBackground)
                .padding(horizontal = 16.dp)
                .testTag("playlist_screen")
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Create Playlist & Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Library",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = LarkTextPrimary
                )

                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { showCreateDialog = true },
                    color = LarkDarkCard,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, LarkBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = LarkTealPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "New Playlist",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = LarkTealPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Favorites Hero Card
                val favCount = favoriteIds.size
                item {
                    PlaylistCard(
                        emoji = "❤️",
                        title = "Favorites",
                        subtitle = "$favCount favorite tracks",
                        gradient = listOf(LarkRed.copy(alpha = 0.3f), LarkDarkCard),
                        onClick = {
                            selectedPlaylist = Playlist("fav", "Favorites")
                        }
                    )
                }

                // Recently Played Card
                val recentCount = recentIds.size
                item {
                    PlaylistCard(
                        emoji = "⏱️",
                        title = "Recently Played",
                        subtitle = "$recentCount items played",
                        gradient = listOf(LarkPurpleAccent.copy(alpha = 0.3f), LarkDarkCard),
                        onClick = {
                            selectedPlaylist = Playlist("recent", "Recently Played")
                        }
                    )
                }

                // Hidden Media Vault Card
                item {
                    PlaylistCard(
                        emoji = "🔒",
                        title = "Hidden Media Vault",
                        subtitle = if (hiddenCount > 0) "$hiddenCount hidden tracks & videos" else "Private / hidden media",
                        gradient = listOf(Color(0xFF334155).copy(alpha = 0.4f), LarkDarkCard),
                        onClick = onOpenHiddenVault
                    )
                }

                // Custom User Playlists
                items(playlists.filter { it.id != "fav" && it.id != "recent" }, key = { it.id }) { pl ->
                    PlaylistCard(
                        emoji = pl.coverEmoji,
                        title = pl.name,
                        subtitle = "${pl.songIds.size} tracks",
                        gradient = listOf(LarkTealPrimary.copy(alpha = 0.2f), LarkDarkCard),
                        onClick = {
                            selectedPlaylist = pl
                        }
                    )
                }
            }
        }
    }

    // Create playlist dialog
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = LarkDarkCard,
            title = {
                Text(
                    text = "Create Playlist",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = LarkTextPrimary
                )
            },
            text = {
                OutlinedTextField(
                    value = newPlaylistName,
                    onValueChange = { newPlaylistName = it },
                    placeholder = { Text("Playlist name", color = LarkTextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LarkTealPrimary,
                        unfocusedBorderColor = LarkBorder,
                        focusedTextColor = LarkTextPrimary,
                        unfocusedTextColor = LarkTextPrimary
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPlaylistName.isNotBlank()) {
                            onCreatePlaylist(newPlaylistName)
                            newPlaylistName = ""
                            showCreateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LarkTealPrimary)
                ) {
                    Text("Create", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel", color = LarkTextSecondary)
                }
            }
        )
    }
}

@Composable
fun PlaylistCard(
    emoji: String,
    title: String,
    subtitle: String,
    gradient: List<Color>,
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
                .background(Brush.horizontalGradient(gradient))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = LarkTextPrimary
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = LarkTextMuted
                )
            }

            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Open",
                tint = LarkTealPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
