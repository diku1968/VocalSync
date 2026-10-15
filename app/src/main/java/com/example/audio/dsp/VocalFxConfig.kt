package com.example.audio.dsp

data class VocalFxConfig(
    val presetName: String = "Studio Clean",

    // 1. Dolby Digital 3D Spatial Audio
    val isDolbySpatialEnabled: Boolean = true,
    val spatialWidth: Float = 0.65f, // 0.0 (Mono) to 1.0 (Super Wide 3D)
    val spatialSurroundDepth: Float = 0.50f,

    // 2. 5-Band Parametric Equalizer (-12 dB to +12 dB)
    val eqLowGainDb: Float = 1.5f,     // 100 Hz Sub & Warmth
    val eqMidLowGainDb: Float = -1.0f,  // 500 Hz Mud Cut
    val eqMidGainDb: Float = 2.0f,     // 1.5 kHz Core Vocal Presence
    val eqHighMidGainDb: Float = 2.5f, // 4.0 kHz Definition & Clarity
    val eqHighAirGainDb: Float = 3.5f, // 10.0 kHz Studio Air Sheen

    // 3. Studio Reverb
    val isReverbEnabled: Boolean = true,
    val reverbPreset: String = "Concert Hall", // "Studio Room", "Concert Hall", "Plate Reverb", "Cathedral Air"
    val reverbDecaySeconds: Float = 2.2f,
    val reverbWetDry: Float = 0.28f, // 0.0 (dry) to 1.0 (wet)

    // 4. Vocal Dynamics Compressor
    val isCompressorEnabled: Boolean = true,
    val compressionThresholdDb: Float = -18f,
    val compressionRatio: Float = 4.0f,
    val makeupGainDb: Float = 2.5f,

    // 5. Pitch Correction & Auto-Tune
    val isAutoTuneEnabled: Boolean = false,
    val autoTuneSpeed: String = "Smooth Pop", // "Hard Trap", "Smooth Pop", "Subtle Natural"
    val scaleKey: String = "Chromatic",

    // 6. De-Esser (Sibilance Control)
    val isDeEsserEnabled: Boolean = true,
    val deEsserAmount: Float = 0.40f,

    // 7. Warm Tube Saturation
    val isSaturationEnabled: Boolean = true,
    val saturationDrive: Float = 0.20f
) {
    companion object {
        val PRESET_DOLBY_DIGITAL = VocalFxConfig(
            presetName = "Dolby Digital 3D",
            isDolbySpatialEnabled = true,
            spatialWidth = 0.85f,
            spatialSurroundDepth = 0.70f,
            eqLowGainDb = 2.5f,
            eqMidLowGainDb = -0.5f,
            eqMidGainDb = 2.0f,
            eqHighMidGainDb = 3.0f,
            eqHighAirGainDb = 4.5f,
            isReverbEnabled = true,
            reverbPreset = "Concert Hall",
            reverbDecaySeconds = 2.8f,
            reverbWetDry = 0.32f,
            isCompressorEnabled = true,
            compressionThresholdDb = -16f,
            compressionRatio = 3.5f,
            makeupGainDb = 3.0f,
            isDeEsserEnabled = true,
            deEsserAmount = 0.45f,
            isSaturationEnabled = true,
            saturationDrive = 0.25f
        )

        val PRESET_STUDIO_CLEAN = VocalFxConfig(
            presetName = "Studio Clean",
            isDolbySpatialEnabled = true,
            spatialWidth = 0.40f,
            spatialSurroundDepth = 0.30f,
            eqLowGainDb = 0.0f,
            eqMidLowGainDb = -1.5f,
            eqMidGainDb = 1.5f,
            eqHighMidGainDb = 2.0f,
            eqHighAirGainDb = 2.5f,
            isReverbEnabled = true,
            reverbPreset = "Studio Room",
            reverbDecaySeconds = 1.2f,
            reverbWetDry = 0.18f,
            isCompressorEnabled = true,
            compressionThresholdDb = -20f,
            compressionRatio = 3.0f,
            makeupGainDb = 2.0f,
            isDeEsserEnabled = true,
            deEsserAmount = 0.35f,
            isSaturationEnabled = false
        )

        val PRESET_POP_AIR = VocalFxConfig(
            presetName = "Pop Vocal Air",
            isDolbySpatialEnabled = true,
            spatialWidth = 0.70f,
            spatialSurroundDepth = 0.50f,
            eqLowGainDb = -1.0f,
            eqMidLowGainDb = -2.0f,
            eqMidGainDb = 2.5f,
            eqHighMidGainDb = 4.0f,
            eqHighAirGainDb = 6.0f,
            isReverbEnabled = true,
            reverbPreset = "Plate Reverb",
            reverbDecaySeconds = 2.0f,
            reverbWetDry = 0.30f,
            isCompressorEnabled = true,
            compressionThresholdDb = -15f,
            compressionRatio = 4.5f,
            makeupGainDb = 3.5f,
            isDeEsserEnabled = true,
            deEsserAmount = 0.55f,
            isSaturationEnabled = true,
            saturationDrive = 0.15f
        )

        val PRESET_CATHEDRAL = VocalFxConfig(
            presetName = "Cathedral Air",
            isDolbySpatialEnabled = true,
            spatialWidth = 0.95f,
            spatialSurroundDepth = 0.85f,
            eqLowGainDb = 1.0f,
            eqMidLowGainDb = 0.5f,
            eqMidGainDb = 1.0f,
            eqHighMidGainDb = 2.0f,
            eqHighAirGainDb = 3.5f,
            isReverbEnabled = true,
            reverbPreset = "Cathedral Air",
            reverbDecaySeconds = 4.5f,
            reverbWetDry = 0.50f,
            isCompressorEnabled = true,
            compressionThresholdDb = -18f,
            compressionRatio = 2.5f,
            makeupGainDb = 2.0f,
            isDeEsserEnabled = true,
            deEsserAmount = 0.30f,
            isSaturationEnabled = true,
            saturationDrive = 0.10f
        )

        val PRESET_TRAP_AUTOTUNE = VocalFxConfig(
            presetName = "Modern Auto-Tune",
            isDolbySpatialEnabled = true,
            spatialWidth = 0.60f,
            spatialSurroundDepth = 0.40f,
            eqLowGainDb = -2.0f,
            eqMidLowGainDb = -1.0f,
            eqMidGainDb = 3.0f,
            eqHighMidGainDb = 4.5f,
            eqHighAirGainDb = 5.0f,
            isReverbEnabled = true,
            reverbPreset = "Plate Reverb",
            reverbDecaySeconds = 1.8f,
            reverbWetDry = 0.35f,
            isCompressorEnabled = true,
            compressionThresholdDb = -14f,
            compressionRatio = 6.0f,
            makeupGainDb = 4.0f,
            isAutoTuneEnabled = true,
            autoTuneSpeed = "Hard Trap",
            scaleKey = "Chromatic",
            isDeEsserEnabled = true,
            deEsserAmount = 0.50f,
            isSaturationEnabled = true,
            saturationDrive = 0.30f
        )
    }
}
