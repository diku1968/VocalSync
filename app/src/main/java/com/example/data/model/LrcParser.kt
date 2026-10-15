package com.example.data.model

import java.util.regex.Pattern

data class TimedLyricLine(
    val timestampMs: Long,
    val text: String
)

object LrcParser {
    private val TIME_TAG_PATTERN = Pattern.compile("\\[(\\d{2}):(\\d{2})\\.?(\\d{2,3})?\\]")

    fun parse(rawText: String): List<TimedLyricLine> {
        val lines = rawText.lines()
        val result = mutableListOf<TimedLyricLine>()

        var fallbackTimeMs = 0L

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue

            val matcher = TIME_TAG_PATTERN.matcher(trimmed)
            if (matcher.find()) {
                val min = matcher.group(1)?.toLongOrNull() ?: 0L
                val sec = matcher.group(2)?.toLongOrNull() ?: 0L
                val msString = matcher.group(3) ?: "00"
                val ms = if (msString.length == 2) msString.toLong() * 10 else msString.toLong()
                val totalMs = (min * 60 + sec) * 1000 + ms

                val cleanText = trimmed.replace(TIME_TAG_PATTERN.toRegex(), "").trim()
                if (cleanText.isNotEmpty()) {
                    result.add(TimedLyricLine(totalMs, cleanText))
                }
            } else {
                // Untimed line: spacing ~4 seconds apart for auto teleprompting
                result.add(TimedLyricLine(fallbackTimeMs, trimmed))
                fallbackTimeMs += 4000L
            }
        }

        return result.sortedBy { it.timestampMs }
    }
}
