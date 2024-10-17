package com.example.employee

import com.example.model.EmployeeResponse
import com.example.model.ErrorResponse
import com.example.plugins.tables.EmployeeService
import com.example.plugins.tables.LoginService
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.header
import io.ktor.server.response.respond

suspend fun ApplicationCall.handleEmployeeFiles(loginService: LoginService, employeeService: EmployeeService) {
    println(
        request.headers.entries().toString()
    )

    val authToken = request.header(HttpHeaders.Authorization)?.substringAfter("Bearer ")

    if (authToken == null) {
        response.status(HttpStatusCode.Unauthorized)
        respond("Missing or invalid Authorization header")
        respond(
            HttpStatusCode.Unauthorized,
            ErrorResponse(status = HttpStatusCode.Unauthorized.value, message = "Please login again")
        )
        return
    }

    val loginState = loginService.hasUserByToken(authToken)

    if (loginState == null) {
        respond(
            HttpStatusCode.Unauthorized,
            ErrorResponse(status = HttpStatusCode.Unauthorized.value, message = "Please login again")
        )
        /*response.status(HttpStatusCode.Unauthorized)
        respond("Invalid token")*/
        return
    } else {
        val employeeList = employeeService.getAllEmployees()

            /*

            val response = LoginResponse(status = HttpStatusCode.OK.value, authKey = token, data = userInfo)
            respond(HttpStatusCode.OK, response)
             */

            respond(EmployeeResponse(HttpStatusCode.OK.value, employeeList))
    }

// Assuming you have a function like getEmployeesList in UserInfoService


}