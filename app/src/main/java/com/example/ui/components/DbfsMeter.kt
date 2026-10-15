package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.MeterGreen
import com.example.ui.theme.MeterRed
import com.example.ui.theme.MeterYellow
import com.example.ui.theme.StudioCardBgElevated
import com.example.ui.theme.TextMuted

@Composable
fun DbfsMeter(
    dbfs: Float,
    modifier: Modifier = Modifier
) {
    // Total segments = 24
    val totalSegments = 24
    // dbfs is between -60f and 0f. Normalized from 0f to 1f
    val normalized = ((dbfs + 60f) / 60f).coerceIn(0f, 1f)
    val activeSegments = (normalized * totalSegments).toInt()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(StudioCardBgElevated)
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("input_level_meter")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "INPUT LEVEL",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
            Text(
                text = if (dbfs <= -59.5f) "-60 dBFS" else "%.0f dBFS".format(dbfs),
                color = CyanAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until totalSegments) {
                val isActive = i < activeSegments
                val segmentColor = when {
                    i >= 20 -> MeterRed
                    i >= 15 -> MeterYellow
                    else -> MeterGreen
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(10.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (isActive) segmentColor else segmentColor.copy(alpha = 0.16f)
                        )
                )
            }
        }
    }
}
