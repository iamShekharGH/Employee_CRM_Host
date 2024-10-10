package com.example.plugins

import com.example.model.EmployeeGender
import com.example.model.LoginRequest
import com.example.model.LoginResponse
import com.example.model.UserInformation
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

val jsonConfig = Json { encodeDefaults = true } // Extracted constant


fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")

        }
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

        post("/login") {
            call.handleLogin()
            /*val loginRequest = call.receive<LoginRequest>()
            val user = validateUser(loginRequest.username, loginRequest.password)

            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, "Invalid username or password")
                return@post
            }

            val token = generateToken(user.eid)
            val response = LoginResponse(token, user)

            val json = Json {
                encodeDefaults = true // Enables encoding of default values
            }

            call.respond(
                HttpStatusCode.OK,
                json.encodeToString(serializer = LoginResponse.serializer(), value = response)
            ) */// Encode to JSON string

//            call.respond(HttpStatusCode.OK, response.toString())
        }


    }
}

suspend fun ApplicationCall.handleLogin() {
    val loginRequest = receive<LoginRequest>()
    val user = validateUser(loginRequest.username, loginRequest.password)
    if (user == null) {
        respond(HttpStatusCode.Unauthorized, "Invalid username or password")
        return
    }
    val token = generateToken(user.eid)
    val response = LoginResponse(token, user)
    respond(HttpStatusCode.OK, jsonConfig.encodeToString(LoginResponse.serializer(), response))
}

fun validateUser(username: String, password: String): UserInformation? {
    // Replace with your actual user authentication logic
    // This example just returns some dummy data
    return if (username == "user" && password == "password") {
        UserInformation(
            eid = 1,
            name = "John Doe",
            title = "Software Engineer",
            email = "john.doe@example.com",
            age = 30,
            birthday = "1993-10-26",
            photoUrl = "https://example.com/profile.jpg",
            salary = 100000,
            employeeGender = EmployeeGender.MALE,
            presentToday = true,
            salaryCredited = false
        )
    } else {
        null
    }
}
