package com.xclub.feature.notes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xclub.core.data.db.dao.NoteDao
import com.xclub.core.data.db.dao.NoteLinkDao
import com.xclub.core.data.db.dao.NoteTagDao
import com.xclub.core.data.db.entity.*
import com.xclub.feature.notes.util.NoteLinkParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

data class NoteListState(val notes: List<NoteEntity> = emptyList(), val searchQuery: String = "")

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteDao: NoteDao,
    private val noteTagDao: NoteTagDao,
    private val noteLinkDao: NoteLinkDao
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val noteListState: StateFlow<NoteListState> = _searchQuery
        .flatMapLatest { query -> if (query.isBlank()) noteDao.getAll() else noteDao.search(query) }
        .combine(_searchQuery) { notes, query -> NoteListState(notes, query) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NoteListState())

    fun search(query: String) { _searchQuery.value = query }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch { noteLinkDao.deleteBySource(note.id); noteDao.delete(note) }
    }

    suspend fun getNoteById(id: Long): NoteEntity? = noteDao.getById(id)

    suspend fun getBackLinks(noteId: Long): List<NoteEntity> {
        val links = noteLinkDao.getIncomingLinks(noteId).first()
        return links.mapNotNull { noteDao.getById(it.sourceNoteId) }
    }

    fun saveNote(id: Long, title: String, content: String, remark: String?) {
        viewModelScope.launch {
            val now = Instant.now()
            if (id == 0L) {
                val newId = noteDao.insert(NoteEntity(title = title, content = content, remark = remark, createdAt = now, updatedAt = now))
                updateLinks(newId, content)
            } else {
                noteDao.update(NoteEntity(id = id, title = title, content = content, remark = remark, createdAt = now, updatedAt = now))
                updateLinks(id, content)
            }
        }
    }

    private suspend fun updateLinks(noteId: Long, content: String) {
        noteLinkDao.deleteBySource(noteId)
        val linkNames = NoteLinkParser.extractLinks(content)
        for (name in linkNames) {
            val target = noteDao.getByTitle(name) ?: continue
            noteLinkDao.insert(NoteLinkEntity(sourceNoteId = noteId, targetNoteId = target.id))
        }
    }
}
