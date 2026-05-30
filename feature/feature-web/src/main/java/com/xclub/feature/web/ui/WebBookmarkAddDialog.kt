package com.xclub.feature.web.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xclub.core.data.db.entity.WebBookmarkEntity

@Composable
fun WebBookmarkAddDialog(
    initialBookmark: WebBookmarkEntity? = null,
    onDismiss: () -> Unit,
    onConfirm: (title: String, url: String, group: String?) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var title by remember { mutableStateOf(initialBookmark?.title ?: "") }
    var url by remember { mutableStateOf(initialBookmark?.url ?: "") }
    var group by remember { mutableStateOf(initialBookmark?.group ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialBookmark != null) "编辑网页" else "添加网页") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("标题") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("网址") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = group, onValueChange = { group = it }, label = { Text("分组（可选）") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(title, url, group.ifBlank { null }) }, enabled = title.isNotBlank() && url.isNotBlank()) { Text("确定") } },
        dismissButton = {
            Row {
                onDelete?.let { TextButton(onClick = it, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("删除") }; Spacer(modifier = Modifier.width(8.dp)) }
                TextButton(onClick = onDismiss) { Text("取消") }
            }
        }
    )
}
