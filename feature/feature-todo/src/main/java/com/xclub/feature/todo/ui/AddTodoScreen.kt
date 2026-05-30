package com.xclub.feature.todo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xclub.core.ui.component.XclubScaffold
import com.xclub.feature.todo.viewmodel.TodoViewModel
import java.time.LocalDate

@Composable
fun AddTodoScreen(onBack: () -> Unit, viewModel: TodoViewModel = hiltViewModel()) {
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("MEDIUM") }
    var hasDueDate by remember { mutableStateOf(false) }

    XclubScaffold(title = { Text("新建待办") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回") } }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("标题") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("备注") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)
            Text("优先级", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("HIGH" to "高", "MEDIUM" to "中", "LOW" to "低").forEach { (value, label) -> FilterChip(selected = priority == value, onClick = { priority = value }, label = { Text(label) }) }
            }
            Row(verticalAlignment = Alignment.CenterVertically) { Text("截止日期", style = MaterialTheme.typography.labelLarge); Spacer(modifier = Modifier.width(8.dp)); Switch(checked = hasDueDate, onCheckedChange = { hasDueDate = it }) }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = { viewModel.addTodo(title = title, note = note.ifBlank { null }, priority = priority, dueDate = if (hasDueDate) LocalDate.now() else null, repeatRule = null); onBack() }, modifier = Modifier.fillMaxWidth(), enabled = title.isNotBlank()) { Text("保存") }
        }
    }
}
