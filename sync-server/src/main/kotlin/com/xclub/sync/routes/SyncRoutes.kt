package com.xclub.sync.routes

import com.xclub.sync.database.DatabaseFactory
import com.xclub.sync.model.SyncRecord
import com.xclub.sync.model.SyncRequest
import com.xclub.sync.model.SyncResponse
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.syncRoutes() {
    authenticate("auth") {
        route("/api/sync") {
            post {
                val request = call.receive<SyncRequest>()
                val conn = DatabaseFactory.getConnection()

                conn.use {
                    for (record in request.records) {
                        it.createStatement().use { stmt ->
                            stmt.execute(
                                "INSERT INTO sync_records (module, record_id, action, data, updated_at, sync_version) " +
                                "VALUES ('${request.module}', ${record.id}, '${record.action}', '${record.data}', ${record.updatedAt}, ${record.syncVersion})"
                            )
                        }
                    }

                    val rs = it.createStatement().executeQuery(
                        "SELECT * FROM sync_records WHERE module = '${request.module}' AND sync_version > ${request.lastSyncVersion} ORDER BY sync_version ASC"
                    )

                    val results = mutableListOf<SyncRecord>()
                    while (rs.next()) {
                        results.add(SyncRecord(
                            id = rs.getLong("record_id"),
                            action = rs.getString("action"),
                            data = rs.getString("data"),
                            updatedAt = rs.getLong("updated_at"),
                            syncVersion = rs.getLong("sync_version")
                        ))
                    }

                    val maxVersion = it.createStatement().executeQuery("SELECT MAX(sync_version) FROM sync_records").let { rs2 ->
                        if (rs2.next()) rs2.getLong(1) else request.lastSyncVersion
                    }

                    call.respond(SyncResponse(currentVersion = maxVersion, records = results))
                }
            }
        }
    }
}
