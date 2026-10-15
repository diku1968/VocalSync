package com.example.ui.screens.settings

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.VocalSyncViewModel
import com.example.ui.theme.CoralRed
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.StudioCardBg
import com.example.ui.theme.StudioCardBgElevated
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    viewModel: VocalSyncViewModel,
    modifier: Modifier = Modifier
) {
    val sampleRate by viewModel.wavSampleRate.collectAsState()
    val countdownDuration by viewModel.countdownDuration.collectAsState()
    val headphoneMonitoring by viewModel.headphoneMonitoring.collectAsState()
    val autoStartMetronome by viewModel.autoStartMetronome.collectAsState()
    val defaultTempo by viewModel.studioTempoBpm.collectAsState()
    val storageUsage by viewModel.totalStorageDisplay.collectAsState()
    val showClearDialog by viewModel.showClearStorageDialog.collectAsState()
    val privacyDialogType by viewModel.privacyDialogType.collectAsState()

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
            Text(
                text = "Studio Settings",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Audio engine, local storage, and privacy controls",
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        // Section 1: AUDIO ENGINE CONFIGURATION
        item {
            Text(
                text = "AUDIO ENGINE CONFIGURATION",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(StudioCardBg)
                    .border(1.dp, StudioCardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // WAV Sample Rate
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "WAV Sample Rate",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "16-bit uncompressed PCM capture",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F3647))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = sampleRate,
                            color = CyanAccent,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                HorizontalDivider(thickness = 1.dp, color = StudioCardBorder)

                // Recording Countdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Recording Countdown",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Pre-roll timer before voice recording starts",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(3, 5).forEach { seconds ->
                            val isSelected = countdownDuration == seconds
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0xFF0F3647) else Color(0xFF1E293B))
                                    .border(
                                        1.dp,
                                        if (isSelected) CyanAccent else StudioCardBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.setCountdownDuration(seconds) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .testTag("countdown_${seconds}s")
                            ) {
                                Text(
                                    text = "${seconds}s",
                                    color = if (isSelected) CyanAccent else TextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(thickness = 1.dp, color = StudioCardBorder)

                // Headphone Monitoring
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Headphone Monitoring",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Real-time low-latency vocal feedback when wired/bluetooth headset is connected",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }

                    Switch(
                        checked = headphoneMonitoring,
                        onCheckedChange = { viewModel.setHeadphoneMonitoring(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyanAccent,
                            checkedTrackColor = Color(0xFF0F3647),
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.testTag("switch_headphone_monitor")
                    )
                }

                HorizontalDivider(thickness = 1.dp, color = StudioCardBorder)

                // Auto-Start Metronome on Record
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Auto-Start Metronome on Record",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Automatically play click track whenever a new vocal take begins",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }

                    Switch(
                        checked = autoStartMetronome,
                        onCheckedChange = { viewModel.setAutoStartMetronome(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyanAccent,
                            checkedTrackColor = Color(0xFF0F3647),
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.testTag("switch_auto_metronome")
                    )
                }

                HorizontalDivider(thickness = 1.dp, color = StudioCardBorder)

                // Default Studio Tempo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Default Studio Tempo",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "$defaultTempo BPM • 4/4 Time",
                            fontSize = 12.sp,
                            color = CyanAccent
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(80, 100, 120).forEach { bpm ->
                            val isSelected = defaultTempo == bpm
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0xFF0F3647) else Color(0xFF1E293B))
                                    .border(
                                        1.dp,
                                        if (isSelected) CyanAccent else StudioCardBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.setStudioTempoBpm(bpm) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("tempo_${bpm}bpm")
                            ) {
                                Text(
                                    text = "$bpm",
                                    color = if (isSelected) CyanAccent else TextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: PRO AUDIO DSP & SOUND EFFECTS
        item {
            Text(
                text = "PRO AUDIO DSP & SOUND EFFECTS",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(StudioCardBg)
                    .border(1.dp, StudioCardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Dolby Digital 3D Sound Engine",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Cinema spatial widening, multi-comb reverb & 5-band EQ",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F3647))
                            .clickable { viewModel.openVocalFxStudio() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Open FX Studio",
                            color = CyanAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Section: GOOGLE ADMOB MONETIZATION
        item {
            Text(
                text = "GOOGLE ADMOB INTEGRATION",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(StudioCardBg)
                    .border(1.dp, StudioCardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "AdMob Status", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF064E3B))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(text = "Active & Configured", color = Color(0xFF34D399), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = "Banner Unit ID (Home_banner):\nca-app-pub-8497938951597030/1580148237",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Text(
                    text = "Interstitial Unit ID (can_init):\nca-app-pub-8497938951597030/6023624424",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }

        // Section 2: LOCAL-FIRST STORAGE & DATA
        item {
            Text(
                text = "LOCAL-FIRST STORAGE & DATA",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(StudioCardBg)
                    .border(1.dp, StudioCardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Local Storage Usage",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "WAV takes, enhanced masters, & 9:16 videos",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Text(
                        text = storageUsage,
                        color = CyanAccent,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Clear All Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF2E151E))
                        .border(1.dp, Color(0xFF7F1D1D), RoundedCornerShape(12.dp))
                        .clickable { viewModel.setShowClearStorageDialog(true) }
                        .padding(vertical = 12.dp)
                        .testTag("btn_clear_storage"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear storage",
                            tint = CoralRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Clear All Local Audio & Video Files",
                            color = CoralRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Section 3: PRIVACY & LEGAL (GOOGLE PLAY COMPLIANT)
        item {
            Text(
                text = "PRIVACY & LEGAL (GOOGLE PLAY COMPLIANT)",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(StudioCardBg)
                    .border(1.dp, StudioCardBorder, RoundedCornerShape(16.dp))
            ) {
                LegalItemRow(
                    icon = Icons.Default.Security,
                    iconTint = Color(0xFF38BDF8),
                    title = "Privacy Policy",
                    subtitle = "Offline-first. Zero audio uploads.",
                    testTag = "legal_privacy_policy",
                    onClick = { viewModel.setPrivacyDialogType("PRIVACY") }
                )

                HorizontalDivider(thickness = 1.dp, color = StudioCardBorder)

                LegalItemRow(
                    icon = Icons.Default.Description,
                    iconTint = CyanAccent,
                    title = "Terms of Service",
                    subtitle = "License and user-provided lyrics rights",
                    testTag = "legal_terms",
                    onClick = { viewModel.setPrivacyDialogType("TERMS") }
                )

                HorizontalDivider(thickness = 1.dp, color = StudioCardBorder)

                LegalItemRow(
                    icon = Icons.Default.VerifiedUser,
                    iconTint = Color(0xFF10B981),
                    title = "Google Play Data Safety",
                    subtitle = "Complete transparency of microphone & device permissions",
                    testTag = "legal_data_safety",
                    onClick = { viewModel.setPrivacyDialogType("DATA_SAFETY") }
                )
            }
        }

        // Section 4: App Info Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(StudioCardBg)
                    .border(1.dp, StudioCardBorder, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "VocalLyric Studio",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Version 1.0.1 (Release Build Ready)",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Crafted with native DSP algorithms and clean Jetpack Compose architecture.",
                    fontSize = 12.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Clear Storage Confirmation Dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowClearStorageDialog(false) },
            containerColor = StudioCardBg,
            title = {
                Text(
                    text = "Clear All Local Audio Files?",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "This will permanently delete all recorded WAV takes and cached audio files from your device. This action cannot be undone.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearAllStorage() },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralRed)
                ) {
                    Text("Clear All", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setShowClearStorageDialog(false) }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // Privacy & Legal Dialog
    if (privacyDialogType != null) {
        val title = when (privacyDialogType) {
            "PRIVACY" -> "Privacy Policy"
            "TERMS" -> "Terms of Service"
            else -> "Google Play Data Safety"
        }
        val content = when (privacyDialogType) {
            "PRIVACY" -> "VocalSync operates strictly as an offline-first studio application. We do NOT upload, stream, analyze, or share any of your audio recordings, lyrics, or microphone feeds to external cloud servers. All DSP operations, pitch analysis, and files remain strictly local to your device's sandbox storage."
            "TERMS" -> "By using VocalSync / VocalLyric Studio, you retain 100% intellectual property ownership of your original voice recordings and performance takes. Please ensure you only input or perform lyrics you own or have obtained the proper synchronization rights for."
            else -> "Data Safety Declaration:\n• Microphone: Used solely for capturing voice takes and real-time dBFS metering. Data is not shared.\n• Camera (Optional): Used solely on-device during Camera Sing Mode to preview your video while viewing teleprompted lyrics.\n• Storage: Used to write 16-bit uncompressed WAV takes to the app's private files folder."
        }

        AlertDialog(
            onDismissRequest = { viewModel.setPrivacyDialogType(null) },
            containerColor = StudioCardBg,
            title = { Text(text = title, color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = content,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.setPrivacyDialogType(null) },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
                ) {
                    Text("Understood", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun LegalItemRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    testTag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF162032)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}
