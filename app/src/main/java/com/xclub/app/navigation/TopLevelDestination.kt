package com.xclub.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.ui.graphics.vector.ImageVector
import com.xclub.feature.web.navigation.WebRoute
import com.xclub.feature.finance.navigation.FinanceRoute
import com.xclub.feature.notes.navigation.NotesRoute
import com.xclub.feature.todo.navigation.TodoRoute

enum class TopLevelDestination(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    WEB(route = WebRoute.list, icon = Icons.Default.Language, label = "网页"),
    FINANCE(route = FinanceRoute.overview, icon = Icons.Default.AccountBalanceWallet, label = "记账"),
    NOTES(route = NotesRoute.list, icon = Icons.Default.NoteAlt, label = "笔记"),
    TODO(route = TodoRoute.list, icon = Icons.Default.CheckCircle, label = "工具")
}
