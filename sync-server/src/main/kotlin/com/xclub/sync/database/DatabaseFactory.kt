package com.xclub.sync.database

import java.sql.Connection
import java.sql.DriverManager

object DatabaseFactory {
    private const val DB_URL = "jdbc:sqlite:./data/xclub_sync.db"

    fun init() {
        DriverManager.getConnection(DB_URL).use { conn ->
            conn.createStatement().use { stmt ->
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS sync_records (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        module TEXT NOT NULL,
                        record_id INTEGER NOT NULL,
                        action TEXT NOT NULL,
                        data TEXT NOT NULL,
                        updated_at INTEGER NOT NULL,
                        sync_version INTEGER NOT NULL
                    )
                """)
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_sync_module_version ON sync_records(module, sync_version)")
            }
        }
    }

    fun getConnection(): Connection = DriverManager.getConnection(DB_URL)
}
