package com.xclub.feature.notes.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.xclub.feature.notes.ui.KnowledgeGraphScreen
import com.xclub.feature.notes.ui.NoteEditScreen
import com.xclub.feature.notes.ui.NoteListScreen

fun NavGraphBuilder.notesGraph(navController: NavController) {
    composable(NotesRoute.list) {
        NoteListScreen(
            onNoteClick = { noteId -> navController.navigate(NotesRoute.edit(noteId)) },
            onNewNote = { navController.navigate(NotesRoute.newNote()) },
            onGraph = { navController.navigate(NotesRoute.graph) }
        )
    }
    composable(
        route = NotesRoute.edit,
        arguments = listOf(navArgument("noteId") { type = androidx.navigation.NavType.LongType })
    ) { backStackEntry ->
        val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0
        NoteEditScreen(noteId = noteId, onBack = { navController.popBackStack() }, onNavigateToNote = { id -> navController.navigate(NotesRoute.edit(id)) })
    }
    composable(NotesRoute.graph) {
        KnowledgeGraphScreen(onBack = { navController.popBackStack() })
    }
}
