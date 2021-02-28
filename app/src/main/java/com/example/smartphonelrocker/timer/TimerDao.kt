package com.example.smartphonelrocker.timer

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface TimerDao {
    @Query("select * from timers ORDER BY id")
    fun getAllTimer(): Flow<List<MyTimer>>

    @Query("select * from timers where id = :id")
    fun getTimer(id: Int): MyTimer

    @Query("SELECT * FROM timers ORDER BY id DESC LIMIT 1")
    fun getLastTimer(): Flow<MyTimer>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimer(timer: MyTimer): Long

    @Query("DELETE FROM timers where id = :id")
    suspend fun deleteTimer(id: Int): Int

    @Query("DELETE FROM timers")
    suspend fun deleteAll()
}