package com.xclub.feature.finance.util

import com.xclub.core.data.db.entity.BillingCycleEntity
import java.time.LocalDate
import java.time.YearMonth

object BillingCycleCalculator {
    data class CycleRange(val start: LocalDate, val end: LocalDate)

    fun calculate(cycle: BillingCycleEntity, referenceDate: LocalDate = LocalDate.now()): CycleRange {
        return when (cycle.mode) {
            "NATURAL_MONTH" -> calculateNaturalMonth(cycle, referenceDate)
            "PAYDAY" -> calculatePayday(cycle, referenceDate)
            else -> calculateNaturalMonth(cycle, referenceDate)
        }
    }

    private fun calculateNaturalMonth(cycle: BillingCycleEntity, referenceDate: LocalDate): CycleRange {
        val yearMonth = YearMonth.from(referenceDate)
        val start = yearMonth.atDay(1).plusDays(cycle.startOffset.toLong())
        val end = yearMonth.atEndOfMonth().plusDays(cycle.endOffset.toLong())
        return CycleRange(start, end)
    }

    private fun calculatePayday(cycle: BillingCycleEntity, referenceDate: LocalDate): CycleRange {
        val payday = cycle.payday ?: 1
        val yearMonth = YearMonth.from(referenceDate)
        val cycleStart = yearMonth.atDay(payday.coerceAtMost(yearMonth.lengthOfMonth())).plusDays(cycle.startOffset.toLong())
        val nextMonth = yearMonth.plusMonths(1)
        val cycleEnd = nextMonth.atDay(payday.coerceAtMost(nextMonth.lengthOfMonth())).minusDays(1).plusDays(cycle.endOffset.toLong())
        if (referenceDate < cycleStart) {
            val prevMonth = yearMonth.minusMonths(1)
            val prevStart = prevMonth.atDay(payday.coerceAtMost(prevMonth.lengthOfMonth())).plusDays(cycle.startOffset.toLong())
            val prevEnd = yearMonth.atDay(payday.coerceAtMost(yearMonth.lengthOfMonth())).minusDays(1).plusDays(cycle.endOffset.toLong())
            return CycleRange(prevStart, prevEnd)
        }
        return CycleRange(cycleStart, cycleEnd)
    }

    fun previousRange(current: CycleRange, cycle: BillingCycleEntity): CycleRange {
        val daysInRange = current.start.until(current.end).days.toLong() + 1
        val prevStart = current.start.minusDays(daysInRange)
        return calculate(cycle, prevStart)
    }

    fun nextRange(current: CycleRange, cycle: BillingCycleEntity): CycleRange {
        val nextStart = current.end.plusDays(1)
        return calculate(cycle, nextStart)
    }
}
