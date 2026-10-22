package com.example.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TodoViewModel : ViewModel() {
    private val repository = TodoRepository()
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init {
        viewModelScope.launch {
            repository.getAllTasks().collect { tasks ->
                _uiState.update { it.copy(tasks = tasks) }
            }
        }
    }

    fun addTask(title: String) {
        viewModelScope.launch { repository.insertTask(TodoTask(title = title)) }
    }

    fun toggleDone(id: Long) {
        viewModelScope.launch { repository.toggleDone(id) }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch { repository.deleteTask(id) }
    }
}

data class UiState(val tasks: List<TodoTask> = emptyList())
