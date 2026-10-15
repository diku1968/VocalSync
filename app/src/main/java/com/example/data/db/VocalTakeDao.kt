package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.VocalTake
import kotlinx.coroutines.flow.Flow

@Dao
interface VocalTakeDao {
    @Query("SELECT * FROM vocal_takes ORDER BY createdAt DESC")
    fun getAllTakes(): Flow<List<VocalTake>>

    @Query("SELECT * FROM vocal_takes WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteTakes(): Flow<List<VocalTake>>

    @Query("SELECT * FROM vocal_takes ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentTakes(limit: Int = 5): Flow<List<VocalTake>>

    @Query("SELECT * FROM vocal_takes WHERE id = :id")
    suspend fun getTakeById(id: Long): VocalTake?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTake(take: VocalTake): Long

    @Update
    suspend fun updateTake(take: VocalTake)

    @Delete
    suspend fun deleteTake(take: VocalTake)

    @Query("DELETE FROM vocal_takes WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM vocal_takes")
    suspend fun clearAll()

    @Query("SELECT SUM(fileSizeBytes) FROM vocal_takes")
    fun getTotalSizeBytes(): Flow<Long?>
}
