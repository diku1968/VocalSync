package com.example.ui.screens.pitch

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.VocalSyncViewModel
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.MeterGreen
import com.example.ui.theme.MeterRed
import com.example.ui.theme.StudioCardBg
import com.example.ui.theme.StudioCardBgElevated
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PitchAnalyzerDialog(
    viewModel: VocalSyncViewModel,
    onDismiss: () -> Unit
) {
    val currentPitch by viewModel.recorderManager.currentPitch.collectAsState()
    val isRecording by viewModel.recorderManager.isRecording.collectAsState()

    val animatedCents by animateFloatAsState(
        targetValue = currentPitch.centsOffset.toFloat(),
        label = "pitch_cents"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = StudioCardBg,
        modifier = Modifier.fillMaxWidth(0.95f),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Pitch Analyzer",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "YIN Fundamental Frequency Detection",
                        color = CyanAccent,
                        fontSize = 12.sp
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Large Musical Note Circle
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF133642))
                        .border(
                            2.dp,
                            if (currentPitch.isConfident && kotlin.math.abs(currentPitch.centsOffset) < 10) MeterGreen else CyanAccent,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (currentPitch.isConfident) currentPitch.fullNoteDisplay else "--",
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            fontFamily = FontFamily.SansSerif
                        )
                        Text(
                            text = currentPitch.frequencyDisplay,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CyanAccent
                        )
                    }
                }

                // Cents Tuning Gauge (-50 to +50 cents)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(StudioCardBgElevated)
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "-50 Flat", color = TextMuted, fontSize = 11.sp)
                        Text(
                            text = if (currentPitch.isConfident) "%+d cents".format(currentPitch.centsOffset) else "In Tune (0)",
                            color = if (kotlin.math.abs(currentPitch.centsOffset) < 10) MeterGreen else CyanAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(text = "+50 Sharp", color = TextMuted, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Gauge Track
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color(0xFF334155)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Center in-tune marker
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(10.dp)
                                .background(MeterGreen)
                        )
                        // Dynamic needle
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(14.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (kotlin.math.abs(animatedCents) < 10) MeterGreen else CyanAccent)
                        )
                    }
                }

                Text(
                    text = if (isRecording) {
                        "Analyzing live vocal input from microphone. Sing steady tones to view detected note and cents offset."
                    } else {
                        "Microphone recording is standby. Sing during capture or tap Start Capture to evaluate pitch."
                    },
                    color = TextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                modifier = Modifier.testTag("btn_close_pitch_analyzer")
            ) {
                Text("Done", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    )
}
