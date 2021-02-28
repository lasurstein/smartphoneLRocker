package com.example.smartphonelrocker.timer

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class TimerRepository(private val timerDao: TimerDao) {
    val allTimers: Flow<List<MyTimer>> = timerDao.getAllTimer()
    val lastTimer: Flow<MyTimer>? = timerDao.getLastTimer()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertTimer(myTimer: MyTimer): Long {
        return timerDao.insertTimer(myTimer)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteTimer(id: Int): Int {
        return timerDao.deleteTimer(id)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAll() {
        return timerDao.deleteAll()
    }
}