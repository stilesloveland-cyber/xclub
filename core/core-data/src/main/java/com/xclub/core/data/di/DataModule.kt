package com.xclub.core.data.di

import android.content.Context
import androidx.room.Room
import com.xclub.core.data.db.XclubDatabase
import com.xclub.core.data.db.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import java.util.UUID
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): XclubDatabase {
        val passphrase = getOrCreatePassphrase(context)
        val factory = SupportOpenHelperFactory(passphrase)

        return Room.databaseBuilder(
            context,
            XclubDatabase::class.java,
            "xclub.db"
        )
            .openHelperFactory(factory)
            .fallbackToDestructiveMigrationFrom()
            .build()
    }

    private fun getOrCreatePassphrase(context: Context): ByteArray {
        val prefs = context.getSharedPreferences("xclub_secure", Context.MODE_PRIVATE)
        val existing = prefs.getString("db_key", null)
        if (existing != null) {
            return existing.toByteArray(Charsets.UTF_8)
        }
        val key = UUID.randomUUID().toString()
        prefs.edit().putString("db_key", key).apply()
        return key.toByteArray(Charsets.UTF_8)
    }

    @Provides fun provideWebBookmarkDao(db: XclubDatabase): WebBookmarkDao = db.webBookmarkDao()
    @Provides fun provideTransactionDao(db: XclubDatabase): TransactionDao = db.transactionDao()
    @Provides fun provideCategoryDao(db: XclubDatabase): CategoryDao = db.categoryDao()
    @Provides fun provideSavingPlanDao(db: XclubDatabase): SavingPlanDao = db.savingPlanDao()
    @Provides fun provideSavingRecordDao(db: XclubDatabase): SavingRecordDao = db.savingRecordDao()
    @Provides fun provideBillingCycleDao(db: XclubDatabase): BillingCycleDao = db.billingCycleDao()
    @Provides fun provideNoteDao(db: XclubDatabase): NoteDao = db.noteDao()
    @Provides fun provideNoteTagDao(db: XclubDatabase): NoteTagDao = db.noteTagDao()
    @Provides fun provideNoteLinkDao(db: XclubDatabase): NoteLinkDao = db.noteLinkDao()
    @Provides fun provideTodoDao(db: XclubDatabase): TodoDao = db.todoDao()
    @Provides fun provideReminderDao(db: XclubDatabase): ReminderDao = db.reminderDao()
    @Provides fun provideUserConfigDao(db: XclubDatabase): UserConfigDao = db.userConfigDao()
}
