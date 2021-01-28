package com.example.smartphonelrocker.timer

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerDao {
    @Query("select * from timers ORDER BY id")
    fun getAllTimer(): Flow<List<Timer>>

    @Query("select * from timers where id = :id")
    fun getTimer(id: Int): Timer

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimer(vararg timer: Timer)

    @Delete
    suspend fun deleteTimer(vararg timer: Timer)

    @Query("DELETE FROM timers")
    suspend fun deleteAll()
}