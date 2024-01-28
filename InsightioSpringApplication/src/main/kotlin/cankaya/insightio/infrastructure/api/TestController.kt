package cankaya.insightio.infrastructure.api

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/deneme")
class TestController {
    @GetMapping("/deneme/{id}")
    fun getResourceById(@PathVariable id: Long): String {
        val resource = "Resource with ID $id"
        return resource
    }
}