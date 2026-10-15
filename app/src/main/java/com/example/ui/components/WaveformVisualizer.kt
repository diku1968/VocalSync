package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.StudioCardBgElevated

@Composable
fun WaveformVisualizer(
    bars: List<Float>,
    isRecording: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(StudioCardBgElevated)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bars.forEach { barFactor ->
                val animatedHeight by animateFloatAsState(
                    targetValue = if (isRecording) (barFactor * 38f).coerceIn(4f, 40f) else 4f,
                    animationSpec = tween(durationMillis = 80),
                    label = "waveform_bar"
                )

                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(animatedHeight.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (isRecording) CyanAccent else Color(0xFF334155)
                        )
                )
            }
        }
    }
}
