package com.xclub.feature.notes.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.xclub.core.ui.component.XclubScaffold

@Composable
fun KnowledgeGraphScreen(onBack: () -> Unit) {
    XclubScaffold(title = { Text("知识图谱") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回") } }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text("知识图谱可视化（后续迭代完善）", style = MaterialTheme.typography.bodyLarge) }
    }
}
