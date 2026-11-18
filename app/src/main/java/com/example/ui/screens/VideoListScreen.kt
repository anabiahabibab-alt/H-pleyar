package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Videocam
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
fun VideoListScreen(
    videos: List<MediaItem>,
    subTabIndex: Int,
    searchQuery: String,
    favoriteIds: Set<String>,
    onSelectSubTab: (Int) -> Unit,
    onVideoClick: (MediaItem) -> Unit,
    onPlayAsAudio: (MediaItem) -> Unit,
    onToggleFavorite: (MediaItem) -> Unit,
    onScanStorage: () -> Unit,
    onHideVideo: (MediaItem) -> Unit = {},
    onDeleteVideo: (MediaItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val filteredVideos = remember(videos, searchQuery) {
        if (searchQuery.isBlank()) videos
        else videos.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.folder.contains(searchQuery, ignoreCase = true)
        }
    }

    val folders = remember(filteredVideos) {
        filteredVideos.groupBy { it.folder }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LarkDarkBackground)
            .testTag("video_list_screen")
    ) {
        // Subtabs: All Videos & Folders
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
            Tab(
                selected = subTabIndex == 0,
                onClick = { onSelectSubTab(0) },
                text = {
                    Text(
                        text = "All Videos (${filteredVideos.size})",
                        fontWeight = if (subTabIndex == 0) FontWeight.Bold else FontWeight.Medium,
                        color = if (subTabIndex == 0) LarkTealPrimary else LarkTextSecondary
                    )
                }
            )
            Tab(
                selected = subTabIndex == 1,
                onClick = { onSelectSubTab(1) },
                text = {
                    Text(
                        text = "Folders (${folders.size})",
                        fontWeight = if (subTabIndex == 1) FontWeight.Bold else FontWeight.Medium,
                        color = if (subTabIndex == 1) LarkTealPrimary else LarkTextSecondary
                    )
                }
            )
        }

        // Scan storage prompt strip
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { onScanStorage() },
            color = LarkDarkCard,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, LarkBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = LarkTealPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Scan device storage for videos",
                        style = MaterialTheme.typography.bodySmall,
                        color = LarkTextSecondary
                    )
                }
                Text(
                    text = "Scan",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = LarkTealPrimary
                )
            }
        }

        if (subTabIndex == 0) {
            // ALL VIDEOS LIST
            if (filteredVideos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No videos found",
                        color = LarkTextMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 16.dp, end = 16.dp, top = 6.dp, bottom = 90.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredVideos, key = { it.id }) { video ->
                        VideoCardItem(
                            video = video,
                            isFavorite = favoriteIds.contains(video.id),
                            onVideoClick = { onVideoClick(video) },
                            onPlayAsAudio = { onPlayAsAudio(video) },
                            onToggleFavorite = { onToggleFavorite(video) },
                            onHideVideo = { onHideVideo(video) },
                            onDeleteVideo = { onDeleteVideo(video) }
                        )
                    }
                }
            }
        } else {
            // FOLDERS VIEW
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                folders.forEach { (folderName, folderVideos) ->
                    item(key = folderName) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    if (folderVideos.isNotEmpty()) {
                                        onVideoClick(folderVideos.first())
                                    }
                                },
                            colors = CardDefaults.cardColors(containerColor = LarkDarkCard),
                            shape = RoundedCornerShape(16.dp),
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
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(LarkTealPrimary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Folder,
                                        contentDescription = null,
                                        tint = LarkTealPrimary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = folderName,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = LarkTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${folderVideos.size} videos",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = LarkTextMuted
                                    )
                                }

                                IconButton(onClick = {
                                    if (folderVideos.isNotEmpty()) {
                                        onVideoClick(folderVideos.first())
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play Folder",
                                        tint = LarkTealPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VideoCardItem(
    video: MediaItem,
    isFavorite: Boolean,
    onVideoClick: () -> Unit,
    onPlayAsAudio: () -> Unit,
    onToggleFavorite: () -> Unit,
    onHideVideo: () -> Unit = {},
    onDeleteVideo: () -> Unit = {}
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onVideoClick() },
        colors = CardDefaults.cardColors(containerColor = LarkDarkCard),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, LarkBorder)
    ) {
        Column {
            // Thumbnail container with Duration & Resolution Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                SongCoverArt(
                    mediaItem = video,
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)
                )

                // Play triangle overlay button
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.55f))
                        .border(1.5.dp, LarkTealPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = LarkTealPrimary,
                        modifier = Modifier.size(30.dp)
                    )
                }

                // Top Resolution Badge
                if (!video.resolution.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(LarkTealPrimary)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = video.resolution,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }

                // Bottom Duration Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = video.formattedDuration,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White
                    )
                }
            }

            // Info row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = LarkTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "${video.folder} • ${if (video.formattedSize.isNotBlank()) video.formattedSize else video.artist}",
                        style = MaterialTheme.typography.bodySmall,
                        color = LarkTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Quick Play as Audio icon button
                IconButton(
                    onClick = onPlayAsAudio,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(LarkPurpleAccent.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = "Play as Audio in Background",
                        tint = LarkPurpleAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Favorite
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

                // More menu
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
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
                            text = { Text("Play Video", color = LarkTextPrimary) },
                            onClick = {
                                menuExpanded = false
                                onVideoClick()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = LarkTealPrimary)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Play as Audio (Background)", color = LarkTextPrimary) },
                            onClick = {
                                menuExpanded = false
                                onPlayAsAudio()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Headphones, contentDescription = null, tint = LarkPurpleAccent)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Hide Video", color = LarkTextPrimary) },
                            onClick = {
                                menuExpanded = false
                                onHideVideo()
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
                                onDeleteVideo()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = LarkRed)
                            }
                        )
                    }
                }
            }
        }
    }
}
