package com.xclub.feature.finance.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xclub.core.data.db.entity.TransactionEntity
import com.xclub.core.ui.component.XclubScaffold
import com.xclub.core.ui.theme.XclubExpense
import com.xclub.core.ui.theme.XclubIncome
import com.xclub.feature.finance.viewmodel.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceOverviewScreen(onAddTransaction: () -> Unit, onSavingPlan: () -> Unit, onStats: () -> Unit, viewModel: FinanceViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    XclubScaffold(
        title = { state.cycleRange?.let { range -> Text("${range.start.monthValue}月${range.start.dayOfMonth}日 - ${range.end.monthValue}月${range.end.dayOfMonth}日") } ?: Text("记账") },
        navigationIcon = { IconButton(onClick = { viewModel.previousCycle() }) { Icon(Icons.Default.ChevronLeft, contentDescription = "上一周期") } },
        actions = {
            IconButton(onClick = { viewModel.nextCycle() }) { Icon(Icons.Default.ChevronRight, contentDescription = "下一周期") }
            IconButton(onClick = onSavingPlan) { Icon(Icons.Default.Savings, contentDescription = "存钱计划") }
            IconButton(onClick = onStats) { Icon(Icons.Default.BarChart, contentDescription = "统计") }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                CycleSummaryCard(income = state.totalIncome, expense = state.totalExpense)
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp)) {
                    items(state.transactions, key = { it.id }) { transaction -> TransactionItem(transaction = transaction) }
                }
            }
            FloatingActionButton(onClick = onAddTransaction, modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) {
                Icon(Icons.Default.Add, contentDescription = "记一笔")
            }
        }
    }
}

@Composable
private fun CycleSummaryCard(income: Long, expense: Long) {
    Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = MaterialTheme.shapes.medium) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("收入", style = MaterialTheme.typography.labelMedium, color = XclubIncome)
                Text(formatAmount(income), style = MaterialTheme.typography.titleLarge, fontFamily = FontFamily.Monospace, color = XclubIncome)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("支出", style = MaterialTheme.typography.labelMedium, color = XclubExpense)
                Text(formatAmount(expense), style = MaterialTheme.typography.titleLarge, fontFamily = FontFamily.Monospace, color = XclubExpense)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("结余", style = MaterialTheme.typography.labelMedium)
                Text(formatAmount(income - expense), style = MaterialTheme.typography.titleLarge, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
private fun TransactionItem(transaction: TransactionEntity) {
    val isExpense = transaction.type == "EXPENSE"
    ListItem(
        headlineContent = { Text(transaction.note ?: transaction.type) },
        trailingContent = { Text("${if (isExpense) "-" else "+"}${formatAmount(transaction.amount)}", style = MaterialTheme.typography.titleMedium, fontFamily = FontFamily.Monospace, color = if (isExpense) XclubExpense else XclubIncome) },
        supportingContent = { Text(transaction.date.toString()) }
    )
}

private fun formatAmount(amount: Long): String = String.format("%.2f", amount / 100.0)
