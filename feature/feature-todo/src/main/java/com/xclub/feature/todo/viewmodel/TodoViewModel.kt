package com.xclub.feature.todo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xclub.core.data.db.dao.TodoDao
import com.xclub.core.data.db.entity.TodoEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(private val todoDao: TodoDao) : ViewModel() {

    val activeTodos: StateFlow<List<TodoEntity>> = todoDao.getActive().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val completedTodos: StateFlow<List<TodoEntity>> = todoDao.getCompleted().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTodo(title: String, note: String?, priority: String, dueDate: LocalDate?, repeatRule: String?) {
        viewModelScope.launch { todoDao.insert(TodoEntity(title = title, note = note, priority = priority, dueDate = dueDate, repeatRule = repeatRule, createdAt = Instant.now())) }
    }

    fun toggleComplete(todo: TodoEntity) { viewModelScope.launch { todoDao.update(todo.copy(isCompleted = !todo.isCompleted, updatedAt = Instant.now())) } }

    fun deleteTodo(todo: TodoEntity) { viewModelScope.launch { todoDao.delete(todo) } }
}
