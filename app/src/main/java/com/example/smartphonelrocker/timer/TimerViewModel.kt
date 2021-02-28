package com.example.smartphonelrocker.timer

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TimerViewModel(private val repository: TimerRepository) : ViewModel() {
    val allTimers: LiveData<List<MyTimer>> = repository.allTimers.asLiveData()
    val lastTimer: LiveData<MyTimer>? = repository.lastTimer?.asLiveData()

    suspend fun insertTimer(myTimer: MyTimer) =
        withContext(viewModelScope.coroutineContext) {
            repository.insertTimer(myTimer)
        }

    suspend fun deleteTimer(id: Int) =
        withContext(viewModelScope.coroutineContext) {
            repository.deleteTimer(id)
        }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }
}

class TimerViewModelFactory(private val repository: TimerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}