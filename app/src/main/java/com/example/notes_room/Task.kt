package com.example.notes_room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true) val id:Int = 0,
    val date : String,
    val category : String,
    val Description : String

)
