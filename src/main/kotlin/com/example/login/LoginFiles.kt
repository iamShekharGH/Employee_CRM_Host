import com.example.model.ErrorResponse
import com.example.model.Login
import com.example.model.LoginRequest
import com.example.model.LoginResponse
import com.example.model.UserInformation
import com.example.plugins.generateToken
import com.example.plugins.tables.LoginService
import com.example.plugins.tables.UserInfoService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.*
import io.ktor.server.response.respond
import kotlinx.serialization.json.Json

val jsonConfig = Json { encodeDefaults = true }


suspend fun ApplicationCall.handleLogin(loginService: LoginService, userInfoService: UserInfoService) {

    val loginRequest = receive<LoginRequest>()
    val loginUser = validateUser(loginRequest.username, loginRequest.password, loginService)

    if (loginUser == null) {
        respond(
            HttpStatusCode.Unauthorized,
            ErrorResponse(status = HttpStatusCode.Unauthorized.value, message = "Invalid username or password")
        )
        return
    } else {
        val token = generateToken(loginUser.id)
        loginService.update(loginUser.id, loginUser.copy(authToken = token))

        val userInfo = getUser(userInfoService, loginUser.id)


        val response = LoginResponse(token, userInfo)
        respond(HttpStatusCode.OK, jsonConfig.encodeToString(LoginResponse.serializer(), response))
    }
}

suspend fun getUser(userInfoService: UserInfoService, id: Int): UserInformation {
    //This should be found!!
    val userInfo = userInfoService.readUser(id)
    return userInfo
}

suspend fun validateUser(username: String, password: String, loginService: LoginService): Login? {
    val ls = loginService.hasUser(username, password)
    return if (ls.userFound) {
        ls.login
    } else {
        null
    }

}