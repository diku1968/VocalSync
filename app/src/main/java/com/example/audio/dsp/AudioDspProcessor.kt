package com.example.audio.dsp

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tanh

object AudioDspProcessor {

    /**
     * Applies the complete vocal DSP chain (EQ, Compression, Saturation, Reverb, Dolby 3D)
     * to a buffer of mono 16-bit PCM samples.
     * Returns a stereo 16-bit PCM buffer (interleaved L, R) if Dolby Spatial is enabled,
     * or mono if spatial is off.
     */
    fun processVocal(input: ShortArray, sampleRate: Int, config: VocalFxConfig): ShortArray {
        if (input.isEmpty()) return input

        val floatSamples = FloatArray(input.size) { i ->
            input[i].toFloat() / 32768.0f
        }

        // 1. Equalizer (5-Band Biquad Filters)
        apply5BandEq(floatSamples, sampleRate, config)

        // 2. Vocal Dynamics Compressor
        if (config.isCompressorEnabled) {
            applyCompressor(floatSamples, config)
        }

        // 3. De-Esser (high frequency softening)
        if (config.isDeEsserEnabled) {
            applyDeEsser(floatSamples, sampleRate, config.deEsserAmount)
        }

        // 4. Analog Tube Saturation
        if (config.isSaturationEnabled) {
            applySaturation(floatSamples, config.saturationDrive)
        }

        // 5. Reverb
        val reverbWet = if (config.isReverbEnabled) {
            generateReverb(floatSamples, sampleRate, config)
        } else null

        // 6. Dolby Digital 3D Spatial Audio & Stereo Widening
        return if (config.isDolbySpatialEnabled) {
            generateDolbySpatialStereo(floatSamples, reverbWet, config, sampleRate)
        } else {
            // Mono mix
            val output = ShortArray(floatSamples.size)
            val dryMix = 1.0f - (if (config.isReverbEnabled) config.reverbWetDry else 0f)
            val wetMix = if (config.isReverbEnabled) config.reverbWetDry else 0f

            for (i in floatSamples.indices) {
                var s = floatSamples[i] * dryMix
                if (reverbWet != null && i < reverbWet.size) {
                    s += reverbWet[i] * wetMix
                }
                val sampleInt = (s * 32767.0f).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                output[i] = sampleInt.toShort()
            }
            output
        }
    }

    private fun apply5BandEq(samples: FloatArray, sampleRate: Int, config: VocalFxConfig) {
        // 100 Hz Low Shelf
        if (config.eqLowGainDb != 0f) {
            val filter = BiquadFilter.lowShelf(sampleRate, 100.0, config.eqLowGainDb.toDouble())
            filter.process(samples)
        }
        // 500 Hz Peak
        if (config.eqMidLowGainDb != 0f) {
            val filter = BiquadFilter.peaking(sampleRate, 500.0, 1.0, config.eqMidLowGainDb.toDouble())
            filter.process(samples)
        }
        // 1.5 kHz Peak
        if (config.eqMidGainDb != 0f) {
            val filter = BiquadFilter.peaking(sampleRate, 1500.0, 1.2, config.eqMidGainDb.toDouble())
            filter.process(samples)
        }
        // 4.0 kHz Peak
        if (config.eqHighMidGainDb != 0f) {
            val filter = BiquadFilter.peaking(sampleRate, 4000.0, 1.2, config.eqHighMidGainDb.toDouble())
            filter.process(samples)
        }
        // 10.0 kHz High Shelf (Air)
        if (config.eqHighAirGainDb != 0f) {
            val filter = BiquadFilter.highShelf(sampleRate, 10000.0, config.eqHighAirGainDb.toDouble())
            filter.process(samples)
        }
    }

