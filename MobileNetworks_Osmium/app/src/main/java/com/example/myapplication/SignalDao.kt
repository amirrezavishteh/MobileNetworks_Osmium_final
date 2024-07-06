package com.example.osmium.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.myapplication.Signal
import kotlinx.coroutines.flow.Flow

@Dao
interface SignalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(item: List<Signal>)

    @Query("DELETE FROM signal")
    suspend fun clearAll()

    @Query("SELECT * FROM signal")
    fun getSignals(): Flow<List<Signal>>

    @Transaction
    suspend fun replaceAll(item: List<Signal>) {
        clearAll()
        insertAll(item)
    }
}
