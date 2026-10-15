package com.example.ui.screens.lyrics

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SongLyric
import com.example.ui.StudioTab
import com.example.ui.VocalSyncViewModel
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.MagentaAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.StudioCardBg
import com.example.ui.theme.StudioCardBgElevated
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LyricsScreen(
    viewModel: VocalSyncViewModel,
    modifier: Modifier = Modifier
) {
    val lyricsList by viewModel.allLyrics.collectAsState()
    val activeLyric by viewModel.activeLyricForTeleprompter.collectAsState()
    val showEditorDialog by viewModel.showLyricsEditorDialog.collectAsState()

    var showTeleprompterModal by remember { mutableStateOf(false) }
    var selectedForTeleprompter by remember { mutableStateOf<SongLyric?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Lyrics Studio",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Teleprompter & synchronized lyrics manager",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                // '+' FAB
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(CyanAccent)
                        .clickable { viewModel.setShowLyricsEditorDialog(true) }
                        .testTag("fab_add_lyrics"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Lyrics",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Action Buttons (Side by Side)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Write / Paste Lyrics
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(CyanAccent, Color(0xFF67E8F9), Color(0xFFA78BFA))
                            )
                        )
                        .clickable { viewModel.setShowLyricsEditorDialog(true) }
                        .padding(horizontal = 12.dp)
                        .testTag("btn_write_lyrics"),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Write Lyrics",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Write / Paste\nLyrics",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            lineHeight = 16.sp
                        )
                    }
                }

                // Import .LRC / .TXT
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(StudioCardBgElevated)
                        .border(1.dp, StudioCardBorder, RoundedCornerShape(16.dp))
                        .clickable {
                            // Pre-load an example LRC track for immediate use
                            viewModel.saveLyrics(
                                title = "Starry Horizons",
                                artist = "VocalLyric Studio",
                                content = """
                                    [00:00.00] (Ready - 80 BPM)
                                    [00:04.00] In the quiet of the night
                                    [00:08.00] We find our guiding light
                                    [00:12.00] Singing loud and singing true
                                    [00:16.00] Every harmonic note for you
                                """.trimIndent(),
                                tempo = 80
                            )
                        }
                        .padding(horizontal = 12.dp)
                        .testTag("btn_import_lrc"),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Import LRC",
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Import .LRC / .TXT",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        // Info notice
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(StudioCardBg)
                    .border(1.dp, StudioCardBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(PurpleAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = MagentaAccent,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = "Lyrics are stored locally on your device. Please enter lyrics you own or have permission to perform.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }

        // Empty state vs Lyrics list
        if (lyricsList.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(StudioCardBg)
                        .border(1.dp, StudioCardBorder, RoundedCornerShape(16.dp))
                        .padding(vertical = 40.dp, horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(48.dp)
                    )

                    Text(
                        text = "Your Lyrics Library is Empty",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Text(
                        text = "Paste lyrics or import your .LRC/.TXT files to sing along with real-time scrolling.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        } else {
            item {
                Text(
                    text = "SAVED SONGS (${lyricsList.size})",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }

            items(lyricsList) { lyric ->
                val isSelectedForConsole = activeLyric?.id == lyric.id

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(StudioCardBg)
                        .border(
                            1.dp,
                            if (isSelectedForConsole) CyanAccent else StudioCardBorder,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(14.dp)
                        .testTag("lyric_item_${lyric.id}"),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = lyric.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${lyric.artist} • ${lyric.tempoBpm} BPM ${if (lyric.isLrc) "• LRC Timed" else ""}",
                                fontSize = 12.sp,
                                color = CyanAccent
                            )
                        }

                        IconButton(
                            onClick = { viewModel.deleteLyric(lyric) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete lyric",
                                tint = TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelectedForConsole) CyanAccent else Color(0xFF1E293B))
                                .clickable {
                                    viewModel.setActiveLyricForTeleprompter(
                                        if (isSelectedForConsole) null else lyric
                                    )
                                    viewModel.setTab(StudioTab.RECORD)
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isSelectedForConsole) "Active on Console" else "Load to Record",
                                color = if (isSelectedForConsole) Color.Black else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E293B))
                                .clickable {
                                    selectedForTeleprompter = lyric
                                    showTeleprompterModal = true
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Teleprompter View",
                                color = CyanAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Write / Paste Dialog
    if (showEditorDialog) {
        LyricsEditorDialog(
            onDismiss = { viewModel.setShowLyricsEditorDialog(false) },
            onSave = { title, artist, content, tempo ->
                viewModel.saveLyrics(title, artist, content, tempo)
            }
        )
    }

    // Fullscreen Teleprompter Modal
    if (showTeleprompterModal && selectedForTeleprompter != null) {
        TeleprompterDialog(
            lyric = selectedForTeleprompter!!,
            onDismiss = { showTeleprompterModal = false }
        )
    }
}

@Composable
private fun LyricsEditorDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var artist by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var tempoText by remember { mutableStateOf("80") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = StudioCardBg,
        title = {
            Text(
                text = "Write or Paste Lyrics",
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Song Title") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = StudioCardBgElevated,
                        unfocusedContainerColor = StudioCardBgElevated,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = artist,
                    onValueChange = { artist = it },
                    label = { Text("Artist / Vocalist") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = StudioCardBgElevated,
                        unfocusedContainerColor = StudioCardBgElevated,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Lyrics (Plain text or [00:12.00] LRC)") },
                    minLines = 6,
                    maxLines = 10,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = StudioCardBgElevated,
                        unfocusedContainerColor = StudioCardBgElevated,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val tempo = tempoText.toIntOrNull() ?: 80
                    onSave(title, artist, content, tempo)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
            ) {
                Text("Save to Studio", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun TeleprompterDialog(
    lyric: SongLyric,
    onDismiss: () -> Unit
) {
    var isAutoScrolling by remember { mutableStateOf(false) }
    var scrollSpeed by remember { mutableStateOf(1.0f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF090D14),
        modifier = Modifier.fillMaxWidth(0.95f),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = lyric.title, color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text(text = "${lyric.artist} • ${lyric.tempoBpm} BPM", color = CyanAccent, fontSize = 12.sp)
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Visibility, contentDescription = "Close", tint = TextSecondary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .height(340.dp)
                    .background(Color(0xFF090D14))
                    .padding(8.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val lines = lyric.content.lines().filter { it.isNotBlank() }
                    items(lines) { line ->
                        Text(
                            text = line,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (line.startsWith("[")) CyanAccent else Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
            ) {
                Text("Done", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    )
}
