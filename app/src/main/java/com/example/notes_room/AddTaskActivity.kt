package com.example.notes_room


import android.app.DatePickerDialog
import android.os.Build
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date

class AddTaskActivity : ComponentActivity() {
    private lateinit var taskViewModel : TaskViewModel
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        setContent {
            MaterialTheme {
                    AddtaskScreen(taskViewModel)
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddtaskScreen(taskViewModel: TaskViewModel){
    var showDialog   by remember { mutableStateOf(false) }
    var expanded   by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val tasks by taskViewModel.allTasks.observeAsState(emptyList())


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Tasks") },
                actions = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        modifier = Modifier
                            .padding(10.dp)
                            .border(10.dp, Color.Gray, shape = RoundedCornerShape(4.dp)),
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        CustomDropdownItem(
                            icon = Icons.Default.Add,
                            text = "Add Tasks",
                            onClick = {
                                showDialog = true

                            }
                        )
                    }
                }
            )
        }
    ){ innerPadding ->
        TaskList(tasks,taskViewModel, modifier = Modifier.padding(innerPadding))
    }
    if (showDialog){
            AddTaskDialog(categoryOptions = listOf("Work", "Personal", "Fitness", "Study"),onDismiss = {showDialog=false}, onSave = {date,category,description-> taskViewModel.insert(Task(0,date,category,description))})
    }
}

@Composable
fun TaskList(tasksList:List<Task>,taskViewModel: TaskViewModel,modifier: Modifier = Modifier){
    LazyColumn (modifier.padding(16.dp)){
        items(tasksList) {
            task -> TaskItem(task = task, onDelete = {taskViewModel.delete(task)} , onEdit = {})
        }
    }
}

@Composable
fun TaskItem (task:Task , onDelete:() -> Unit , onEdit:() -> Unit){
    Card (modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = 4.dp){
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                androidx.compose.material.Text(
                    text = task.category,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.DarkGray
                )
                androidx.compose.material.Text(
                    text = task.date,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Column(modifier = Modifier.weight(0.1f)) {
                androidx.compose.material.IconButton(onClick = onEdit) {
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTaskDialog(
    categoryOptions: List<String>,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var dateCreated by remember { mutableStateOf(LocalDate.now()) }
    var taskDialogExpanded by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                dateCreated = LocalDate.of(year, month + 1, day)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (content.isNotBlank() && selectedCategory.isNotBlank()) {
                        onSave(dateCreated.toString(), selectedCategory, content.trim())
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
        },
        title = { Text("Add Task") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)

            ) {
                OutlinedTextField(
                    value = dateCreated.toString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date") },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Pick date")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Task description") },
                    placeholder = { Text("e.g. Buy groceries") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Select a category", style = MaterialTheme.typography.bodyMedium)

                ExposedDropdownMenuBox(
                    expanded = taskDialogExpanded,
                    onExpandedChange = { taskDialogExpanded = !taskDialogExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = taskDialogExpanded)
                        },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = taskDialogExpanded,
                        onDismissRequest = { taskDialogExpanded = false },
                        modifier = Modifier
                            .heightIn(max = 200.dp)
                    ) {
                        categoryOptions.forEach { category ->
                            androidx.compose.material3.DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    taskDialogExpanded = false
                                }
                            )
                        }
                    }
                }

                if (selectedCategory.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Selected: $selectedCategory", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    )
}
