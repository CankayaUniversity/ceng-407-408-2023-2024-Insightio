package cankaya.insightio.infrastructure.api

import cankaya.insightio.application.persistance.CountReportRepository
import cankaya.insightio.application.services.CountReportManager
import cankaya.insightio.domain.CountReport
import cankaya.insightio.infrastructure.api.models.AddCountRequest
import cankaya.insightio.infrastructure.api.models.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/count-reports")
class CountReportController(
    private val countReportManager: CountReportManager,
    private val countReportRepository: CountReportRepository,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Operation(description = "Adds new count report")
    @PostMapping("/add")
    fun addCountReport(
        @RequestBody addRequest: AddCountRequest,
    ): ResponseEntity<ApiResponse> {
        return try {
            val countReport =
                CountReport(
                    cameraId = addRequest.cameraId,
                    targetId = addRequest.targetId,
                    timestamp = addRequest.timestamp,
                    totalCounts = addRequest.totalCounts,
                    overallTotal = addRequest.overallTotal,
                )
            countReportRepository.save(countReport)
            ResponseEntity.ok(ApiResponse.success())
        } catch (exception: Exception) {
            logger.error(
                "Exception occurred in CountReportController add endpoint, exception: " +
                    "${exception.message}",
            )
            ResponseEntity.internalServerError().body(
                ApiResponse.error(errorMessage = exception.localizedMessage),
            )
        }
    }

    @Operation(description = "Gets count report by target id and camera id")
    @GetMapping("/audit/{targetId}")
    suspend fun getAllCountReportById(
        @PathVariable targetId: Int,
        @RequestParam cameraId: String,
    ): ResponseEntity<ApiResponse> {
        return try {
            val auditReport = countReportManager.createAuditReportByTarget(targetId, cameraId)
            ResponseEntity.ok(ApiResponse.success(auditReport))
        } catch (exception: Exception) {
            logger.error(
                "Exception occurred in CountReportController, audit/$targetId exception: " +
                    "${exception.message}",
            )
            ResponseEntity.internalServerError().body(
                ApiResponse.error(errorMessage = exception.localizedMessage),
            )
        }
    }

    @Operation(description = "Gets count reports for every target by camera id")
    @GetMapping("/audit")
    suspend fun getAllCountReports(
        @RequestParam cameraId: String,
    ): ResponseEntity<ApiResponse> {
        return try {
            val auditReport = countReportManager.createAuditReports(cameraId)
            ResponseEntity.ok(ApiResponse.success(auditReport))
        } catch (exception: Exception) {
            logger.error(
                "Exception occurred in CountReportController, audit exception: " +
                    "${exception.message}",
            )
            ResponseEntity.internalServerError().body(
                ApiResponse.error(errorMessage = exception.localizedMessage),
            )
        }
    }

    @Operation(description = "Gets every target id")
    @GetMapping("/target-ids")
    suspend fun getTargetIds(): ResponseEntity<ApiResponse> {
        return try {
            val targetIds = countReportRepository.getDistinctTargetIds()
            ResponseEntity.ok(ApiResponse.success(targetIds))
        } catch (exception: Exception) {
            logger.error(
                "Exception occurred in CountReportController, target-ids exception: " +
                    "${exception.message}",
            )
            ResponseEntity.internalServerError().body(
                ApiResponse.error(errorMessage = exception.localizedMessage),
            )
        }
    }

    @Operation(description = "Gets every target id by camera id")
    @GetMapping("/target-ids/{cameraId}")
    suspend fun getTargetIdsByCameraId(
        @PathVariable cameraId: String,
    ): ResponseEntity<ApiResponse> {
        return try {
            val targetIds = countReportRepository.getDistinctTargetIdsByCameraId(cameraId)
            ResponseEntity.ok(ApiResponse.success(targetIds))
        } catch (exception: Exception) {
            logger.error(
                "Exception occurred in CountReportController, target-ids/{cameraId} exception: " +
                    "${exception.message}",
            )
            ResponseEntity.internalServerError().body(
                ApiResponse.error(errorMessage = exception.localizedMessage),
            )
        }
    }
}
