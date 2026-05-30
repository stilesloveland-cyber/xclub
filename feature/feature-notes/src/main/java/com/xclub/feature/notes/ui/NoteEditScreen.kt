package com.xclub.feature.notes.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xclub.core.data.db.entity.NoteEntity
import com.xclub.core.ui.component.XclubScaffold
import com.xclub.feature.notes.viewmodel.NoteViewModel

@Composable
fun NoteEditScreen(noteId: Long, onBack: () -> Unit, onNavigateToNote: (Long) -> Unit, viewModel: NoteViewModel = hiltViewModel()) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var remark by remember { mutableStateOf("") }
    var isPreview by remember { mutableStateOf(false) }
    var backLinks by remember { mutableStateOf<List<NoteEntity>>(emptyList()) }
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(noteId) {
        if (noteId != 0L) {
            val note = viewModel.getNoteById(noteId)
            if (note != null) { title = note.title; content = note.content; remark = note.remark ?: ""; backLinks = viewModel.getBackLinks(noteId) }
        }
        loaded = true
    }

    XclubScaffold(title = { Text(if (noteId == 0L) "新建笔记" else "编辑笔记") }, navigationIcon = { IconButton(onClick = { if (loaded) viewModel.saveNote(noteId, title, content, remark.ifBlank { null }); onBack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回") } }, actions = { IconButton(onClick = { isPreview = !isPreview }) { Icon(Icons.Default.Visibility, contentDescription = if (isPreview) "编辑" else "预览") } }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("标题") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(modifier = Modifier.height(8.dp))
            if (isPreview) { Text(content, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f)) } else { OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("正文（Markdown，使用 [[笔记名]] 创建链接）") }, modifier = Modifier.weight(1f).fillMaxWidth()) }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = remark, onValueChange = { remark = it }, label = { Text("备注") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)
            if (backLinks.isNotEmpty()) { Spacer(modifier = Modifier.height(16.dp)); Text("反向链接", style = MaterialTheme.typography.labelLarge); backLinks.forEach { link -> TextButton(onClick = { onNavigateToNote(link.id) }) { Text(link.title) } } }
        }
    }
}
