package cankaya.insightio.application.services

import cankaya.insightio.application.persistance.CountReportRepository
import cankaya.insightio.domain.CountReportAudit
import org.springframework.stereotype.Service

@Service
class CountReportManager(
    private val countReportRepository: CountReportRepository,
) {
    fun createAuditReports(): CountReportAudit {
        // TODO
        return CountReportAudit("a", listOf(listOf(1)))
    }
}
