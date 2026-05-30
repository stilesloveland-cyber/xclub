package com.xclub.core.common.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateTimeUtils {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val displayFormatter = DateTimeFormatter.ofPattern("M月d日")

    fun formatDate(date: LocalDate): String = date.format(displayFormatter)
    fun parseDate(text: String): LocalDate = LocalDate.parse(text, dateFormatter)
    fun now(): Instant = Instant.now()
    fun today(): LocalDate = LocalDate.now()
    fun LocalDate.toTimestamp(): Long = atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
}
