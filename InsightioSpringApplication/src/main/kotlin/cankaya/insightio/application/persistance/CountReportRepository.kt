package cankaya.insightio.application.persistance

import cankaya.insightio.domain.CountReport
import java.time.Instant

interface CountReportRepository {
    fun save(countReport: CountReport): CountReport

    fun getDistinctTargetIds(): List<Int>

    fun getDistinctTargetIdsByCameraId(cameraId: String): List<Int>

    fun getReportsByCameraIdAndTargetId(
        cameraId: String,
        targetId: Int,
    ): List<CountReport>

    fun getReportsByCameraIdAndTargetIdAndDate(
        cameraId: String,
        targetId: Int,
        startDate: Instant,
        endDate: Instant,
    ): List<CountReport>

    fun getReportsByCameraIdAndTargetIdAndDateSorted(
        cameraId: String,
        targetId: Int,
        startDate: Instant,
        endDate: Instant,
    ): List<CountReport>
}
