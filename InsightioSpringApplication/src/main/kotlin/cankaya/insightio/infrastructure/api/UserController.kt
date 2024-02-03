package cankaya.insightio.infrastructure.api

import cankaya.insightio.application.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

//Login Controller
@RestController
@RequestMapping("/login")
class UserController(private val userService: UserService) {

    @GetMapping("/{login}")
    fun getUserByLogin(@PathVariable username: String, @RequestParam password: String): ResponseEntity<String> {
        return if (userService.validateUser(username, password)) {
            ResponseEntity.ok("Login successful")
        } else {
            ResponseEntity.status(401).body("Unauthorized")
        }
    }
}