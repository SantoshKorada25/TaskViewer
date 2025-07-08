package com.example.notes_room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NoteDao {

    @Insert
    suspend fun insert(note: Note)

    @Query("SELECT * from note_table order by id Desc")
    fun getAllNotes(): LiveData<List<Note>>

    @Delete
    suspend fun delete(note:Note)

    @Update
    suspend fun update(note: Note)

}