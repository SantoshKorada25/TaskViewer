package com.example.notes_room

import androidx.lifecycle.LiveData

class NoteRepository(private val noteDao : NoteDao) {
    val allNotes : LiveData<List<Note>> = noteDao.getAllNotes()
    suspend fun insert(note: Note){
        noteDao.insert(note)
    }
    suspend fun delete(note:Note){
        noteDao.delete(note)
    }
    suspend fun update(note:Note){
        noteDao.update(note)
    }
}