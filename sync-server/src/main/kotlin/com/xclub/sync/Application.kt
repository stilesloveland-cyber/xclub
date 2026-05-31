package com.xclub.sync

import com.xclub.sync.database.DatabaseFactory
import com.xclub.sync.routes.authRoutes
import com.xclub.sync.routes.syncRoutes
import com.xclub.sync.routes.updateRoutes
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 5555
    embeddedServer(Netty, port = port) {
        install(ContentNegotiation) { json(Json { prettyPrint = true; isLenient = true; ignoreUnknownKeys = true }) }
        install(CORS) { anyHost(); allowHeader(HttpHeaders.ContentType); allowMethod(HttpMethod.Put); allowMethod(HttpMethod.Delete) }
        install(StatusPages) { exception<Throwable> { call, _ -> call.respondText("Internal Server Error", status = HttpStatusCode.InternalServerError) } }
        install(Authentication) { bearer("auth") { authenticate { credential -> if (credential.token.isNotBlank()) UserIdPrincipal(credential.token) else null } } }
        DatabaseFactory.init()
        routing {
            get("/") { call.respondText("Xclub Sync Server") }
            get("/health") { call.respondText("ok") }
            authRoutes()
            syncRoutes()
            updateRoutes()
        }
    }.start(wait = true)
}
