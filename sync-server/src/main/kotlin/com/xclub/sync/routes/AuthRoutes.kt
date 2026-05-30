package com.xclub.sync.routes

import com.xclub.sync.model.AuthRequest
import com.xclub.sync.model.AuthResponse
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.authRoutes() {
    route("/api/auth") {
        post {
            val request = call.receive<AuthRequest>()
            when (request.action) {
                "register" -> {
                    val token = UUID.randomUUID().toString()
                    call.respond(AuthResponse(token = token, status = "ok"))
                }
                "verify" -> {
                    call.respond(AuthResponse(token = request.token ?: "", status = "ok"))
                }
                else -> call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid action"))
            }
        }
    }
}
