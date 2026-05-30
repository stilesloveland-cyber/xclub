package com.xclub.feature.todo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xclub.core.data.db.entity.TodoEntity
import com.xclub.core.ui.component.XclubScaffold
import com.xclub.feature.todo.viewmodel.TodoViewModel

@Composable
fun TodoListScreen(onAddTodo: () -> Unit, viewModel: TodoViewModel = hiltViewModel()) {
    val activeTodos by viewModel.activeTodos.collectAsState()
    val completedTodos by viewModel.completedTodos.collectAsState()

    XclubScaffold(title = { Text("工具") }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp)) {
                item { Text("待办", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) }
                items(activeTodos, key = { it.id }) { todo -> TodoItem(todo = todo, onToggle = { viewModel.toggleComplete(todo) }) }
                if (completedTodos.isNotEmpty()) {
                    item { Text("已完成", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) }
                    items(completedTodos, key = { it.id }) { todo -> TodoItem(todo = todo, onToggle = { viewModel.toggleComplete(todo) }) }
                }
            }
            FloatingActionButton(onClick = onAddTodo, modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) { Icon(Icons.Default.Add, contentDescription = "新建待办") }
        }
    }
}

@Composable
private fun TodoItem(todo: TodoEntity, onToggle: () -> Unit) {
    ListItem(
        headlineContent = { Text(todo.title, style = if (todo.isCompleted) MaterialTheme.typography.titleMedium.copy(textDecoration = TextDecoration.LineThrough) else MaterialTheme.typography.titleMedium) },
        supportingContent = { todo.dueDate?.let { Text(it.toString()) } },
        leadingContent = { Checkbox(checked = todo.isCompleted, onCheckedChange = { onToggle() }) },
        trailingContent = {
            val color = when (todo.priority) { "HIGH" -> MaterialTheme.colorScheme.error; "MEDIUM" -> MaterialTheme.colorScheme.tertiary; else -> MaterialTheme.colorScheme.outline }
            Surface(color = color, modifier = Modifier.size(8.dp), shape = MaterialTheme.shapes.extraSmall) {}
        }
    )
}
