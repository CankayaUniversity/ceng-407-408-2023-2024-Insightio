package cankaya.insightio.infrastructure.api

import cankaya.insightio.application.services.TrackingService
import cankaya.insightio.infrastructure.api.models.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/script")
class ScriptController(
    val trackingService: TrackingService,
) {
    @Operation(description = "Run python tracking script")
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
}
