package com.example.smartphonelrocker.timer

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class TimerViewModel(private val repository: TimerRepository) : ViewModel(){
    val allTimers: LiveData<List<Timer>> = repository.allTimers.asLiveData()

    fun insertTimer(timer: Timer) = viewModelScope.launch {
        repository.insertTimer(timer)
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