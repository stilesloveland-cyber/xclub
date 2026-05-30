package com.xclub.feature.finance.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xclub.core.data.db.entity.CategoryEntity
import com.xclub.core.ui.component.XclubScaffold
import com.xclub.feature.finance.viewmodel.FinanceViewModel
import java.time.LocalDate

@Composable
fun AddTransactionScreen(onBack: () -> Unit, viewModel: FinanceViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    var isExpense by remember { mutableStateOf(true) }
    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var note by remember { mutableStateOf("") }
    val filteredCategories = state.categories.filter { it.type == if (isExpense) "EXPENSE" else "INCOME" }

    XclubScaffold(title = { Text("记一笔") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回") } }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = isExpense, onClick = { isExpense = true; selectedCategory = null }, label = { Text("支出") })
                FilterChip(selected = !isExpense, onClick = { isExpense = false; selectedCategory = null }, label = { Text("收入") })
            }
            Text(text = if (amountText.isEmpty()) "0.00" else formatAmount((amountText.toLongOrNull() ?: 0)), style = MaterialTheme.typography.displaySmall, fontFamily = FontFamily.Monospace, modifier = Modifier.fillMaxWidth())
            LazyVerticalGrid(columns = GridCells.Fixed(4), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredCategories) { category -> FilterChip(selected = selectedCategory?.id == category.id, onClick = { selectedCategory = category }, label = { Text(category.name) }) }
            }
            OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("备注") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = {
                val amount = amountText.toLongOrNull() ?: 0
                if (amount > 0 && selectedCategory != null) { viewModel.addTransaction(type = if (isExpense) "EXPENSE" else "INCOME", amount = amount, categoryId = selectedCategory!!.id, note = note.ifBlank { null }, date = LocalDate.now()); onBack() }
            }, modifier = Modifier.fillMaxWidth(), enabled = amountText.toLongOrNull()?.let { it > 0 } == true && selectedCategory != null) { Text("保存") }
        }
    }
}

private fun formatAmount(amount: Long): String = String.format("%.2f", amount / 100.0)
