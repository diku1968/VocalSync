package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.StudioTab
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.TextMuted

@Composable
fun BottomNavBar(
    currentTab: StudioTab,
    onTabSelected: (StudioTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(StudioDarkBg)
            .navigationBarsPadding()
    ) {
        HorizontalDivider(thickness = 1.dp, color = StudioCardBorder)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                label = "Studio",
                icon = Icons.Default.Home,
                isSelected = currentTab == StudioTab.STUDIO,
                testTag = "nav_tab_studio",
                onClick = { onTabSelected(StudioTab.STUDIO) }
            )

            NavItem(
                label = "Record",
                icon = Icons.Default.Mic,
                isSelected = currentTab == StudioTab.RECORD,
                testTag = "nav_tab_record",
                onClick = { onTabSelected(StudioTab.RECORD) }
            )

            NavItem(
                label = "Lyrics",
                icon = Icons.Default.MusicNote,
                isSelected = currentTab == StudioTab.LYRICS,
                testTag = "nav_tab_lyrics",
                onClick = { onTabSelected(StudioTab.LYRICS) }
            )

            NavItem(
                label = "My Takes",
                icon = Icons.Default.QueueMusic,
                isSelected = currentTab == StudioTab.MY_TAKES,
                testTag = "nav_tab_my_takes",
                onClick = { onTabSelected(StudioTab.MY_TAKES) }
            )

            NavItem(
                label = "Settings",
                icon = Icons.Default.Settings,
                isSelected = currentTab == StudioTab.SETTINGS,
                testTag = "nav_tab_settings",
                onClick = { onTabSelected(StudioTab.SETTINGS) }
            )
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (isSelected) Color(0xFF133642) else Color.Transparent
                )
                .padding(horizontal = 16.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) CyanAccent else TextMuted
            )
        }

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) CyanAccent else TextMuted,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
