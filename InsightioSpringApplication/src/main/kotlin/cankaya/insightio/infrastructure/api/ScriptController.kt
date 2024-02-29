package cankaya.insightio.infrastructure.api

import cankaya.insightio.application.services.TrackerService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import cankaya.insightio.application.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/script")
class ScriptController(
        val trackerService: TrackerService
) {
    @PostMapping("/run")
    suspend fun runPythonScript() {
        return trackerService.runPythonScript()
    }

    @GetMapping("/denemeEndpoint")
    suspend fun deneme(): Int {
        return 5
    }
}



