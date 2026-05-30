package com.xclub.core.data.db.dao

import androidx.room.*
import com.xclub.core.data.db.entity.NoteLinkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteLinkDao {
    @Query("SELECT * FROM note_links WHERE sourceNoteId = :noteId")
    fun getOutgoingLinks(noteId: Long): Flow<List<NoteLinkEntity>>

    @Query("SELECT * FROM note_links WHERE targetNoteId = :noteId")
    fun getIncomingLinks(noteId: Long): Flow<List<NoteLinkEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(link: NoteLinkEntity)

    @Query("DELETE FROM note_links WHERE sourceNoteId = :noteId")
    suspend fun deleteBySource(noteId: Long)

    @Query("DELETE FROM note_links WHERE sourceNoteId = :sourceNoteId AND targetNoteId = :targetNoteId")
    suspend fun delete(sourceNoteId: Long, targetNoteId: Long)
}
