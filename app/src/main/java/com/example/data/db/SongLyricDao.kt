package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.SongLyric
import kotlinx.coroutines.flow.Flow

@Dao
interface SongLyricDao {
    @Query("SELECT * FROM song_lyrics ORDER BY createdAt DESC")
    fun getAllLyrics(): Flow<List<SongLyric>>

    @Query("SELECT * FROM song_lyrics WHERE id = :id")
    suspend fun getLyricById(id: Long): SongLyric?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLyric(lyric: SongLyric): Long

    @Update
    suspend fun updateLyric(lyric: SongLyric)

    @Delete
    suspend fun deleteLyric(lyric: SongLyric)

    @Query("DELETE FROM song_lyrics WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM song_lyrics")
    suspend fun clearAll()
}
