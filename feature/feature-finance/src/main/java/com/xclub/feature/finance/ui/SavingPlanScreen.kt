package com.xclub.feature.finance.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xclub.core.data.db.entity.SavingPlanEntity
import com.xclub.core.ui.component.XclubScaffold
import com.xclub.core.ui.theme.XclubSaving
import com.xclub.feature.finance.viewmodel.SavingPlanViewModel

@Composable
fun SavingPlanScreen(onBack: () -> Unit, viewModel: SavingPlanViewModel = hiltViewModel()) {
    val plans by viewModel.plans.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    XclubScaffold(title = { Text("存钱计划") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回") } }, actions = { IconButton(onClick = { showDialog = true }) { Icon(Icons.Default.Add, contentDescription = "添加") } }) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(plans, key = { it.id }) { plan -> SavingPlanCard(plan = plan, onDeposit = { viewModel.deposit(plan.id, it) }) }
        }
    }

    if (showDialog) {
        var name by remember { mutableStateOf("") }
        var targetText by remember { mutableStateOf("") }
        AlertDialog(onDismissRequest = { showDialog = false }, title = { Text("新建存钱计划") }, text = { Column(verticalArrangement = Arrangement.spacedBy(12.dp)) { OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("名称") }); OutlinedTextField(value = targetText, onValueChange = { targetText = it }, label = { Text("目标金额（分）") }) } }, confirmButton = { TextButton(onClick = { viewModel.createPlan(name, targetText.toLongOrNull() ?: 0); showDialog = false }, enabled = name.isNotBlank() && targetText.toLongOrNull()?.let { it > 0 } == true) { Text("创建") } }, dismissButton = { TextButton(onClick = { showDialog = false }) { Text("取消") } })
    }
}

@Composable
private fun SavingPlanCard(plan: SavingPlanEntity, onDeposit: (Long) -> Unit) {
    val progress = if (plan.targetAmount > 0) plan.currentAmount.toFloat() / plan.targetAmount else 0f
    Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(plan.name, style = MaterialTheme.typography.titleMedium)
                Text("${formatAmount(plan.currentAmount)} / ${formatAmount(plan.targetAmount)}", style = MaterialTheme.typography.bodyMedium, fontFamily = FontFamily.Monospace, color = XclubSaving)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth(), color = XclubSaving)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) { TextButton(onClick = { onDeposit(10000) }) { Text("存入") } }
        }
    }
}

private fun formatAmount(amount: Long): String = String.format("%.2f", amount / 100.0)
