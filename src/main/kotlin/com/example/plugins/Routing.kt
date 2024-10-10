package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")

        }
        get("/1234") {
            call.respondText("Get on the dance floor!")

        }
        post("/login") {
            val body = call.receive<String>()
            call.respond(body)

        }
    }
}
