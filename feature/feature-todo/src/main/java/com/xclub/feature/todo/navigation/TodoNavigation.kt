package com.xclub.feature.todo.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.xclub.feature.todo.ui.AddTodoScreen
import com.xclub.feature.todo.ui.TodoListScreen

fun NavGraphBuilder.todoGraph(navController: NavController) {
    composable(TodoRoute.list) {
        TodoListScreen(onAddTodo = { navController.navigate(TodoRoute.add) })
    }
    composable(TodoRoute.add) {
        AddTodoScreen(onBack = { navController.popBackStack() })
    }
}
