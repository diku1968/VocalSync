package com.example.ui.screens.record

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Subject
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.StudioTab
import com.example.ui.VocalSyncViewModel
import com.example.ui.components.DbfsMeter
import com.example.ui.components.WaveformVisualizer
import com.example.ui.theme.CoralRed
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.StudioCardBg
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun RecordScreen(
    viewModel: VocalSyncViewModel,
    modifier: Modifier = Modifier
) {
    val isRecording by viewModel.recorderManager.isRecording.collectAsState()
    val dbfs by viewModel.recorderManager.dbfs.collectAsState()
    val durationMs by viewModel.recorderManager.recordingDurationMs.collectAsState()
    val waveformBars by viewModel.recorderManager.waveformBars.collectAsState()
    val activeLyric by viewModel.activeLyricForTeleprompter.collectAsState()
    val isMetronomePlaying by viewModel.metronome.isPlaying.collectAsState()
    val currentBeat by viewModel.metronome.currentBeat.collectAsState()
    val tempoBpm by viewModel.studioTempoBpm.collectAsState()
    val countdownRemaining by viewModel.countdownRemaining.collectAsState()
    val headphoneMonitoring by viewModel.headphoneMonitoring.collectAsState()

    var showBpmPicker by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.triggerRecordAction()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_recording")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "RAW A",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "Raw vocal capture",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }

            // Quick icon toggles
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StudioIconButton(
                    icon = Icons.Default.Videocam,
                    tint = CyanAccent,
                    testTag = "record_btn_cam",
                    onClick = { viewModel.setShowCameraSingMode(true) }
                )
                StudioIconButton(
                    icon = Icons.Default.Subject,
                    tint = TextSecondary,
                    testTag = "record_btn_lyrics",
                    onClick = { viewModel.setTab(StudioTab.LYRICS) }
                )
                StudioIconButton(
                    icon = Icons.Default.GraphicEq,
                    tint = CyanAccent,
                    testTag = "record_btn_eq",
                    onClick = { viewModel.openVocalFxStudio() }
                )
                StudioIconButton(
                    icon = Icons.Default.Headphones,
                    tint = if (headphoneMonitoring) CyanAccent else TextMuted,
                    testTag = "record_btn_headphones",
                    onClick = { viewModel.setHeadphoneMonitoring(!headphoneMonitoring) }
                )
                StudioIconButton(
                    icon = Icons.Default.MusicNote,
                    tint = TextSecondary,
                    testTag = "record_btn_load_lyrics",
                    onClick = { viewModel.setTab(StudioTab.LYRICS) }
                )
            }
        }

        // Metronome strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(StudioCardBg)
                .border(1.dp, StudioCardBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Metronome on/off pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E293B))
                    .clickable { viewModel.toggleMetronome() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("btn_toggle_metronome")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "⏱",
                        fontSize = 12.sp
                    )
                    Text(
                        text = if (isMetronomePlaying) "METRONOME ON" else "METRONOME OFF",
                        color = if (isMetronomePlaying) CyanAccent else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 4-beat dot indicator
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                for (b in 0 until 4) {
                    val isCurrent = isMetronomePlaying && currentBeat == b
                    Box(
                        modifier = Modifier
                            .size(if (isCurrent) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isCurrent) CyanAccent else Color(0xFF334155)
                            )
                    )
                }
            }

            // Tempo selector pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E293B))
                    .clickable { showBpmPicker = !showBpmPicker }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("btn_tempo_selector")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "$tempoBpm BPM",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "(4/4)",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Tempo options",
                        tint = CyanAccent,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // Tempo picker chips when opened
        if (showBpmPicker) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(72, 80, 100, 120, 140).forEach { bpm ->
                    val isSelected = tempoBpm == bpm
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) CyanAccent else Color(0xFF1E293B))
                            .clickable {
                                viewModel.setStudioTempoBpm(bpm)
                                showBpmPicker = false
                            }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$bpm",
                            color = if (isSelected) Color.Black else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Center Display: Large Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(StudioCardBg)
                .border(1.dp, StudioCardBorder, RoundedCornerShape(16.dp))
                .padding(vertical = 24.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF133642)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Studio Mic",
                    tint = CyanAccent,
                    modifier = Modifier.size(30.dp)
                )
            }

            Text(
                text = "A Cappella Recording Console",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = if (activeLyric != null) {
                    "Singing: \"${activeLyric?.title}\" (${activeLyric?.tempoBpm} BPM)"
                } else {
                    "Sing freely without lyrics or tap 'Load Lyrics' to teleprompt your song."
                },
                fontSize = 13.sp,
                color = if (activeLyric != null) CyanAccent else TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1E293B))
                    .clickable { viewModel.setTab(StudioTab.LYRICS) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("btn_load_lyrics_pill")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Load Lyrics",
                        tint = CyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (activeLyric != null) "Change Lyrics" else "Load Lyrics",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Real-time audio waveform visualizer
        WaveformVisualizer(
            bars = waveformBars,
            isRecording = isRecording
        )

        // Dynamic dBFS audio meter
        DbfsMeter(dbfs = dbfs)

        // Digital timer readout
        val minutes = (durationMs / 1000) / 60
        val seconds = (durationMs / 1000) % 60
        val centiseconds = (durationMs % 1000) / 10
        val timerFormatted = "%02d:%02d.%02d".format(minutes, seconds, centiseconds)

        Text(
            text = if (countdownRemaining > 0) "00:0$countdownRemaining.00" else timerFormatted,
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = if (countdownRemaining > 0) CyanAccent else if (isRecording) CoralRed else TextPrimary,
            letterSpacing = 1.sp
        )

        // Large circular Red record button
        Box(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .size(90.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isRecording || countdownRemaining > 0) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(CoralRed.copy(alpha = 0.28f))
                )
            }

            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(CoralRed)
                    .clickable {
                        if (countdownRemaining > 0) {
                            viewModel.cancelCountdown()
                        } else if (!viewModel.recorderManager.hasRecordPermission()) {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        } else {
                            viewModel.triggerRecordAction()
                        }
                    }
                    .testTag("btn_main_record"),
                contentAlignment = Alignment.Center
            ) {
                if (countdownRemaining > 0) {
                    Text(
                        text = "$countdownRemaining",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                } else if (isRecording) {
                    // Stop icon square
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White)
                    )
                }
            }
        }

        com.example.ui.components.AdBannerView(modifier = Modifier.padding(top = 4.dp))

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun StudioIconButton(
    icon: ImageVector,
    tint: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color(0xFF162032))
            .clickable(onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
    }
}
