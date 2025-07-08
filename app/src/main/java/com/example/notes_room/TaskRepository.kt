package com.example.notes_room

import androidx.lifecycle.LiveData

class TaskRepository(private val taskDao : TaskDao) {
    val allTasks : LiveData<List<Task>> = taskDao.getAllTasks()
    suspend fun insert(task:Task){
        taskDao.insert(task)
    }
}