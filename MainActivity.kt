package com.example.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class MainActivity : ComponentActivity() {
    private val viewModel: TodoViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoApp(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoApp(viewModel: TodoViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var newTask by rememberSaveable { mutableStateOf("") }
    Scaffold(
        topBar = { SmallTopAppBar(title = { Text("To‑Do List") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (newTask.isNotBlank()) {
                    viewModel.addTask(newTask)
                    newTask = ""
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add task")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = newTask,
                onValueChange = { newTask = it },
                label = { Text("New task") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (newTask.isNotBlank()) {
                        viewModel.addTask(newTask)
                        newTask = ""
                    }
                })
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items(uiState.tasks, key = { it.id }) { task ->
                    TaskItem(task = task, onToggle = { viewModel.toggleDone(it) }, onDelete = { viewModel.deleteTask(it) })
                }
            }
        }
    }
}

@Composable
fun TaskItem(task: TodoTask, onToggle: (Long) -> Unit, onDelete: (Long) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Checkbox(
                checked = task.done,
                onCheckedChange = { onToggle(task.id) }
            )
            Text(
                text = task.title,
                style = if (task.done) MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.LineThrough) else MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        IconButton(onClick = { onDelete(task.id) }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        }
    }
}
