package com.example.ui.screens.takes

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VocalTake
import com.example.ui.StudioTab
import com.example.ui.TakesFilter
import com.example.ui.VocalSyncViewModel
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.StudioCardBg
import com.example.ui.theme.StudioCardBgElevated
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TakesScreen(
    viewModel: VocalSyncViewModel,
    modifier: Modifier = Modifier
) {
    val takes by viewModel.filteredTakes.collectAsState()
    val allTakes by viewModel.allTakes.collectAsState()
    val activeFilter by viewModel.takesFilter.collectAsState()
    val searchQuery by viewModel.takesSearchQuery.collectAsState()
    val currentPlayingId by viewModel.playerManager.currentPlayingId.collectAsState()
    val isPlaying by viewModel.playerManager.isPlaying.collectAsState()

    var takeToRename by remember { mutableStateOf<VocalTake?>(null) }
    var renameText by remember { mutableStateOf("") }
    var takeForPreset by remember { mutableStateOf<VocalTake?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Vocal Takes",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${allTakes.size} recorded takes in local studio",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                // Cyan circular FAB with Mic icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(CyanAccent)
                        .clickable { viewModel.setTab(StudioTab.RECORD) }
                        .testTag("fab_quick_record"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Record take",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Search Bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(StudioCardBg)
                    .border(1.dp, StudioCardBorder, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setTakesSearchQuery(it) },
                        placeholder = {
                            Text(
                                text = "Search takes by song or lyrics...",
                                color = TextMuted,
                                fontSize = 14.sp
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_takes_input")
                    )
                }
            }
        }

        // Filter Pills
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // All Takes (X)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (activeFilter == TakesFilter.ALL) Color(0xFF0F3647) else StudioCardBg
                        )
                        .border(
                            1.dp,
                            if (activeFilter == TakesFilter.ALL) CyanAccent else StudioCardBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { viewModel.setTakesFilter(TakesFilter.ALL) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("filter_all_takes")
                ) {
                    Text(
                        text = "All Takes (${allTakes.size})",
                        color = if (activeFilter == TakesFilter.ALL) CyanAccent else TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Favorites ★
                val favCount = allTakes.count { it.isFavorite }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (activeFilter == TakesFilter.FAVORITES) Color(0xFF0F3647) else StudioCardBg
                        )
                        .border(
                            1.dp,
                            if (activeFilter == TakesFilter.FAVORITES) CyanAccent else StudioCardBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { viewModel.setTakesFilter(TakesFilter.FAVORITES) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("filter_fav_takes")
                ) {
                    Text(
                        text = "Favorites ★ ($favCount)",
                        color = if (activeFilter == TakesFilter.FAVORITES) CyanAccent else TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // AdMob Studio Banner
        item {
            com.example.ui.components.AdBannerView(modifier = Modifier.padding(vertical = 2.dp))
        }

        // Takes List
        if (takes.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No vocal takes found",
                        color = TextSecondary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            items(takes) { take ->
                val isCurrentPlaying = currentPlayingId == take.id && isPlaying

                TakeCardItem(
                    take = take,
                    isPlaying = isCurrentPlaying,
                    onPlayPause = {
                        viewModel.playerManager.playTake(take.id, take.filePath)
                    },
                    onToggleFavorite = { viewModel.toggleFavorite(take) },
                    onRename = {
                        takeToRename = take
                        renameText = take.title
                    },
                    onChangePreset = { takeForPreset = take },
                    onOpenFx = { viewModel.openVocalFxStudio(take) },
                    onDelete = { viewModel.deleteTake(take) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Rename Dialog
    if (takeToRename != null) {
        AlertDialog(
            onDismissRequest = { takeToRename = null },
            containerColor = StudioCardBg,
            title = { Text(text = "Rename Vocal Take", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = StudioCardBgElevated,
                        unfocusedContainerColor = StudioCardBgElevated,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.renameTake(takeToRename!!, renameText)
                        takeToRename = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
                ) {
                    Text("Rename", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { takeToRename = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // Change Preset Dialog
    if (takeForPreset != null) {
        AlertDialog(
            onDismissRequest = { takeForPreset = null },
            containerColor = StudioCardBg,
            title = { Text("Select Audio Preset", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("PRO: Studio Clean", "PRO: Soft Vocal", "PRO: Radio Warmth", "PRO: Air Reverb", "PRO: Heavy Punch").forEach { preset ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (takeForPreset!!.preset == preset) Color(0xFF0F3647) else StudioCardBgElevated)
                                .clickable {
                                    viewModel.updatePreset(takeForPreset!!, preset)
                                    takeForPreset = null
                                }
                                .padding(12.dp)
                        ) {
                            Text(
                                text = preset,
                                color = if (takeForPreset!!.preset == preset) CyanAccent else TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { takeForPreset = null }) {
                    Text("Close", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun TakeCardItem(
    take: VocalTake,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onToggleFavorite: () -> Unit,
    onRename: () -> Unit,
    onChangePreset: () -> Unit,
    onOpenFx: () -> Unit = {},
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(StudioCardBg)
            .border(1.dp, StudioCardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
            .testTag("take_card_${take.id}"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Play / Pause Circle
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF26334D))
                        .clickable(onClick = onPlayPause)
                        .testTag("take_play_btn_${take.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play take",
                        tint = if (isPlaying) CyanAccent else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = take.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = take.durationDisplay,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                        Text(
                            text = " • ${take.dateFormatted} • ${take.timeFormatted}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Star favorite + 3 dots menu
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (take.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (take.isFavorite) Color(0xFFFBBF24) else TextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(StudioCardBgElevated)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Dolby 3D & Vocal FX", color = CyanAccent, fontWeight = FontWeight.Bold) },
                            leadingIcon = { Icon(Icons.Default.Tune, contentDescription = null, tint = CyanAccent) },
                            onClick = {
                                showMenu = false
                                onOpenFx()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Rename Take", color = TextPrimary) },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = TextSecondary) },
                            onClick = {
                                showMenu = false
                                onRename()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Quick Preset", color = TextPrimary) },
                            leadingIcon = { Icon(Icons.Default.Tune, contentDescription = null, tint = TextSecondary) },
                            onClick = {
                                showMenu = false
                                onChangePreset()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete", color = Color(0xFFFF5252)) },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFFF5252)) },
                            onClick = {
                                showMenu = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }

        // Preset Badge below
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF0F3647))
                .clickable(onClick = onChangePreset)
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = take.preset,
                color = CyanAccent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
