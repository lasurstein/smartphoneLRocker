package com.example.smartphonelrocker.timer

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class TimerViewModel(private val repository: TimerRepository) : ViewModel() {
    val allTimers: LiveData<List<MyTimer>> = repository.allTimers.asLiveData()
    val lastTimer: LiveData<MyTimer>? = repository.lastTimer?.asLiveData()

    fun insertTimer(myTimer: MyTimer) = viewModelScope.launch {
        repository.insertTimer(myTimer)
    }

    fun updateTimer(myTimer: MyTimer) = viewModelScope.launch {
        repository.updateTimer(myTimer)
    }

    fun deleteTimer(id: Int) = viewModelScope.launch {
        repository.deleteTimer(id)
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