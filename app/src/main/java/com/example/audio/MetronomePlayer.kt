package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class MetronomePlayer {
    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentBeat = MutableStateFlow(0) // 0, 1, 2, 3 (4/4 time)
    val currentBeat: StateFlow<Int> = _currentBeat.asStateFlow()

    private val sampleRate = 44100
    private val highClick = generateClick(1200.0, 0.035) // Beat 1
    private val lowClick = generateClick(800.0, 0.025)   // Beats 2, 3, 4

    private var audioTrack: AudioTrack? = null

    init {
        try {
            val minBufSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBufSize.coerceAtLeast(4096))
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()
            audioTrack?.play()
        } catch (_: Exception) {
        }
    }

    fun start(bpm: Int) {
        stop()
        _isPlaying.value = true
        val intervalMs = (60_000L / bpm).coerceAtLeast(100L)

        job = scope.launch {
            var beat = 0
            while (isActive) {
                _currentBeat.value = beat
                val buffer = if (beat == 0) highClick else lowClick
                audioTrack?.write(buffer, 0, buffer.size)

                beat = (beat + 1) % 4
                delay(intervalMs)
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
        _isPlaying.value = false
        _currentBeat.value = 0
    }

    fun release() {
        stop()
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (_: Exception) {
        }
        audioTrack = null
    }

    private fun generateClick(frequency: Double, durationSec: Double): ShortArray {
        val numSamples = (durationSec * sampleRate).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            // Exponential decay envelope
            val decay = (1.0 - (i.toDouble() / numSamples))
            val sample = (sin(2.0 * PI * frequency * t) * decay * 32767 * 0.8).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }
}
