package cankaya.insightio.infrastructure.api

import cankaya.insightio.application.persistance.CountReportRepository
import cankaya.insightio.application.services.CountReportManager
import cankaya.insightio.domain.CountReport
import cankaya.insightio.infrastructure.api.models.AddCountRequest
import cankaya.insightio.infrastructure.api.models.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

// dependencies kontrol edilmeli** -> formatlarsan bunlar gider

@RestController
@RequestMapping("/count-reports")
class CountReportController(
    private val countReportManager: CountReportManager,
    private val countReportRepository: CountReportRepository,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

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
                "Exception occured in UserController add endpoint, stack trace: " +
                    "${exception.stackTrace}",
            )
            ResponseEntity.internalServerError().body(
                ApiResponse.error(errorMessage = exception.localizedMessage),
            )
        }
    }

    @GetMapping("/audit/count-reports")
    fun getAllCountReports(): ResponseEntity<ApiResponse> {
        return try {
            val auditReport = countReportManager.createAuditReports()
            ResponseEntity.ok(ApiResponse.success(auditReport))
        } catch (exception: Exception) {
            logger.error(
                "Exception occured in UserController, audit/count-reports stack trace: " +
                    "${exception.stackTrace}",
            )
            ResponseEntity.internalServerError().body(
                ApiResponse.error(errorMessage = exception.localizedMessage),
            )
        }
    }
}
