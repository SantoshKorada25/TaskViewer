package com.example.notes_room
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
data class Note(
    @PrimaryKey(autoGenerate = true) val id:Int = 0,
    val title:String,
    val content:String
)
