package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.SongLyric
import com.example.data.model.VocalTake
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [VocalTake::class, SongLyric::class], version = 1, exportSchema = false)
abstract class VocalSyncDatabase : RoomDatabase() {
    abstract fun vocalTakeDao(): VocalTakeDao
    abstract fun songLyricDao(): SongLyricDao

    companion object {
        @Volatile
        private var INSTANCE: VocalSyncDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): VocalSyncDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VocalSyncDatabase::class.java,
                    "vocalsync_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateInitialData(database.vocalTakeDao(), database.songLyricDao())
                    }
                }
            }

            private suspend fun populateInitialData(takeDao: VocalTakeDao, lyricDao: SongLyricDao) {
                // Seed takes matching screenshot
                takeDao.insertTake(
                    VocalTake(
                        id = 1,
                        title = "Raw Vocal Take",
                        filePath = "mock_take_1.wav",
                        durationSeconds = 49,
                        dateFormatted = "Oct 06, 2026",
                        timeFormatted = "14:06",
                        createdAt = 1791295560000L,
                        preset = "PRO: Studio Clean",
                        isFavorite = false,
                        fileSizeBytes = 17_400_000L // ~17.4 MB
                    )
                )
                takeDao.insertTake(
                    VocalTake(
                        id = 2,
                        title = "Raw Vocal Take",
                        filePath = "mock_take_2.wav",
                        durationSeconds = 23,
                        dateFormatted = "Oct 06, 2026",
                        timeFormatted = "14:00",
                        createdAt = 1791295200000L,
                        preset = "PRO: Soft Vocal",
                        isFavorite = true,
                        fileSizeBytes = 9_010_000L // ~9.01 MB (total ~26.41 MB matching screenshot!)
                    )
                )

                // Seed sample lyrics for teleprompter demonstration
                lyricDao.insertLyric(
                    SongLyric(
                        id = 1,
                        title = "Midnight Echoes",
                        artist = "VocalLyric Studio",
                        tempoBpm = 80,
                        isLrc = true,
                        content = """
                            [00:00.00] (Intro - 80 BPM)
                            [00:04.00] Step into the neon light
                            [00:08.50] Heartbeat racing through the night
                            [00:13.00] Every frequency we find
                            [00:17.50] Leaves the shadows far behind
                            [00:22.00] Sing with clarity and soul
                            [00:26.50] Studio sound to make you whole
                            [00:31.00] Catch the wave and hold the tone
                            [00:35.50] In this room you're not alone
                        """.trimIndent()
                    )
                )
            }
        }
    }
}
