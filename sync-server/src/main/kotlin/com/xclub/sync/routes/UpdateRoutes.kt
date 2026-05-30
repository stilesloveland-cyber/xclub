package com.xclub.sync.routes

import com.xclub.sync.model.UpdateInfo
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.updateRoutes() {
    route("/api/update") {
        get("/check") {
            val updatesDir = File("./data/updates")
            if (!updatesDir.exists() || !updatesDir.isDirectory) {
                call.respond(UpdateInfo(versionCode = 0, versionName = "", changelog = "", downloadUrl = ""))
                return@get
            }

            val latestApk = updatesDir.listFiles { f -> f.extension == "apk" }?.maxByOrNull { f.nameWithoutExtension.toLongOrNull() ?: 0 }
            if (latestApk == null) {
                call.respond(UpdateInfo(versionCode = 0, versionName = "", changelog = "", downloadUrl = ""))
                return@get
            }

            val versionCode = latestApk.nameWithoutExtension.toLongOrNull() ?: 0
            call.respond(UpdateInfo(
                versionCode = versionCode,
                versionName = "${versionCode / 100}.${versionCode % 100}",
                changelog = "",
                downloadUrl = "/api/update/download/$versionCode"
            ))
        }
    }
}
