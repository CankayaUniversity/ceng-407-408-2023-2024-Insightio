package cankaya.insightio.infrastructure.api

import cankaya.insightio.application.services.UserService
import cankaya.insightio.infrastructure.api.models.ApiResponse
import cankaya.insightio.application.utils.AESUtils
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

// dependencies kontrol edilmeli** -> formatlarsan gider

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
    private val utils: AESUtils,
) {
    @PostMapping("/login")
    fun login(
        @RequestBody loginDto: LoginDto,
    ): ResponseEntity<ApiResponse> {
        val isUserValid = userService.validateUser(loginDto.username, loginDto.password)
        return if (isUserValid) {
            val user = userService.findByUsername(loginDto.username)
            ResponseEntity.ok(ApiResponse.success(user))
        } else {
            ResponseEntity.status(401).body(ApiResponse.error(statusCode = 401, errorMessage = "Unauthorized"))
        }
    }

    @GetMapping("/sadeceDeneme/encyrpt")
    fun encryp(
        @RequestParam password: String,
    ): String {
        return utils.encryptAES(password)
    }
}

// başka yere alınması lazım**
// istersen services -> models altına yazabilirsin LoginRequest ismiyle
data class LoginDto(val username: String, val password: String)
