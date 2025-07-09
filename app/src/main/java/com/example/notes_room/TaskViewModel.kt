package com.example.notes_room
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TaskViewModel(application : Application) : AndroidViewModel(application) {
    private val repository : TaskRepository
    val allTasks : LiveData<List<Task>>
    init {
        val taskDao = NoteDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        allTasks = repository.allTasks
    }
    fun insert(task: Task) = viewModelScope.launch {
        repository.insert(task)
    }
    fun delete(task:Task) = viewModelScope.launch {
        repository.delete(task)
    }
}