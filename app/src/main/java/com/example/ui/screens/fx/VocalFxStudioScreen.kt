package com.example.ui.screens.fx

import android.app.Activity
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.AdMobManager
import com.example.audio.dsp.VocalFxConfig
import com.example.data.model.VocalTake
import com.example.ui.VocalSyncViewModel
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanPurpleGradient
import com.example.ui.theme.MagentaAccent
import com.example.ui.theme.MeterGreen
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.StudioCardBg
import com.example.ui.theme.StudioCardBgElevated
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun VocalFxStudioScreen(
    viewModel: VocalSyncViewModel,
    targetTake: VocalTake?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var fxConfig by remember { mutableStateOf(VocalFxConfig.PRESET_DOLBY_DIGITAL) }
    var isProcessing by remember { mutableStateOf(false) }
    var processingProgress by remember { mutableStateOf(0f) }

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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Vocal FX Studio",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F3647))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "PRO DSP",
                                color = CyanAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = targetTake?.let { "Enhancing: ${it.title}" } ?: "Dolby Digital 3D, Reverb, EQ & Effects",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("btn_close_vocal_fx")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }
            }
        }

        // Section: ONE-TAP MASTER PRESETS
        item {
            Text(
                text = "STUDIO PRESETS",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val presets = listOf(
                    VocalFxConfig.PRESET_DOLBY_DIGITAL,
                    VocalFxConfig.PRESET_STUDIO_CLEAN,
                    VocalFxConfig.PRESET_POP_AIR,
                    VocalFxConfig.PRESET_CATHEDRAL,
                    VocalFxConfig.PRESET_TRAP_AUTOTUNE
                )

                items(presets) { preset ->
                    val isSelected = fxConfig.presetName == preset.presetName
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0xFF0F3647) else StudioCardBg)
                            .border(
                                1.dp,
                                if (isSelected) CyanAccent else StudioCardBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { fxConfig = preset }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .testTag("preset_${preset.presetName}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = preset.presetName,
                                color = if (isSelected) CyanAccent else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Section 1: DOLBY DIGITAL & 3D SPATIAL AUDIO
        item {
            FxCardContainer(
                title = "Dolby Digital & 3D Spatial Audio",
                subtitle = "Immersive stereo widening and cinematic acoustic field",
                icon = Icons.Default.SurroundSound,
                iconTint = CyanAccent,
                isEnabled = fxConfig.isDolbySpatialEnabled,
                onToggle = { fxConfig = fxConfig.copy(isDolbySpatialEnabled = it) }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Spatial Width Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Spatial 3D Width", color = TextSecondary, fontSize = 13.sp)
                            Text(
                                text = "${(fxConfig.spatialWidth * 100).toInt()}%",
                                color = CyanAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Slider(
                            value = fxConfig.spatialWidth,
                            onValueChange = { fxConfig = fxConfig.copy(spatialWidth = it) },
                            enabled = fxConfig.isDolbySpatialEnabled,
                            colors = SliderDefaults.colors(
                                thumbColor = CyanAccent,
                                activeTrackColor = CyanAccent,
                                inactiveTrackColor = Color(0xFF334155)
                            )
                        )
                    }

                    // Surround Depth Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Surround Sound Depth", color = TextSecondary, fontSize = 13.sp)
                            Text(
                                text = "${(fxConfig.spatialSurroundDepth * 100).toInt()}%",
                                color = CyanAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Slider(
                            value = fxConfig.spatialSurroundDepth,
                            onValueChange = { fxConfig = fxConfig.copy(spatialSurroundDepth = it) },
                            enabled = fxConfig.isDolbySpatialEnabled,
                            colors = SliderDefaults.colors(
                                thumbColor = CyanAccent,
                                activeTrackColor = CyanAccent,
                                inactiveTrackColor = Color(0xFF334155)
                            )
                        )
                    }
                }
            }
        }

        // Section 2: 5-BAND PARAMETRIC VOCAL EQUALIZER
        item {
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF133642)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Equalizer,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "5-Band Vocal Equalizer",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Sculpt warmth, body, clarity & air",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Text(
                        text = "RESET",
                        color = CyanAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                fxConfig = fxConfig.copy(
                                    eqLowGainDb = 0f,
                                    eqMidLowGainDb = 0f,
                                    eqMidGainDb = 0f,
                                    eqHighMidGainDb = 0f,
                                    eqHighAirGainDb = 0f
                                )
                            }
                            .padding(4.dp)
                    )
                }

                HorizontalDivider(thickness = 1.dp, color = StudioCardBorder)

                // 5 Bands Sliders
                EqBandRow(
                    label = "100 Hz (Sub/Warmth)",
                    valueDb = fxConfig.eqLowGainDb,
                    onValueChange = { fxConfig = fxConfig.copy(eqLowGainDb = it) }
                )
                EqBandRow(
                    label = "500 Hz (Mud Cut/Body)",
                    valueDb = fxConfig.eqMidLowGainDb,
                    onValueChange = { fxConfig = fxConfig.copy(eqMidLowGainDb = it) }
                )
                EqBandRow(
                    label = "1.5 kHz (Vocal Clarity)",
                    valueDb = fxConfig.eqMidGainDb,
                    onValueChange = { fxConfig = fxConfig.copy(eqMidGainDb = it) }
                )
                EqBandRow(
                    label = "4.0 kHz (Presence/Bite)",
                    valueDb = fxConfig.eqHighMidGainDb,
                    onValueChange = { fxConfig = fxConfig.copy(eqHighMidGainDb = it) }
                )
                EqBandRow(
                    label = "10.0 kHz (Studio Air Sheen)",
                    valueDb = fxConfig.eqHighAirGainDb,
                    onValueChange = { fxConfig = fxConfig.copy(eqHighAirGainDb = it) }
                )
            }
        }

        // Section 3: STUDIO REVERB
        item {
            FxCardContainer(
                title = "Studio Reverb",
                subtitle = "Acoustic space and depth simulation",
                icon = Icons.Default.Waves,
                iconTint = PurpleAccent,
                isEnabled = fxConfig.isReverbEnabled,
                onToggle = { fxConfig = fxConfig.copy(isReverbEnabled = it) }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Room Preset Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Studio Room", "Concert Hall", "Plate Reverb", "Cathedral Air").forEach { room ->
                            val isSelected = fxConfig.reverbPreset == room
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) PurpleAccent else Color(0xFF1E293B))
                                    .clickable { fxConfig = fxConfig.copy(reverbPreset = room) }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = room.replace(" Reverb", "").replace(" Air", ""),
                                    color = if (isSelected) Color.White else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // Decay Time Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Reverb Decay", color = TextSecondary, fontSize = 13.sp)
                            Text(
                                text = "%.1fs".format(fxConfig.reverbDecaySeconds),
                                color = MagentaAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Slider(
                            value = fxConfig.reverbDecaySeconds,
                            onValueChange = { fxConfig = fxConfig.copy(reverbDecaySeconds = it) },
                            valueRange = 0.5f..5.0f,
                            enabled = fxConfig.isReverbEnabled,
                            colors = SliderDefaults.colors(
                                thumbColor = MagentaAccent,
                                activeTrackColor = PurpleAccent,
                                inactiveTrackColor = Color(0xFF334155)
                            )
                        )
                    }

                    // Wet/Dry Mix Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Wet / Dry Mix", color = TextSecondary, fontSize = 13.sp)
                            Text(
                                text = "${(fxConfig.reverbWetDry * 100).toInt()}%",
                                color = MagentaAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Slider(
                            value = fxConfig.reverbWetDry,
                            onValueChange = { fxConfig = fxConfig.copy(reverbWetDry = it) },
                            valueRange = 0.0f..0.8f,
                            enabled = fxConfig.isReverbEnabled,
                            colors = SliderDefaults.colors(
                                thumbColor = MagentaAccent,
                                activeTrackColor = PurpleAccent,
                                inactiveTrackColor = Color(0xFF334155)
                            )
                        )
                    }
                }
            }
        }

        // Section 4: DYNAMICS & AUTO-TUNE
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(StudioCardBg)
                    .border(1.dp, StudioCardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "VOCAL DYNAMICS & TUNING",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                // Vocal Compressor
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Studio Compressor & Limiter", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Smooth vocal peaks & punchy presence", color = TextSecondary, fontSize = 12.sp)
                    }
                    Switch(
                        checked = fxConfig.isCompressorEnabled,
                        onCheckedChange = { fxConfig = fxConfig.copy(isCompressorEnabled = it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyanAccent,
                            checkedTrackColor = Color(0xFF0F3647),
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = Color(0xFF1E293B)
                        )
                    )
                }

                // Pitch Quantizer / Auto-Tune
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Auto-Tune Pitch Quantizer", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Chromatic tuning & pitch correction", color = TextSecondary, fontSize = 12.sp)
                    }
                    Switch(
                        checked = fxConfig.isAutoTuneEnabled,
                        onCheckedChange = { fxConfig = fxConfig.copy(isAutoTuneEnabled = it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MagentaAccent,
                            checkedTrackColor = Color(0xFF381A4B),
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = Color(0xFF1E293B)
                        )
                    )
                }

                // De-Esser
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "De-Esser (Sibilance Reducer)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Tame harsh 6-8 kHz 'S' and 'T' frequencies", color = TextSecondary, fontSize = 12.sp)
                    }
                    Switch(
                        checked = fxConfig.isDeEsserEnabled,
                        onCheckedChange = { fxConfig = fxConfig.copy(isDeEsserEnabled = it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyanAccent,
                            checkedTrackColor = Color(0xFF0F3647),
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = Color(0xFF1E293B)
                        )
                    )
                }
            }
        }

        // Apply & Save Enhanced Master Button
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(CyanAccent, PurpleAccent, MagentaAccent)
                        )
                    )
                    .clickable(enabled = !isProcessing) {
                        isProcessing = true
                        val activity = context as? Activity
                        if (activity != null) {
                            // Show user's Interstitial Ad (can_init)
                            AdMobManager.showInterstitial(activity) {
                                viewModel.applyFxToTake(targetTake, fxConfig) {
                                    isProcessing = false
                                    onDismiss()
                                }
                            }
                        } else {
                            viewModel.applyFxToTake(targetTake, fxConfig) {
                                isProcessing = false
                                onDismiss()
                            }
                        }
                    }
                    .testTag("btn_apply_vocal_fx"),
                contentAlignment = Alignment.Center
            ) {
                if (isProcessing) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.5.dp
                        )
                        Text(
                            text = "Rendering Dolby 3D Master...",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "APPLY & SAVE DOLBY 3D MASTER",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun FxCardContainer(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF133642)),
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
                    Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = subtitle, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
                }
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = CyanAccent,
                    checkedTrackColor = Color(0xFF0F3647),
                    uncheckedThumbColor = TextMuted,
                    uncheckedTrackColor = Color(0xFF1E293B)
                )
            )
        }

        if (isEnabled) {
            HorizontalDivider(thickness = 1.dp, color = StudioCardBorder)
            content()
        }
    }
}

@Composable
private fun EqBandRow(
    label: String,
    valueDb: Float,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = TextSecondary, fontSize = 13.sp)
            Text(
                text = "%+.1f dB".format(valueDb),
                color = if (valueDb > 0) CyanAccent else if (valueDb < 0) MagentaAccent else TextMuted,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
        Slider(
            value = valueDb,
            onValueChange = onValueChange,
            valueRange = -12.0f..12.0f,
            colors = SliderDefaults.colors(
                thumbColor = CyanAccent,
                activeTrackColor = CyanAccent,
                inactiveTrackColor = Color(0xFF334155)
            )
        )
    }
}
