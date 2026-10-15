package com.example.audio

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
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
import java.io.FileOutputStream
import java.io.RandomAccessFile
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.sqrt

class AudioRecorderManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var recordingJob: Job? = null
    private var timerJob: Job? = null

    private var audioRecord: AudioRecord? = null
    private var currentOutputFile: File? = null

    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _dbfs = MutableStateFlow(-60f)
    val dbfs: StateFlow<Float> = _dbfs.asStateFlow()

    private val _recordingDurationMs = MutableStateFlow(0L)
    val recordingDurationMs: StateFlow<Long> = _recordingDurationMs.asStateFlow()

    private val _waveformBars = MutableStateFlow(List(28) { 0.08f })
    val waveformBars: StateFlow<List<Float>> = _waveformBars.asStateFlow()

    private val _currentPitch = MutableStateFlow(PitchResult(0f, "--", 0, 0, false))
    val currentPitch: StateFlow<PitchResult> = _currentPitch.asStateFlow()

    fun hasRecordPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    fun startRecording(targetFile: File): Boolean {
        if (!hasRecordPermission()) return false
        if (_isRecording.value) return false

        val minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        val bufferSize = max(minBufSize * 2, 4096)

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                audioRecord?.release()
                audioRecord = null
                return false
            }

            currentOutputFile = targetFile
            val fos = FileOutputStream(targetFile)
            writeWavHeader(fos, channelConfig, sampleRate, audioFormat)

            audioRecord?.startRecording()
            _isRecording.value = true
            _recordingDurationMs.value = 0L

            val startTime = System.currentTimeMillis()
            timerJob = scope.launch {
                while (isActive && _isRecording.value) {
                    _recordingDurationMs.value = System.currentTimeMillis() - startTime
                    delay(50)
                }
            }

            recordingJob = scope.launch {
                val shortBuffer = ShortArray(bufferSize / 2)
                val byteBuffer = ByteArray(bufferSize)
                var totalBytesWritten = 0L
                var pitchSkipCounter = 0

                try {
                    while (isActive && _isRecording.value) {
                        val shortsRead = audioRecord?.read(shortBuffer, 0, shortBuffer.size) ?: -1
                        if (shortsRead > 0) {
                            // Convert shorts to bytes and write to file
                            for (i in 0 until shortsRead) {
                                val s = shortBuffer[i]
                                byteBuffer[i * 2] = (s.toInt() and 0xFF).toByte()
                                byteBuffer[i * 2 + 1] = ((s.toInt() shr 8) and 0xFF).toByte()
                            }
                            val bytesCount = shortsRead * 2
                            fos.write(byteBuffer, 0, bytesCount)
                            totalBytesWritten += bytesCount

                            // Calculate RMS & dBFS
                            var sumSquares = 0.0
                            for (i in 0 until shortsRead) {
                                sumSquares += shortBuffer[i] * shortBuffer[i]
                            }
                            val rms = sqrt(sumSquares / shortsRead)
                            val normalized = (rms / 32767.0).coerceIn(0.00001, 1.0)
                            val computedDbfs = (20.0 * log10(normalized)).toFloat().coerceIn(-60f, 0f)
                            _dbfs.value = computedDbfs

                            // Update waveform visualization bars
                            val barLevel = (normalized * 2.5).toFloat().coerceIn(0.08f, 1.0f)
                            val currentList = _waveformBars.value.toMutableList()
                            if (currentList.isNotEmpty()) {
                                currentList.removeAt(0)
                                currentList.add(barLevel)
                                _waveformBars.value = currentList
                            }

                            // Run YIN pitch detector every ~100ms
                            pitchSkipCounter++
                            if (pitchSkipCounter % 4 == 0) {
                                val pitch = PitchDetector.detectPitch(shortBuffer, sampleRate)
                                _currentPitch.value = pitch
                            }
                        }
                    }
                } finally {
                    try {
                        fos.flush()
                        fos.close()
                        updateWavHeader(targetFile, totalBytesWritten)
                    } catch (_: Exception) {
                    }
                }
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            stopRecording()
            return false
        }
    }

    fun stopRecording(): File? {
        if (!_isRecording.value) return null
        _isRecording.value = false
        timerJob?.cancel()
        timerJob = null
        recordingJob?.cancel()
        recordingJob = null

        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (_: Exception) {
        }
        audioRecord = null
        _dbfs.value = -60f

        val recorded = currentOutputFile
        currentOutputFile = null
        return recorded
    }

    private fun writeWavHeader(
        out: FileOutputStream,
        channels: Int,
        sampleRate: Int,
        encoding: Int
    ) {
        val numChannels = if (channels == AudioFormat.CHANNEL_IN_STEREO) 2 else 1
        val bitsPerSample = if (encoding == AudioFormat.ENCODING_PCM_8BIT) 8 else 16
        val byteRate = sampleRate * numChannels * bitsPerSample / 8
        val blockAlign = numChannels * bitsPerSample / 8

        val header = ByteArray(44)
        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        // 4-7: chunk size (updated later)
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        header[16] = 16 // Subchunk1Size for PCM
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1 // AudioFormat: 1 = PCM
        header[21] = 0
        header[22] = numChannels.toByte()
        header[23] = 0
        header[24] = (sampleRate and 0xFF).toByte()
        header[25] = ((sampleRate shr 8) and 0xFF).toByte()
        header[26] = ((sampleRate shr 16) and 0xFF).toByte()
        header[27] = ((sampleRate shr 24) and 0xFF).toByte()
        header[28] = (byteRate and 0xFF).toByte()
        header[29] = ((byteRate shr 8) and 0xFF).toByte()
        header[30] = ((byteRate shr 16) and 0xFF).toByte()
        header[31] = ((byteRate shr 24) and 0xFF).toByte()
        header[32] = blockAlign.toByte()
        header[33] = 0
        header[34] = bitsPerSample.toByte()
        header[35] = 0
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        // 40-43: data size (updated later)

        out.write(header, 0, 44)
    }

    private fun updateWavHeader(wavFile: File, totalAudioBytes: Long) {
        if (!wavFile.exists()) return
        val totalDataLen = totalAudioBytes + 36
        RandomAccessFile(wavFile, "rw").use { raf ->
            raf.seek(4)
            raf.write(
                byteArrayOf(
                    (totalDataLen and 0xff).toByte(),
                    ((totalDataLen shr 8) and 0xff).toByte(),
                    ((totalDataLen shr 16) and 0xff).toByte(),
                    ((totalDataLen shr 24) and 0xff).toByte()
                )
            )
            raf.seek(40)
            raf.write(
                byteArrayOf(
                    (totalAudioBytes and 0xff).toByte(),
                    ((totalAudioBytes shr 8) and 0xff).toByte(),
                    ((totalAudioBytes shr 16) and 0xff).toByte(),
                    ((totalAudioBytes shr 24) and 0xff).toByte()
                )
            )
        }
    }
}
