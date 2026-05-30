package com.xclub.feature.finance.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xclub.core.ui.component.XclubScaffold

@Composable
fun FinanceStatsScreen(onBack: () -> Unit) {
    XclubScaffold(title = { Text("统计") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回") } }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), contentAlignment = Alignment.Center) { Text("统计图表（后续迭代完善）", style = MaterialTheme.typography.bodyLarge) }
    }
}
