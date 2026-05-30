package com.xclub.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.xclub.feature.finance.navigation.financeGraph
import com.xclub.feature.notes.navigation.notesGraph
import com.xclub.feature.todo.navigation.todoGraph
import com.xclub.feature.web.navigation.webGraph

@Composable
fun XclubNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = TopLevelDestination.WEB.route,
        modifier = modifier
    ) {
        webGraph(navController)
        financeGraph(navController)
        notesGraph(navController)
        todoGraph(navController)
    }
}
