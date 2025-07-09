package com.example.notes_room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task:Task)

    @Query("select * from task_table order by id desc")
    fun getAllTasks(): LiveData<List<Task>>


    @Delete
    suspend fun delete(task:Task)
}