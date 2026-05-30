package com.xclub.feature.finance.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.xclub.feature.finance.ui.*

fun NavGraphBuilder.financeGraph(navController: NavController) {
    composable(FinanceRoute.overview) {
        FinanceOverviewScreen(
            onAddTransaction = { navController.navigate(FinanceRoute.addTransaction) },
            onSavingPlan = { navController.navigate(FinanceRoute.savingPlan) },
            onStats = { navController.navigate(FinanceRoute.stats) }
        )
    }
    composable(FinanceRoute.addTransaction) {
        AddTransactionScreen(onBack = { navController.popBackStack() })
    }
    composable(FinanceRoute.savingPlan) {
        SavingPlanScreen(onBack = { navController.popBackStack() })
    }
    composable(FinanceRoute.stats) {
        FinanceStatsScreen(onBack = { navController.popBackStack() })
    }
}
