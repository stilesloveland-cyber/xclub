package com.xclub.feature.finance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xclub.core.data.db.dao.SavingPlanDao
import com.xclub.core.data.db.dao.SavingRecordDao
import com.xclub.core.data.db.entity.SavingPlanEntity
import com.xclub.core.data.db.entity.SavingRecordEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SavingPlanViewModel @Inject constructor(
    private val savingPlanDao: SavingPlanDao,
    private val savingRecordDao: SavingRecordDao
) : ViewModel() {

    val plans: StateFlow<List<SavingPlanEntity>> = savingPlanDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createPlan(name: String, targetAmount: Long) {
        viewModelScope.launch { savingPlanDao.insert(SavingPlanEntity(name = name, targetAmount = targetAmount, currentAmount = 0, createdAt = Instant.now())) }
    }

    fun deposit(planId: Long, amount: Long) {
        viewModelScope.launch {
            val plan = savingPlanDao.getById(planId) ?: return@launch
            savingPlanDao.update(plan.copy(currentAmount = plan.currentAmount + amount, updatedAt = Instant.now()))
            savingRecordDao.insert(SavingRecordEntity(planId = planId, amount = amount, isAuto = false, date = LocalDate.now(), createdAt = Instant.now()))
        }
    }
}
