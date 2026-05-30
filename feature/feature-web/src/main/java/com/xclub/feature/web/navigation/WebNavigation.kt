package com.xclub.feature.web.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.xclub.feature.web.ui.WebBookmarkListScreen
import com.xclub.feature.web.ui.WebViewScreen

fun NavGraphBuilder.webGraph(navController: NavController) {
    composable(WebRoute.list) {
        WebBookmarkListScreen(
            onBookmarkClick = { bookmarkId -> navController.navigate(WebRoute.view(bookmarkId)) }
        )
    }
    composable(
        route = WebRoute.view,
        arguments = listOf(navArgument("bookmarkId") { type = androidx.navigation.NavType.LongType })
    ) { backStackEntry ->
        val bookmarkId = backStackEntry.arguments?.getLong("bookmarkId") ?: return@composable
        WebViewScreen(bookmarkId = bookmarkId, onBack = { navController.popBackStack() })
    }
}
