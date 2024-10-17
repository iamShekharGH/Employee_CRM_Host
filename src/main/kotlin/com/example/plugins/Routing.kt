package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.http.content.staticResources
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


fun Application.configureRouting() {
    routing {
        staticResources("/", "static")


        get("/1234") {
            call.respondText("Get on the dance floor!")

        }
        post("/reqisres") {
            val body = call.receive<String>()
            call.respond(body)

        }

        get("/protected-route") {
            authenticate {
                // Access protected resources here
                CoroutineScope(parentCoroutineContext).launch {
                    call.respondText("This is a protected route.")
                }
            }
        }
    }
}


