package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.model.UserInformation
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

import java.util.*
import java.util.concurrent.TimeUnit

const val SECRET_KEY = "iEzWyfBDXTzYv8jPM2vY5cew"

fun Application.configureSecurity() {
    // Please read the jwt property from the config file if you are using EngineMain
    val jwtAudience = "jwt-audience"
    val jwtDomain = "https://jwt-provider-domain/"
    val jwtRealm = "ktor sample app"
    val jwtSecret = "secret"
    authentication {
        jwt {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
}

// Utility function for generating JWT tokens
fun generateToken(userId: Int): String {

    val jwtSecret = SECRET_KEY
    val jwtDomain = "https://127.0.0.1:8080/"

    val token = JWT.create()
        .withSubject("user:$userId")
        .withIssuer(jwtDomain)
        .withClaim("userInformation", "user") // Add any additional claims here
        .withExpiresAt(Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(15)))
        .sign(Algorithm.HMAC256(jwtSecret))
    return token
}

suspend fun generateToken(user: UserInformation): String = coroutineScope {

    val jwtSecret = SECRET_KEY
    val jwtDomain = "https://127.0.0.1:8080/"

    val token = launch {
        JWT.create()
            .withSubject(user.eid.toString())
            .withIssuer(jwtDomain)
            .withClaim(
                "userInformation",
                Json.encodeToString(serializer = UserInformation.serializer(), value = user)
            )


            .withExpiresAt(Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(15)))
            .sign(Algorithm.HMAC256(jwtSecret))
    }
    token.join().toString()
}
