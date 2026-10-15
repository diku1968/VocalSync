package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "song_lyrics")
data class SongLyric(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val artist: String = "Original Artist",
    val content: String,
    val tempoBpm: Int = 80,
    val isLrc: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
