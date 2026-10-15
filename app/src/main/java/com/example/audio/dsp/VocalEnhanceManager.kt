package com.example.audio.dsp

import android.content.Context
import com.example.data.model.VocalTake
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VocalEnhanceManager(private val context: Context) {

    suspend fun enhanceTake(
        take: VocalTake,
        config: VocalFxConfig,
        onProgress: (Float) -> Unit = {}
    ): VocalTake = withContext(Dispatchers.IO) {
        onProgress(0.1f)
        val sourceFile = File(take.filePath)
        val sampleRate = 44100

        val inputShorts = if (sourceFile.exists() && sourceFile.length() > 44) {
            readPcmWav(sourceFile)
        } else {
            // Generate synthetic vocal sine harmonic track if mock file
            generateVocalDemoSamples(sampleRate, take.durationSeconds.coerceIn(5, 30))
        }

        onProgress(0.4f)
        val processedShorts = AudioDspProcessor.processVocal(inputShorts, sampleRate, config)
        onProgress(0.7f)

        val outputDir = File(context.filesDir, "enhanced_takes").apply { mkdirs() }
        val outputFile = File(outputDir, "Master_${System.currentTimeMillis()}.wav")

        val isStereo = config.isDolbySpatialEnabled
        writeStereoWav(outputFile, processedShorts, sampleRate, isStereo)
        onProgress(1.0f)

        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
        val now = Date()

        val enhancedPresetTitle = when (config.presetName) {
            "Dolby Digital 3D" -> "PRO: Dolby 3D"
            "Studio Clean" -> "PRO: Studio Clean"
            "Pop Vocal Air" -> "PRO: Pop Air"
            "Cathedral Air" -> "PRO: Cathedral Reverb"
            "Modern Auto-Tune" -> "PRO: Auto-Tune"
            else -> "PRO: ${config.presetName}"
        }

        take.copy(
            id = 0, // Auto-generate new DB row
            title = "${take.title} (${config.presetName})",
            filePath = outputFile.absolutePath,
            preset = enhancedPresetTitle,
            dateFormatted = dateFormat.format(now),
            timeFormatted = timeFormat.format(now),
            createdAt = System.currentTimeMillis(),
            fileSizeBytes = outputFile.length()
        )
    }

    private fun readPcmWav(file: File): ShortArray {
        val totalBytes = file.length().toInt() - 44
        if (totalBytes <= 0) return ShortArray(0)

        val byteBuffer = ByteArray(totalBytes)
        FileInputStream(file).use { fis ->
            fis.skip(44) // Skip WAV header
            fis.read(byteBuffer)
        }

        val shortCount = totalBytes / 2
        val shorts = ShortArray(shortCount)
        ByteBuffer.wrap(byteBuffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts)
        return shorts
    }

    private fun generateVocalDemoSamples(sampleRate: Int, durationSec: Int): ShortArray {
        val totalSamples = sampleRate * durationSec
        val shorts = ShortArray(totalSamples)
        val f0 = 220.0 // A3 vocal tone
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            // Rich vocal formant simulation (fundamental + 2nd & 3rd harmonics + vibrato)
            val vibrato = kotlin.math.sin(2.0 * Math.PI * 5.0 * t) * 4.0
            val s1 = kotlin.math.sin(2.0 * Math.PI * (f0 + vibrato) * t) * 16000.0
            val s2 = kotlin.math.sin(2.0 * Math.PI * (f0 * 2.0) * t) * 7000.0
            val s3 = kotlin.math.sin(2.0 * Math.PI * (f0 * 3.0) * t) * 3500.0
            shorts[i] = (s1 + s2 + s3).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return shorts
    }

    private fun writeStereoWav(file: File, samples: ShortArray, sampleRate: Int, isStereo: Boolean) {
        val channels = if (isStereo) 2 else 1
        val bitsPerSample = 16
        val byteRate = sampleRate * channels * bitsPerSample / 8
        val blockAlign = channels * bitsPerSample / 8
        val dataSize = samples.size * 2

        FileOutputStream(file).use { fos ->
            val header = ByteArray(44)
            header[0] = 'R'.code.toByte()
            header[1] = 'I'.code.toByte()
            header[2] = 'F'.code.toByte()
            header[3] = 'F'.code.toByte()
            val chunkSize = dataSize + 36
            header[4] = (chunkSize and 0xff).toByte()
            header[5] = ((chunkSize shr 8) and 0xff).toByte()
            header[6] = ((chunkSize shr 16) and 0xff).toByte()
            header[7] = ((chunkSize shr 24) and 0xff).toByte()
            header[8] = 'W'.code.toByte()
            header[9] = 'A'.code.toByte()
            header[10] = 'V'.code.toByte()
            header[11] = 'E'.code.toByte()
            header[12] = 'f'.code.toByte()
            header[13] = 'm'.code.toByte()
            header[14] = 't'.code.toByte()
            header[15] = ' '.code.toByte()
            header[16] = 16
            header[17] = 0
            header[18] = 0
            header[19] = 0
            header[20] = 1 // PCM
            header[21] = 0
            header[22] = channels.toByte()
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
            header[40] = (dataSize and 0xff).toByte()
            header[41] = ((dataSize shr 8) and 0xff).toByte()
            header[42] = ((dataSize shr 16) and 0xff).toByte()
            header[43] = ((dataSize shr 24) and 0xff).toByte()
            fos.write(header)

            val byteBuf = ByteArray(dataSize)
            ByteBuffer.wrap(byteBuf).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(samples)
            fos.write(byteBuf)
        }
    }
}
