package cankaya.insightio.infrastructure.api

import cankaya.insightio.application.services.UserService
import cankaya.insightio.application.services.models.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
//dependencies kontrol edilmeli**

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @PostMapping("/username")
    fun loginUser(@RequestBody loginDto: LoginDto): ResponseEntity<ApiResponse> {
        

        // userın doğrulanması
        val isUserValid = userService.validateAndDecryptUser(loginDto.username, loginDto.password)

        return if (isUserValid) {
            val user = userService.findByUsername(loginDto.username)
            ResponseEntity.ok(ApiResponse.success(user))
        } else {
            ResponseEntity.status(401).body(ApiResponse.error(statusCode = 401, errorMessage = "Unauthorized"))
        }
    }
}
// başka yere alınması lazım**
data class LoginDto(val username: String, val password: String)
