package com.xclub.feature.web.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xclub.core.data.db.entity.WebBookmarkEntity
import com.xclub.core.ui.component.XclubScaffold
import com.xclub.feature.web.viewmodel.WebBookmarkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebBookmarkListScreen(
    onBookmarkClick: (Long) -> Unit,
    viewModel: WebBookmarkViewModel = hiltViewModel()
) {
    val bookmarks by viewModel.bookmarks.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingBookmark by remember { mutableStateOf<WebBookmarkEntity?>(null) }

    XclubScaffold(
        title = { Text("网页") },
        actions = {
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "添加")
            }
        }
    ) { padding ->
        if (bookmarks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("点击右上角添加网页", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(vertical = 8.dp)) {
                items(bookmarks, key = { it.id }) { bookmark ->
                    BookmarkItem(bookmark = bookmark, onClick = { onBookmarkClick(bookmark.id) }, onLongClick = { editingBookmark = bookmark })
                }
            }
        }
    }

    if (showAddDialog) {
        WebBookmarkAddDialog(onDismiss = { showAddDialog = false }, onConfirm = { title, url, group -> viewModel.addBookmark(title, url, group); showAddDialog = false })
    }

    editingBookmark?.let { bookmark ->
        WebBookmarkAddDialog(
            initialBookmark = bookmark,
            onDismiss = { editingBookmark = null },
            onConfirm = { title, url, group -> viewModel.updateBookmark(bookmark.copy(title = title, url = url, group = group)); editingBookmark = null },
            onDelete = { viewModel.deleteBookmark(bookmark); editingBookmark = null }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BookmarkItem(bookmark: WebBookmarkEntity, onClick: () -> Unit, onLongClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).combinedClickable(onClick = onClick, onLongClick = onLongClick), shape = MaterialTheme.shapes.medium) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(bookmark.title, style = MaterialTheme.typography.titleMedium)
                Text(bookmark.url, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            }
            bookmark.group?.let { AssistChip(onClick = {}, label = { Text(it, style = MaterialTheme.typography.labelSmall) }) }
        }
    }
}
