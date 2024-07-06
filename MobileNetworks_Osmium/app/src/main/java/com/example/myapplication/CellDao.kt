package com.example.osmium.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.myapplication.Cell
import kotlinx.coroutines.flow.Flow

@Dao
interface CellDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(item: List<Cell>)

    @Query("DELETE FROM cell")
    suspend fun clearAll()

    @Query("SELECT * FROM cell")
    fun getCells(): Flow<List<Cell>>

    @Transaction
    suspend fun replaceAll(item: List<Cell>) {
        clearAll()
        insertAll(item)
    }
}
