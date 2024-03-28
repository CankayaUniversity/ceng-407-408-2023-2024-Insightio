package cankaya.insightio.infrastructure.api

import cankaya.insightio.application.services.UserService
import cankaya.insightio.application.utils.AESUtils
import cankaya.insightio.domain.User
import cankaya.insightio.infrastructure.api.models.ApiResponse
import cankaya.insightio.infrastructure.api.models.LoginRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
    private val utils: AESUtils,
) {
    // Login icin controller
    @PostMapping("/login")
    fun login(
        @RequestBody loginRequest: LoginRequest,
    ): ResponseEntity<ApiResponse> {
        val isUserValid = userService.validateUser(loginRequest.username, loginRequest.password)
        return if (isUserValid) {
            val user = userService.findByUsername(loginRequest.username)
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

    @PostMapping
    fun createUser(
        @RequestBody user: User,
    ): ResponseEntity<ApiResponse> {
        return try {
            val newUser = userService.createUser(user)
            ResponseEntity.ok(ApiResponse.success(newUser))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(ApiResponse.error(errorMessage = "User not created: ${e.message}"))
        }
    }

    // user update with id
    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: String,
        @RequestBody updatedUser: User,
    ): ResponseEntity<ApiResponse> {
        return try {
            val user = userService.updateUser(id, updatedUser)
            ResponseEntity.ok(ApiResponse.success(user))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(ApiResponse.error(errorMessage = "User not updated: ${e.message}"))
        }
    }

    // user delete with id
    @DeleteMapping("/{id}")
    fun deleteUser(
        @PathVariable id: String,
    ): ResponseEntity<ApiResponse> {
        return try {
            userService.deleteUser(id)
            ResponseEntity.ok(ApiResponse.success())
        } catch (e: Exception) {
            ResponseEntity.status(500).body(ApiResponse.error(errorMessage = "User not deleted: ${e.message}"))
        }
    }

    // userları görüntüleme hepsini
    @GetMapping
    fun getAllUsers(): ResponseEntity<ApiResponse> {
        return try {
            val users = userService.findAllUsers()
            ResponseEntity.ok(ApiResponse.success(users))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(ApiResponse.error(errorMessage = "User not be listed: ${e.message}"))
        }
    }

    // user get info with id
    @GetMapping("/{id}")
    fun getUserById(
        @PathVariable id: String,
    ): ResponseEntity<ApiResponse> {
        return try {
            val user = userService.findUserById(id)
            ResponseEntity.ok(ApiResponse.success(user))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(ApiResponse.error(errorMessage = "User not found: ${e.message}"))
        }
    }
}
