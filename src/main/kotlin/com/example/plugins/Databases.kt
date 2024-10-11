package com.example.plugins

import com.example.model.Login
import com.example.model.Success
import com.example.model.UserInformation
import com.example.plugins.tables.City
import com.example.plugins.tables.CityService
import com.example.plugins.tables.EmployeeService
import com.example.plugins.tables.LoginService
import com.example.plugins.tables.UserInfoService
import handleLogin
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import jsonConfig
import kotlinx.coroutines.*
import kotlinx.serialization.builtins.ListSerializer
import java.sql.*

fun Application.configureDatabases() {
    val dbConnection: Connection = connectToPostgres(embedded = false)
    val cityService = CityService(dbConnection)
    val userInfoService = UserInfoService(dbConnection)
    val loginService = LoginService(dbConnection)
    val employeeService = EmployeeService(dbConnection)

    routing {

        get("/test") {
            loginService.checkTableStatus()
            call.respondText("Hello World!")
        }

        get("/generateLoginUsers") {
            val res = loginService.insertRandomLogins()
            call.respond(HttpStatusCode.OK, res)
        }
        get("/showLoginUsers") {
            val res = loginService.getAllUsers()
            if (res.isEmpty()) {
                call.respond(
                    HttpStatusCode.NotFound,
                    jsonConfig.encodeToString(
                        ListSerializer(Login.serializer()),
                        listOf()
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.OK,
                    Success(
                        HttpStatusCode.OK.value,
                        jsonConfig.encodeToString(
                            ListSerializer(Login.serializer()), res
                        )
                    )

                )
            }
        }


        get("/generateUserInformation") {
            val res = userInfoService.insertRandomUsers()
            call.respond(HttpStatusCode.OK, res)
        }
        get("/generateUserInfoCheck") {
            val res = userInfoService.getAllUsers()
            if (res.isEmpty()) {
                call.respond(
                    HttpStatusCode.NotFound,
                    jsonConfig.encodeToString(
                        ListSerializer(UserInformation.serializer()),
                        listOf()
                    )
                )
            } else
                call.respond(
                    HttpStatusCode.OK,
                    Success(
                        HttpStatusCode.OK.value,
                        jsonConfig.encodeToString(
                            ListSerializer(UserInformation.serializer()), res
                        )
                    )

                )
        }
        get("/generateEmployee") {
            val res = employeeService.insertRandomEmployees()
            call.respond(HttpStatusCode.OK, res)
        }
        get("/generateAll") {
            loginService.insertRandomLogins()
            userInfoService.insertRandomUsers()
            employeeService.insertRandomEmployees()
        }

        post("/login") {
            call.handleLogin(loginService, userInfoService)
        }

        // Create city
        post("/cities") {
            val city = call.receive<City>()
            val id = cityService.create(city)
            call.respond(HttpStatusCode.Created, id)
        }

        // Read city
        get("/cities/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            try {
                val city = cityService.read(id)
                call.respond(HttpStatusCode.OK, city)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // Update city
        put("/cities/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val user = call.receive<City>()
            cityService.update(id, user)
            call.respond(HttpStatusCode.OK)
        }

        // Delete city
        delete("/cities/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            cityService.delete(id)
            call.respond(HttpStatusCode.OK)
        }
    }
}

/**
 * Makes a connection to a Postgres database.
 *
 * In order to connect to your running Postgres process,
 * please specify the following parameters in your configuration file:
 * - postgres.url -- Url of your running database process.
 * - postgres.user -- Username for database connection
 * - postgres.password -- Password for database connection
 *
 * If you don't have a database process running yet, you may need to [download]((https://www.postgresql.org/download/))
 * and install Postgres and follow the instructions [here](https://postgresapp.com/).
 * Then, you would be able to edit your url,  which is usually "jdbc:postgresql://host:port/database", as well as
 * user and password values.
 *
 *
 * @param embedded -- if [true] defaults to an embedded database for tests that runs locally in the same process.
 * In this case you don't have to provide any parameters in configuration file, and you don't have to run a process.
 *
 * @return [Connection] that represent connection to the database. Please, don't forget to close this connection when
 * your application shuts down by calling [Connection.close]
 * */
fun Application.connectToPostgres(embedded: Boolean): Connection {
    Class.forName("org.postgresql.Driver")
    if (embedded) {
        return DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "root", "")
    } else {

        val url = environment.config.property("postgres.url").getString()
        val user = environment.config.property("postgres.user").getString()
        val password = environment.config.property("postgres.password").getString()

        return DriverManager.getConnection(url, user, password)
    }
}

