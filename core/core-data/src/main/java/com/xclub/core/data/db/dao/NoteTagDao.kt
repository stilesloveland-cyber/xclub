package com.xclub.core.data.db.dao

import androidx.room.*
import com.xclub.core.data.db.entity.NoteTagEntity
import com.xclub.core.data.db.entity.NoteTagRelationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteTagDao {
    @Query("SELECT * FROM note_tags ORDER BY name ASC")
    fun getAll(): Flow<List<NoteTagEntity>>

    @Query("SELECT * FROM note_tags WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): NoteTagEntity?

    @Insert
    suspend fun insert(tag: NoteTagEntity): Long

    @Delete
    suspend fun delete(tag: NoteTagEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRelation(relation: NoteTagRelationEntity)

    @Delete
    suspend fun deleteRelation(relation: NoteTagRelationEntity)

    @Query("SELECT tagId FROM note_tag_relations WHERE noteId = :noteId")
    suspend fun getTagIdsForNote(noteId: Long): List<Long>

    @Query("SELECT noteId FROM note_tag_relations WHERE tagId = :tagId")
    fun getNoteIdsForTag(tagId: Long): Flow<List<Long>>
}