    private fun applyCompressor(samples: FloatArray, config: VocalFxConfig) {
        val threshLinear = 10.0.pow(config.compressionThresholdDb / 20.0).toFloat()
        val ratio = config.compressionRatio
        val makeupLinear = 10.0.pow(config.makeupGainDb / 20.0).toFloat()

        var envelope = 0.0f
        val attackCoeff = 0.05f
        val releaseCoeff = 0.005f

        for (i in samples.indices) {
            val absSample = kotlin.math.abs(samples[i])
            envelope += if (absSample > envelope) {
                attackCoeff * (absSample - envelope)
            } else {
                releaseCoeff * (absSample - envelope)
            }

            var gain = 1.0f
            if (envelope > threshLinear) {
                // Compression in dB
                val envDb = 20.0 * kotlin.math.log10(envelope.coerceAtLeast(1e-6f).toDouble())
                val threshDb = config.compressionThresholdDb.toDouble()
                val compressedDb = threshDb + (envDb - threshDb) / ratio
                gain = 10.0.pow((compressedDb - envDb) / 20.0).toFloat()
            }

            samples[i] = (samples[i] * gain * makeupLinear).coerceIn(-1.0f, 1.0f)
        }
    }

    private fun applyDeEsser(samples: FloatArray, sampleRate: Int, amount: Float) {
        val bandpass = BiquadFilter.bandpass(sampleRate, 7000.0, 2.0)
        val sibilanceCopy = samples.clone()
        bandpass.process(sibilanceCopy)

        for (i in samples.indices) {
            val sibLevel = kotlin.math.abs(sibilanceCopy[i])
            if (sibLevel > 0.15f) {
                val reduction = 1.0f - (amount * (sibLevel - 0.15f) * 2.5f).coerceIn(0f, 0.7f)
                samples[i] *= reduction
            }
        }
    }

    private fun applySaturation(samples: FloatArray, drive: Float) {
        val mult = 1.0f + drive * 3.0f
        for (i in samples.indices) {
            val x = samples[i] * mult
            samples[i] = tanh(x.toDouble()).toFloat() / tanh(mult.toDouble()).toFloat()
        }
    }

    private fun generateReverb(samples: FloatArray, sampleRate: Int, config: VocalFxConfig): FloatArray {
        // Multi-comb filter reverb model
        val delays = intArrayOf(
            (0.0297 * sampleRate).toInt(),
            (0.0371 * sampleRate).toInt(),
            (0.0411 * sampleRate).toInt(),
            (0.0437 * sampleRate).toInt()
        )
        val feedback = (0.75f + (config.reverbDecaySeconds / 10.0f)).coerceIn(0.6f, 0.94f)

        val outReverb = FloatArray(samples.size)
        val combBuffers = delays.map { FloatArray(it) }
        val combIndices = IntArray(delays.size)

        for (i in samples.indices) {
            var sum = 0.0f
            for (c in delays.indices) {
                val buf = combBuffers[c]
                val idx = combIndices[c]
                val delayed = buf[idx]
                buf[idx] = samples[i] + delayed * feedback
                combIndices[c] = (idx + 1) % delays[c]
                sum += delayed
            }
            outReverb[i] = sum * 0.25f
        }
        return outReverb
    }

