package com.example.audio

import kotlin.math.log2
import kotlin.math.roundToInt

data class PitchResult(
    val pitchHz: Float,
    val noteName: String,
    val octave: Int,
    val centsOffset: Int,
    val isConfident: Boolean
) {
    val fullNoteDisplay: String
        get() = if (isConfident && pitchHz > 40f) "$noteName$octave" else "--"

    val frequencyDisplay: String
        get() = if (isConfident && pitchHz > 40f) "%.1f Hz".format(pitchHz) else "0.0 Hz"
}

object PitchDetector {
    private val NOTE_NAMES = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    private const val YIN_THRESHOLD = 0.15f

    /**
     * YIN pitch detection algorithm on PCM short buffer.
     */
    fun detectPitch(buffer: ShortArray, sampleRate: Int): PitchResult {
        val halfSize = buffer.size / 2
        if (halfSize < 32) {
            return PitchResult(0f, "--", 0, 0, false)
        }

        val yinBuffer = FloatArray(halfSize)

        // Step 1: Difference function
        for (tau in 0 until halfSize) {
            var sum = 0.0
            for (i in 0 until halfSize) {
                val delta = (buffer[i] - buffer[i + tau]).toDouble()
                sum += delta * delta
            }
            yinBuffer[tau] = sum.toFloat()
        }

        // Step 2: Cumulative mean normalized difference function
        yinBuffer[0] = 1f
        var runningSum = 0f
        for (tau in 1 until halfSize) {
            runningSum += yinBuffer[tau]
            yinBuffer[tau] = if (runningSum == 0f) 1f else (yinBuffer[tau] * tau) / runningSum
        }

        // Step 3: Absolute threshold
        var tauEstimate = -1
        var tau = 2
        while (tau < halfSize) {
            if (yinBuffer[tau] < YIN_THRESHOLD) {
                while (tau + 1 < halfSize && yinBuffer[tau + 1] < yinBuffer[tau]) {
                    tau++
                }
                tauEstimate = tau
                break
            }
            tau++
        }

        if (tauEstimate == -1) {
            // Find minimum if no value below threshold
            var minTau = 2
            var minVal = yinBuffer[2]
            for (tau in 3 until halfSize) {
                if (yinBuffer[tau] < minVal) {
                    minVal = yinBuffer[tau]
                    minTau = tau
                }
            }
            if (minVal < 0.35f) {
                tauEstimate = minTau
            } else {
                return PitchResult(0f, "--", 0, 0, false)
            }
        }

        // Step 4: Parabolic interpolation for fine tau
        val x0 = if (tauEstimate > 0) tauEstimate - 1 else tauEstimate
        val x2 = if (tauEstimate + 1 < halfSize) tauEstimate + 1 else tauEstimate
        val betterTau = if (x0 == tauEstimate) {
            if (yinBuffer[tauEstimate] <= yinBuffer[x2]) tauEstimate.toFloat() else x2.toFloat()
        } else if (x2 == tauEstimate) {
            if (yinBuffer[tauEstimate] <= yinBuffer[x0]) tauEstimate.toFloat() else x0.toFloat()
        } else {
            val s0 = yinBuffer[x0]
            val s1 = yinBuffer[tauEstimate]
            val s2 = yinBuffer[x2]
            tauEstimate + (s2 - s0) / (2f * (2f * s1 - s2 - s0))
        }

        val pitch = if (betterTau > 0f) sampleRate.toFloat() / betterTau else 0f
        if (pitch < 60f || pitch > 1500f) {
            return PitchResult(0f, "--", 0, 0, false)
        }

        // Convert Hz to MIDI note
        // MIDI 69 is A4 (440 Hz)
        val midiNote = 69.0 + 12.0 * log2(pitch.toDouble() / 440.0)
        val roundedMidi = midiNote.roundToInt()
        val cents = ((midiNote - roundedMidi) * 100.0).roundToInt().coerceIn(-50, 50)

        val noteIndex = (roundedMidi % 12).let { if (it < 0) it + 12 else it }
        val octave = (roundedMidi / 12) - 1

        return PitchResult(
            pitchHz = pitch,
            noteName = NOTE_NAMES[noteIndex],
            octave = octave,
            centsOffset = cents,
            isConfident = true
        )
    }
}
