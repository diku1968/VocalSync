package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vocal_takes")
data class VocalTake(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "Raw Vocal Take",
    val filePath: String,
    val durationSeconds: Int,
    val dateFormatted: String, // e.g. "Oct 06, 2026"
    val timeFormatted: String, // e.g. "14:06"
    val createdAt: Long = System.currentTimeMillis(),
    val preset: String = "PRO: Studio Clean",
    val isFavorite: Boolean = false,
    val fileSizeBytes: Long = 0L
) {
    val durationDisplay: String
        get() {
            val minutes = durationSeconds / 60
            val seconds = durationSeconds % 60
            return "%02d:%02d".format(minutes, seconds)
        }
}
