package com.xclub.feature.notes.navigation

object NotesRoute {
    const val list = "notes_list"
    const val edit = "notes_edit/{noteId}"
    const val graph = "notes_graph"
    fun edit(noteId: Long) = "notes_edit/$noteId"
    fun newNote() = "notes_edit/0"
}
