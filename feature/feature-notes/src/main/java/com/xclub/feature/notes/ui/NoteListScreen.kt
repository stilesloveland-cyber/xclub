package com.xclub.feature.notes.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xclub.core.data.db.entity.NoteEntity
import com.xclub.core.ui.component.XclubScaffold
import com.xclub.feature.notes.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(onNoteClick: (Long) -> Unit, onNewNote: () -> Unit, onGraph: () -> Unit, viewModel: NoteViewModel = hiltViewModel()) {
    val state by viewModel.noteListState.collectAsState()
    var showSearch by remember { mutableStateOf(false) }

    XclubScaffold(title = { Text("笔记") }, actions = {
        IconButton(onClick = { showSearch = !showSearch }) { Icon(Icons.Default.Search, contentDescription = "搜索") }
        IconButton(onClick = onGraph) { Icon(Icons.Default.Hub, contentDescription = "知识图谱") }
    }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (showSearch) { OutlinedTextField(value = state.searchQuery, onValueChange = { viewModel.search(it) }, label = { Text("搜索笔记") }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), singleLine = true) }
                if (state.notes.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("暂无笔记，点击 + 创建", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                } else {
                    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                        items(state.notes, key = { it.id }) { note -> NoteListItem(note = note, onClick = { onNoteClick(note.id) }, onLongClick = { viewModel.deleteNote(note) }) }
                    }
                }
            }
            FloatingActionButton(onClick = onNewNote, modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) { Icon(Icons.Default.Add, contentDescription = "新建笔记") }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteListItem(note: NoteEntity, onClick: () -> Unit, onLongClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).combinedClickable(onClick = onClick, onLongClick = onLongClick), shape = MaterialTheme.shapes.medium) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(note.title, style = MaterialTheme.typography.titleMedium)
            if (note.content.isNotBlank()) { Text(note.content.take(100), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2) }
            note.remark?.let { Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline) }
        }
    }
}
