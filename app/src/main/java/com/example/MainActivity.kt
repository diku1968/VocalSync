package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.StudioTab
import com.example.ui.VocalSyncViewModel
import com.example.ui.components.BottomNavBar
import com.example.ui.screens.camera.CameraSingModeDialog
import com.example.ui.screens.lyrics.LyricsScreen
import com.example.ui.screens.pitch.PitchAnalyzerDialog
import com.example.ui.screens.record.RecordScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.studio.StudioScreen
import com.example.ui.screens.takes.TakesScreen
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.VocalSyncTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VocalSyncTheme {
                VocalSyncApp()
            }
        }
    }
}

@Composable
fun VocalSyncApp(
    viewModel: VocalSyncViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val showCameraSingMode by viewModel.showCameraSingMode.collectAsState()
    val showPitchAnalyzer by viewModel.showPitchAnalyzer.collectAsState()
    val showVocalFxStudio by viewModel.showVocalFxStudio.collectAsState()
    val targetTakeForFx by viewModel.targetTakeForFx.collectAsState()

    // Handle Android system back button
    BackHandler(enabled = showCameraSingMode || showPitchAnalyzer || showVocalFxStudio || currentTab != StudioTab.STUDIO) {
        when {
            showVocalFxStudio -> viewModel.closeVocalFxStudio()
            showCameraSingMode -> viewModel.setShowCameraSingMode(false)
            showPitchAnalyzer -> viewModel.setShowPitchAnalyzer(false)
            currentTab != StudioTab.STUDIO -> viewModel.setTab(StudioTab.STUDIO)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg),
        bottomBar = {
            BottomNavBar(
                currentTab = currentTab,
                onTabSelected = { viewModel.setTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                StudioTab.STUDIO -> StudioScreen(viewModel = viewModel)
                StudioTab.RECORD -> RecordScreen(viewModel = viewModel)
                StudioTab.LYRICS -> LyricsScreen(viewModel = viewModel)
                StudioTab.MY_TAKES -> TakesScreen(viewModel = viewModel)
                StudioTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
            }

            // Camera Sing Mode Fullscreen Overlay
            if (showCameraSingMode) {
                CameraSingModeDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.setShowCameraSingMode(false) }
                )
            }

            // Pitch Analyzer Dialog
            if (showPitchAnalyzer) {
                PitchAnalyzerDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.setShowPitchAnalyzer(false) }
                )
            }

            // Vocal FX Studio Overlay (Dolby 3D, Reverb, EQ)
            if (showVocalFxStudio) {
                com.example.ui.screens.fx.VocalFxStudioScreen(
                    viewModel = viewModel,
                    targetTake = targetTakeForFx,
                    onDismiss = { viewModel.closeVocalFxStudio() }
                )
            }
        }
    }
}
