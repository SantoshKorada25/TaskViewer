package com.example.notes_room
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import com.example.notes_room.ui.theme.TaskViewerTheme

import androidx.compose.material3.*
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    private lateinit var noteViewModel: NoteViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]
        enableEdgeToEdge()
        setContent {
            TaskViewerTheme {
                    NotesApp(
                        noteViewModel
                    )

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesApp(noteViewModel: NoteViewModel) {
    val notes by noteViewModel.allNotes.observeAsState(emptyList())
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Notes", color =Color.White , fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.White)
                    }
                    DropdownMenu(
                        modifier = Modifier
                            .padding(10.dp)
                            .border(
                                width = 1.dp,
                                color = Color.Gray,
                                shape = RoundedCornerShape(4.dp)
                            ),
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Add Note")
                                }
                            },
                            onClick = {
                                showAddNoteDialog = true
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Task")
                                }
                            },
                            onClick = {
                                expanded = false
                                context.startActivity(Intent(context, AddTaskActivity::class.java))
                            }
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        NotesList(
            notes = notes,
            noteViewModel = noteViewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }

    if (showAddNoteDialog) {
         AddNoteDialog(
            onDismiss = { showAddNoteDialog = false },
            onSave = { title, content ->
                noteViewModel.insert(Note(title = title, content = content))
                showAddNoteDialog = false
            }
        )
    }
    if(expanded){
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.8f)))
    }
}

@Composable
fun CustomDropdownItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    //tint = Color.Black
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .padding(4.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
            }
        },
        onClick = onClick
    )
}


@Composable
fun NotesList(
    notes: List<Note>,
    noteViewModel: NoteViewModel,
    modifier: Modifier = Modifier
) {
    var noteToEdit by remember { mutableStateOf<Note?>(null) }
    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(notes) { note ->
            NoteItem(
                note = note,
                onDelete = { noteViewModel.delete(note) },
                onEdit = {
                    noteToEdit = note
                }
            )
        }
    }
    if (noteToEdit != null){
        EditNoteDialog(note = noteToEdit!!, onDismiss = {noteToEdit=null}, onSave = { updatedNote ->
            noteViewModel.update(updatedNote)
            noteToEdit = null
        })
    }
}



@Composable
fun AddNoteDialog(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Note") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onSave(title.trim(), content.trim())
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


//@Composable
//fun NotesApp(noteViewModel : NoteViewModel){
//    val notes by noteViewModel.allNotes.observeAsState(emptyList())
//    var title by remember { mutableStateOf("") }
//    var content by remember { mutableStateOf("") }
//    var noteToEdit by remember { mutableStateOf<Note?>(null) }
//
//
//    Column (modifier = Modifier.fillMaxSize().padding(16.dp)){
//        Text(text = "Add Note" , style = MaterialTheme.typography.h6)
//        OutlinedTextField(
//            value = title,
//            onValueChange = { title = it },
//            label = { Text("Title") },
//            modifier = Modifier.fillMaxWidth()
//        )
//        OutlinedTextField(
//            value = content,
//            onValueChange = {content = it},
//            label = {Text("Content")},
//            modifier = Modifier.fillMaxWidth()
//        )
//        Button(onClick = {
//            if(title.isNotBlank() && content.isNotBlank()){
//                noteViewModel.insert(Note(title= title, content = content))
//                title = " "
//                content = " "
//            }
//        },
//        modifier = Modifier.padding(top = 8.dp)
//        ) {
//            Text("Save Note")
//        }
//        Divider(modifier = Modifier.padding(vertical = 8.dp))
//        Text(text = "All Notes", style = MaterialTheme.typography.h6)
//        LazyColumn {
//            items(notes) {
//                note -> NoteItem(
//                    note = note,
//                onDelete = {
//                       noteViewModel.delete(note)
//                },
//                onEdit = {
//                    noteToEdit = note
//                }
//                )
//            }
//        }
//        if (noteToEdit != null) {
//            EditNoteDialog(
//                note = noteToEdit!!,
//                onDismiss = { noteToEdit = null },
//                onSave = { updatedNote ->
//                    noteViewModel.update(updatedNote)
//                    noteToEdit = null
//                }
//            )
//        }
//
//    }
//}
@Composable
fun EditNoteDialog(
    note: Note,
    onDismiss: () -> Unit,
    onSave: (Note) -> Unit
) {
    var title by remember { mutableStateOf(note.title) }
    var content by remember { mutableStateOf(note.content) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Note") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Content") })
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(note.copy(title = title, content = content))
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun NoteItem (note:Note , onDelete:() -> Unit , onEdit:() -> Unit){
    Card (modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = 4.dp){
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.DarkGray
                )
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Column(modifier = Modifier.weight(0.1f)) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                Divider(modifier = Modifier.padding(4.dp))
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }


            }

        }
    }
}


