package com.example

import com.example.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSockets()
    configureSerialization()
    configureDatabases()
    configureSecurity()
    configureRouting()






    environment.log.info(
        "Application running on ${
            environment.config.propertyOrNull("ktor.deployment.host")?.getString()
        }:${environment.config.propertyOrNull("ktor.deployment.port")?.getString()}"
    )

}
