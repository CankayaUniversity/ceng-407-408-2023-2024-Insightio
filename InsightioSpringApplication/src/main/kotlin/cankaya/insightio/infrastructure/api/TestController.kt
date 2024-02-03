package cankaya.insightio.infrastructure.api

import cankaya.insightio.application.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/deneme")
class TestController {
    @GetMapping("/deneme/{id}")
    fun getResourceById(@PathVariable id: Long): String {
        val resource = "Resource with ID $id"
        return resource
    }
}

//Login Controller
@RestController
@RequestMapping("/login")
class UserController(private val userService: UserService) {

    @GetMapping("/{login}")
    fun getUserByLogin(@PathVariable login: String, @RequestParam password: String): ResponseEntity<String> {
        return if (userService.validateUser(login, password)) {
            ResponseEntity.ok("Login successful")
        } else {
            ResponseEntity.status(401).body("Unauthorized")
        }
    }
}
