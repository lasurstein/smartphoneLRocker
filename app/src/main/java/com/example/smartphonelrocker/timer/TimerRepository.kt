package com.example.smartphonelrocker.timer

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

class TimerRepository(private val timerDao: TimerDao) {
    val allTimers: Flow<List<MyTimer>> = timerDao.getAllTimer()
    val lastTimer: Flow<MyTimer>? = timerDao.getLastTimer()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertTimer(myTimer: MyTimer) {
        return timerDao.insertTimer(myTimer)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateTimer(myTimer: MyTimer) {
        return timerDao.updateTimer(myTimer)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteTimer(id: Int) {
        return timerDao.deleteTimer(id)
    }
}