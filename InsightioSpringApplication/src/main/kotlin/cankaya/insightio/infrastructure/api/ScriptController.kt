package cankaya.insightio.infrastructure.api

import cankaya.insightio.application.services.TrackingService
import cankaya.insightio.application.services.models.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/script")
class ScriptController(
    val trackingService: TrackingService,
) {
    @PostMapping("/run")
    suspend fun runPythonScript(): ResponseEntity<ApiResponse> {
        try {
            if (trackingService.runPythonScriptAsProcess()) {
                return ResponseEntity.ok(ApiResponse.success())
            }
            return ResponseEntity.internalServerError().body(
                ApiResponse.error(
                    statusCode = 500,
                    errorMessage = "Failed to execute Python script.",
                ),
            )
        } catch (exception: Exception) {
            return ResponseEntity.internalServerError().body(
                ApiResponse.error(
                    statusCode = 500,
                    errorMessage = exception.message ?: "Error executing Python script",
                ),
            )
        }
    }

    @GetMapping("/deneme")
    suspend fun deneme(): Int {
        return 6
    }
}
