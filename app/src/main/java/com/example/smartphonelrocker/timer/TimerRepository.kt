package com.example.smartphonelrocker.timer

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class TimerRepository(private val timerDao: TimerDao) {
    val allTimers: Flow<List<Timer>> = timerDao.getAllTimer()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertTimer(timer: Timer) {
        timerDao.insertTimer(timer)
    }
}