    private fun generateDolbySpatialStereo(
        dryMono: FloatArray,
        reverbWet: FloatArray?,
        config: VocalFxConfig,
        sampleRate: Int
    ): ShortArray {
        // Interleaved stereo output: [L0, R0, L1, R1, ...]
        val stereoOutput = ShortArray(dryMono.size * 2)
        val delaySamples = ((0.014 * sampleRate) * config.spatialWidth).toInt().coerceAtLeast(1)
        val delayBuffer = FloatArray(delaySamples)
        var delayIdx = 0

        val width = config.spatialWidth
        val depth = config.spatialSurroundDepth
        val wetMix = if (config.isReverbEnabled) config.reverbWetDry else 0f
        val dryMix = 1.0f - (wetMix * 0.5f)

        for (i in dryMono.indices) {
            var mono = dryMono[i] * dryMix
            if (reverbWet != null && i < reverbWet.size) {
                mono += reverbWet[i] * wetMix
            }

            // Haas micro-delay for side spatial spread
            val delayed = delayBuffer[delayIdx]
            delayBuffer[delayIdx] = mono
            delayIdx = (delayIdx + 1) % delaySamples

            // Mid/Side matrix
            val mid = mono
            val side = (mono - delayed) * width * 1.4f

            // Left = Mid + Side; Right = Mid - Side * phase
            val left = (mid + side * (1.0f + depth * 0.3f)).coerceIn(-1.0f, 1.0f)
            val right = (mid - side * (1.0f - depth * 0.15f)).coerceIn(-1.0f, 1.0f)

            stereoOutput[i * 2] = (left * 32767.0f).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            stereoOutput[i * 2 + 1] = (right * 32767.0f).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        return stereoOutput
    }

    private class BiquadFilter(
        var a0: Double, var a1: Double, var a2: Double,
        var b0: Double, var b1: Double, var b2: Double
    ) {
        var x1 = 0.0
        var x2 = 0.0
        var y1 = 0.0
        var y2 = 0.0

        fun process(samples: FloatArray) {
            for (i in samples.indices) {
                val x0 = samples[i].toDouble()
                val y0 = (b0 / a0) * x0 + (b1 / a0) * x1 + (b2 / a0) * x2 - (a1 / a0) * y1 - (a2 / a0) * y2
                x2 = x1
                x1 = x0
                y2 = y1
                y1 = y0
                samples[i] = y0.toFloat().coerceIn(-1.0f, 1.0f)
            }
        }

        companion object {
            fun peaking(sampleRate: Int, freq: Double, q: Double, gainDb: Double): BiquadFilter {
                val w0 = 2.0 * PI * freq / sampleRate
                val alpha = sin(w0) / (2.0 * q)
                val a = 10.0.pow(gainDb / 40.0)

                val b0 = 1.0 + alpha * a
                val b1 = -2.0 * cos(w0)
                val b2 = 1.0 - alpha * a
                val a0 = 1.0 + alpha / a
                val a1 = -2.0 * cos(w0)
                val a2 = 1.0 - alpha / a

                return BiquadFilter(a0, a1, a2, b0, b1, b2)
            }

            fun lowShelf(sampleRate: Int, freq: Double, gainDb: Double): BiquadFilter {
                val w0 = 2.0 * PI * freq / sampleRate
                val a = 10.0.pow(gainDb / 40.0)
                val alpha = sin(w0) / 2.0 * sqrt(2.0)

                val b0 = a * ((a + 1.0) - (a - 1.0) * cos(w0) + 2.0 * sqrt(a) * alpha)
                val b1 = 2.0 * a * ((a - 1.0) - (a + 1.0) * cos(w0))
                val b2 = a * ((a + 1.0) - (a - 1.0) * cos(w0) - 2.0 * sqrt(a) * alpha)
                val a0 = (a + 1.0) + (a - 1.0) * cos(w0) + 2.0 * sqrt(a) * alpha
                val a1 = -2.0 * ((a - 1.0) + (a + 1.0) * cos(w0))
                val a2 = (a + 1.0) + (a - 1.0) * cos(w0) - 2.0 * sqrt(a) * alpha

                return BiquadFilter(a0, a1, a2, b0, b1, b2)
            }

            fun highShelf(sampleRate: Int, freq: Double, gainDb: Double): BiquadFilter {
                val w0 = 2.0 * PI * freq / sampleRate
                val a = 10.0.pow(gainDb / 40.0)
                val alpha = sin(w0) / 2.0 * sqrt(2.0)

                val b0 = a * ((a + 1.0) + (a - 1.0) * cos(w0) + 2.0 * sqrt(a) * alpha)
                val b1 = -2.0 * a * ((a - 1.0) + (a + 1.0) * cos(w0))
                val b2 = a * ((a + 1.0) + (a - 1.0) * cos(w0) - 2.0 * sqrt(a) * alpha)
                val a0 = (a + 1.0) - (a - 1.0) * cos(w0) + 2.0 * sqrt(a) * alpha
                val a1 = 2.0 * ((a - 1.0) - (a + 1.0) * cos(w0))
                val a2 = (a + 1.0) - (a - 1.0) * cos(w0) - 2.0 * sqrt(a) * alpha

                return BiquadFilter(a0, a1, a2, b0, b1, b2)
            }

            fun bandpass(sampleRate: Int, freq: Double, q: Double): BiquadFilter {
                val w0 = 2.0 * PI * freq / sampleRate
                val alpha = sin(w0) / (2.0 * q)

                val b0 = alpha
                val b1 = 0.0
                val b2 = -alpha
                val a0 = 1.0 + alpha
                val a1 = -2.0 * cos(w0)
                val a2 = 1.0 - alpha

                return BiquadFilter(a0, a1, a2, b0, b1, b2)
            }
        }
    }
}
