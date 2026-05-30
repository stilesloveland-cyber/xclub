package com.xclub.feature.web.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xclub.core.data.db.dao.WebBookmarkDao
import com.xclub.core.data.db.entity.WebBookmarkEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class WebBookmarkViewModel @Inject constructor(
    private val webBookmarkDao: WebBookmarkDao
) : ViewModel() {

    val bookmarks: StateFlow<List<WebBookmarkEntity>> = webBookmarkDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addBookmark(title: String, url: String, group: String?) {
        viewModelScope.launch {
            webBookmarkDao.insert(WebBookmarkEntity(title = title, url = url, group = group, createdAt = Instant.now(), updatedAt = Instant.now()))
        }
    }

    fun deleteBookmark(bookmark: WebBookmarkEntity) {
        viewModelScope.launch { webBookmarkDao.delete(bookmark) }
    }

    fun updateBookmark(bookmark: WebBookmarkEntity) {
        viewModelScope.launch { webBookmarkDao.update(bookmark.copy(updatedAt = Instant.now())) }
    }
}
