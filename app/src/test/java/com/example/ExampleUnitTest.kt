package com.example

import com.example.audio.PitchDetector
import com.example.audio.dsp.AudioDspProcessor
import com.example.audio.dsp.VocalFxConfig
import com.example.data.model.LrcParser
import com.example.data.model.VocalTake
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI
import kotlin.math.sin

class ExampleUnitTest {
    @Test
    fun lrcParser_parsesTimestampsCorrectly() {
        val lrc = """
            [00:04.50] Step into the neon light
            [00:12.00] In this room you're not alone
        """.trimIndent()

        val parsed = LrcParser.parse(lrc)
        assertEquals(2, parsed.size)
        assertEquals(4500L, parsed[0].timestampMs)
        assertEquals("Step into the neon light", parsed[0].text)
        assertEquals(12000L, parsed[1].timestampMs)
        assertEquals("In this room you're not alone", parsed[1].text)
    }

    @Test
    fun vocalTake_durationFormatting() {
        val take = VocalTake(
            id = 1,
            filePath = "test.wav",
            durationSeconds = 49,
            dateFormatted = "Oct 06, 2026",
            timeFormatted = "14:06"
        )
        assertEquals("00:49", take.durationDisplay)

        val takeLong = VocalTake(
            id = 2,
            filePath = "test2.wav",
            durationSeconds = 125,
            dateFormatted = "Oct 06, 2026",
            timeFormatted = "14:10"
        )
        assertEquals("02:05", takeLong.durationDisplay)
    }

    @Test
    fun pitchDetector_detectsSineToneA4() {
        val sampleRate = 44100
        val freq = 440.0 // A4
        val numSamples = 2048
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            buffer[i] = (sin(2.0 * PI * freq * t) * 20000).toInt().toShort()
        }

        val result = PitchDetector.detectPitch(buffer, sampleRate)
        assertTrue(result.isConfident)
        assertEquals("A", result.noteName)
        assertEquals(4, result.octave)
        assertTrue("Frequency should be near 440Hz", kotlin.math.abs(result.pitchHz - 440f) < 5f)
    }

    @Test
    fun dspProcessor_appliesDolbySpatialStereo() {
        val sampleRate = 44100
        val numSamples = 1000
        val monoBuffer = ShortArray(numSamples) { 10000.toShort() }

        val dolbyConfig = VocalFxConfig.PRESET_DOLBY_DIGITAL
        assertTrue(dolbyConfig.isDolbySpatialEnabled)

        val processedStereo = AudioDspProcessor.processVocal(monoBuffer, sampleRate, dolbyConfig)
        // Stereo output has 2x samples (interleaved L, R)
        assertEquals(numSamples * 2, processedStereo.size)
        assertNotNull(processedStereo)
    }

    @Test
    fun dspProcessor_presetsAreProperlyConfigured() {
        assertEquals("Dolby Digital 3D", VocalFxConfig.PRESET_DOLBY_DIGITAL.presetName)
        assertEquals("Studio Clean", VocalFxConfig.PRESET_STUDIO_CLEAN.presetName)
        assertEquals("Pop Vocal Air", VocalFxConfig.PRESET_POP_AIR.presetName)
        assertTrue(VocalFxConfig.PRESET_TRAP_AUTOTUNE.isAutoTuneEnabled)
    }
}
