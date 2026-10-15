package com.example.ui.screens.studio

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VocalTake
import com.example.ui.StudioTab
import com.example.ui.VocalSyncViewModel
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.MagentaAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.StudioCardBg
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun StudioScreen(
    viewModel: VocalSyncViewModel,
    modifier: Modifier = Modifier
) {
    val recentTakes by viewModel.recentTakes.collectAsState()
    val allTakes by viewModel.allTakes.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Status badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(CyanAccent)
                )
                Text(
                    text = "VOCAL STUDIO ONLINE",
                    color = CyanAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Title & Wave icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "VocalLyric",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF162032))
                        .clickable { viewModel.openVocalFxStudio() }
                        .testTag("btn_header_vocal_fx"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Vocal FX Studio",
                        tint = CyanAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Text(
                text = "Sing with lyrics. Record raw. Enhance professionally.",
                color = TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )
        }

        // Top Action Buttons (Side by Side)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // RAW VOCAL
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(CyanAccent, Color(0xFF67E8F9), Color(0xFFA78BFA))
                            )
                        )
                        .clickable { viewModel.setTab(StudioTab.RECORD) }
                        .padding(horizontal = 12.dp)
                        .testTag("btn_raw_vocal"),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Raw Vocal",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "RAW\nVOCAL",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black,
                            lineHeight = 16.sp
                        )
                    }
                }

                // CAMERA + LYRICS
                Box(
                    modifier = Modifier
                        .weight(1.15f)
                        .height(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(PurpleAccent, MagentaAccent)
                            )
                        )
                        .clickable { viewModel.setShowCameraSingMode(true) }
                        .padding(horizontal = 12.dp)
                        .testTag("btn_camera_lyrics"),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Camera + Lyrics",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "CAMERA + LYRICS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Section: STUDIO MODULES
        item {
            Text(
                text = "STUDIO MODULES",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2x2 Grid Modules
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StudioModuleCard(
                        title = "Sing with Lyrics",
                        subtitle = "Teleprompter + Mic",
                        icon = Icons.Default.MusicNote,
                        iconTint = MagentaAccent,
                        iconBg = Color(0xFF381A4B),
                        modifier = Modifier.weight(1f),
                        testTag = "module_sing_lyrics",
                        onClick = { viewModel.setTab(StudioTab.LYRICS) }
                    )

                    StudioModuleCard(
                        title = "Camera Sing Mode",
                        subtitle = "Front Cam + Lyrics",
                        icon = Icons.Default.Videocam,
                        iconTint = CyanAccent,
                        iconBg = Color(0xFF133642),
                        modifier = Modifier.weight(1f),
                        testTag = "module_camera_sing",
                        onClick = { viewModel.setShowCameraSingMode(true) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StudioModuleCard(
                        title = "Pitch Analyzer",
                        subtitle = "YIN Note Detection",
                        icon = Icons.Default.Explore,
                        iconTint = Color(0xFF38BDF8),
                        iconBg = Color(0xFF182A45),
                        modifier = Modifier.weight(1f),
                        testTag = "module_pitch_analyzer",
                        onClick = { viewModel.setShowPitchAnalyzer(true) }
                    )

                    StudioModuleCard(
                        title = "My Recordings",
                        subtitle = "${allTakes.size} Saved Takes",
                        icon = Icons.Default.QueueMusic,
                        iconTint = Color(0xFFFBBF24),
                        iconBg = Color(0xFF3A2B15),
                        modifier = Modifier.weight(1f),
                        testTag = "module_my_recordings",
                        onClick = { viewModel.setTab(StudioTab.MY_TAKES) }
                    )
                }
            }
        }

        // Section: RECENT TAKES
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT TAKES (${recentTakes.size})",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                Text(
                    text = "View All",
                    color = CyanAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable { viewModel.setTab(StudioTab.MY_TAKES) }
                        .padding(4.dp)
                        .testTag("btn_view_all_takes")
                )
            }
        }

        items(recentTakes) { take ->
            RecentTakeCard(
                take = take,
                onPlay = { viewModel.playerManager.playTake(take.id, take.filePath) },
                onEnhance = { viewModel.openVocalFxStudio(take) }
            )
        }

        item {
            com.example.ui.components.AdBannerView(modifier = Modifier.padding(vertical = 4.dp))
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StudioModuleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .height(134.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(StudioCardBg)
            .border(1.dp, StudioCardBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
            .testTag(testTag),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextMuted,
                maxLines = 1
            )
        }
    }
}

@Composable
fun RecentTakeCard(
    take: VocalTake,
    onPlay: () -> Unit,
    onEnhance: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(StudioCardBg)
            .border(1.dp, StudioCardBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onPlay)
            .padding(14.dp)
            .testTag("recent_take_${take.id}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF162032)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = CyanAccent,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = take.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${take.durationDisplay} • ${take.dateFormatted} • ${take.timeFormatted}",
                    fontSize = 12.sp,
                    color = CyanAccent.copy(alpha = 0.9f)
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF0F3647))
                .clickable(onClick = onEnhance)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = take.preset.replace("PRO: ", ""),
                color = CyanAccent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
