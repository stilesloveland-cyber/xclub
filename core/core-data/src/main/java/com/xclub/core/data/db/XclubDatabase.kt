package com.xclub.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.xclub.core.data.db.converter.Converters
import com.xclub.core.data.db.dao.*
import com.xclub.core.data.db.entity.*

@Database(
    entities = [
        WebBookmarkEntity::class,
        TransactionEntity::class,
        CategoryEntity::class,
        SavingPlanEntity::class,
        SavingRecordEntity::class,
        BillingCycleEntity::class,
        NoteEntity::class,
        NoteTagEntity::class,
        NoteTagRelationEntity::class,
        NoteLinkEntity::class,
        TodoEntity::class,
        ReminderEntity::class,
        UserConfigEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class XclubDatabase : RoomDatabase() {
    abstract fun webBookmarkDao(): WebBookmarkDao
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun savingPlanDao(): SavingPlanDao
    abstract fun savingRecordDao(): SavingRecordDao
    abstract fun billingCycleDao(): BillingCycleDao
    abstract fun noteDao(): NoteDao
    abstract fun noteTagDao(): NoteTagDao
    abstract fun noteLinkDao(): NoteLinkDao
    abstract fun todoDao(): TodoDao
    abstract fun reminderDao(): ReminderDao
    abstract fun userConfigDao(): UserConfigDao
}
