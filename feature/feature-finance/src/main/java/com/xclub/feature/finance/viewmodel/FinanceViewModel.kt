package com.xclub.feature.finance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xclub.core.data.db.dao.*
import com.xclub.core.data.db.entity.*
import com.xclub.feature.finance.util.BillingCycleCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

data class FinanceOverviewState(
    val cycleRange: BillingCycleCalculator.CycleRange? = null,
    val totalIncome: Long = 0,
    val totalExpense: Long = 0,
    val transactions: List<TransactionEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val billingCycle: BillingCycleEntity? = null
)

@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val billingCycleDao: BillingCycleDao
) : ViewModel() {

    private val _state = MutableStateFlow(FinanceOverviewState())
    val state: StateFlow<FinanceOverviewState> = _state.asStateFlow()
    private var currentReferenceDate = LocalDate.now()

    init { loadData() }

    private fun loadData() {
        viewModelScope.launch {
            billingCycleDao.getDefault().collectLatest { cycle ->
                val defaultCycle = cycle ?: BillingCycleEntity(mode = "NATURAL_MONTH", isDefault = true, startOffset = 0, endOffset = 0)
                val range = BillingCycleCalculator.calculate(defaultCycle, currentReferenceDate)
                transactionDao.getByDateRange(range.start, range.end).collectLatest { transactions ->
                    val income = transactionDao.sumByTypeAndDateRange("INCOME", range.start, range.end)
                    val expense = transactionDao.sumByTypeAndDateRange("EXPENSE", range.start, range.end)
                    _state.update { it.copy(cycleRange = range, totalIncome = income, totalExpense = expense, transactions = transactions, billingCycle = defaultCycle) }
                }
            }
        }
        viewModelScope.launch {
            categoryDao.getAll().collectLatest { categories -> _state.update { it.copy(categories = categories) } }
        }
    }

    fun addTransaction(type: String, amount: Long, categoryId: Long, note: String?, date: LocalDate) {
        viewModelScope.launch { transactionDao.insert(TransactionEntity(type = type, amount = amount, categoryId = categoryId, note = note, date = date, createdAt = Instant.now())) }
    }

    fun deleteTransaction(transaction: TransactionEntity) { viewModelScope.launch { transactionDao.delete(transaction) } }

    fun previousCycle() {
        val cycle = _state.value.billingCycle ?: return
        val range = _state.value.cycleRange ?: return
        currentReferenceDate = BillingCycleCalculator.previousRange(range, cycle).start
        loadData()
    }

    fun nextCycle() {
        val cycle = _state.value.billingCycle ?: return
        val range = _state.value.cycleRange ?: return
        currentReferenceDate = BillingCycleCalculator.nextRange(range, cycle).start
        loadData()
    }
}
