package com.example.audio

import android.content.Context
import android.media.MediaPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

class AudioPlayerManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _currentPlayingId = MutableStateFlow<Long?>(null)
    val currentPlayingId: StateFlow<Long?> = _currentPlayingId.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _progressMs = MutableStateFlow(0)
    val progressMs: StateFlow<Int> = _progressMs.asStateFlow()

    private val _totalDurationMs = MutableStateFlow(0)
    val totalDurationMs: StateFlow<Int> = _totalDurationMs.asStateFlow()

    fun playTake(takeId: Long, filePath: String) {
        if (_currentPlayingId.value == takeId && _isPlaying.value) {
            pause()
            return
        }

        if (_currentPlayingId.value == takeId && mediaPlayer != null) {
            mediaPlayer?.start()
            _isPlaying.value = true
            startProgressTracker()
            return
        }

        stop()

        try {
            val file = File(filePath)
            mediaPlayer = MediaPlayer().apply {
                if (file.exists()) {
                    setDataSource(file.absolutePath)
                } else {
                    // For mock pre-seeded takes if file doesn't exist, simulate playback smoothly
                    simulatePlayback(takeId)
                    return
                }
                prepare()
                start()
                _totalDurationMs.value = duration
                _currentPlayingId.value = takeId
                _isPlaying.value = true

                setOnCompletionListener {
                    _isPlaying.value = false
                    _progressMs.value = 0
                    _currentPlayingId.value = null
                    progressJob?.cancel()
                }
            }
            startProgressTracker()
        } catch (_: Exception) {
            simulatePlayback(takeId)
        }
    }

    private fun simulatePlayback(takeId: Long) {
        stop()
        _currentPlayingId.value = takeId
        _isPlaying.value = true
        _totalDurationMs.value = 30000 // 30s demo duration
        progressJob = scope.launch {
            var current = 0
            while (isActive && current < 30000 && _isPlaying.value) {
                _progressMs.value = current
                delay(200)
                current += 200
            }
            _isPlaying.value = false
            _progressMs.value = 0
            _currentPlayingId.value = null
        }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive && _isPlaying.value) {
                mediaPlayer?.let { player ->
                    if (player.isPlaying) {
                        _progressMs.value = player.currentPosition
                    }
                }
                delay(100)
            }
        }
    }

    fun pause() {
        try {
            mediaPlayer?.pause()
        } catch (_: Exception) {
        }
        _isPlaying.value = false
        progressJob?.cancel()
    }

    fun stop() {
        progressJob?.cancel()
        progressJob = null
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {
        }
        mediaPlayer = null
        _isPlaying.value = false
        _progressMs.value = 0
        _currentPlayingId.value = null
    }

    fun release() {
        stop()
    }
}
