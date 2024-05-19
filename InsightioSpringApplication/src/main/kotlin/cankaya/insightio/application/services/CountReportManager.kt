package cankaya.insightio.application.services

import cankaya.insightio.application.persistance.CountReportRepository
import cankaya.insightio.application.utils.InstantToLocalDateConverter
import cankaya.insightio.domain.CountReportAudit
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service
import java.time.*
import java.time.temporal.ChronoField

// Service for generating count reports
@Service
class CountReportManager(
    private val countReportRepository: CountReportRepository,
) {
    private val converter = InstantToLocalDateConverter()

    suspend fun createAuditReportByTarget(
        targetId: Int,
        cameraId: String,
    ): CountReportAudit {
        return coroutineScope {
            val instant = Instant.now()
            // val localDate = instant.atOffset(ZoneOffset.UTC).toLocalDate()
            val localDate = converter.convert(instant)

            val hourlyCountsTask =
                async { calculateHourlyCounts(cameraId, targetId, instant, localDate) }
            val dailyCountsTask =
                async { calculateDailyCounts(cameraId, targetId, instant, localDate) }
            val weeklyCountsTask =
                async { calculateWeeklyCounts(cameraId, targetId, instant, localDate) }
            val monthlyCountsTask =
                async { calculateMonthlyCounts(cameraId, targetId, instant, localDate) }

            return@coroutineScope CountReportAudit(
                target = targetId,
                counts =
                    listOf(
                        hourlyCountsTask.await(),
                        dailyCountsTask.await(),
                        weeklyCountsTask.await(),
                        monthlyCountsTask.await(),
                    ),
            )
        }
    }

    suspend fun createAuditReports(cameraId: String): List<CountReportAudit> {
        return coroutineScope {
            val targetIds = countReportRepository.getDistinctTargetIdsByCameraId(cameraId)
            val results = mutableListOf<CountReportAudit>()

            targetIds.forEach {
                results.add(createAuditReportByTarget(it, cameraId))
            }
            return@coroutineScope results
        }
    }

    fun calculateHourlyCounts(
        cameraId: String,
        targetId: Int,
        now: Instant,
        localDate: LocalDate,
    ): List<Int> {
        val counts = MutableList(size = 24) { 0 }
        val startOfDay = localDate.atStartOfDay()
        val records =
            countReportRepository.getReportsByCameraIdAndTargetIdAndDate(
                cameraId,
                targetId,
                startOfDay.toInstant(ZoneOffset.UTC),
                now,
            )

        records.forEach {
            val hour = Instant.parse(it.timestamp).atZone(ZoneOffset.UTC).hour
            counts[hour] = it.overallTotal
        }
        return counts
    }

    fun calculateDailyCounts(
        cameraId: String,
        targetId: Int,
        now: Instant,
        localDate: LocalDate,
    ): List<Int> {
        val counts = MutableList(7) { 0 }
        val startDate = localDate.atStartOfDay().with(DayOfWeek.MONDAY).toInstant(ZoneOffset.UTC)

        val records =
            countReportRepository.getReportsByCameraIdAndTargetIdAndDate(
                cameraId,
                targetId,
                startDate,
                now,
            )

        records.forEach {
            val zonedDatetime = Instant.parse(it.timestamp).atZone(ZoneOffset.UTC)
            val dayOfWeek = zonedDatetime.dayOfWeek.value - 1
            counts[dayOfWeek] += it.overallTotal
        }
        return counts
    }

    private fun calculateWeeklyCounts(
        cameraId: String,
        targetId: Int,
        now: Instant,
        localDate: LocalDate,
    ): List<Int> {
        val counts = MutableList(4) { 0 }
        val startDate =
            LocalDate.of(
                localDate.year,
                localDate.monthValue,
                1,
            ).atStartOfDay().toInstant(ZoneOffset.UTC)
        val records =
            countReportRepository.getReportsByCameraIdAndTargetIdAndDate(
                cameraId,
                targetId,
                startDate,
                now,
            )

        records.forEach {
            val zonedDatetime = Instant.parse(it.timestamp).atZone(ZoneOffset.UTC)
            val dayOfWeek = zonedDatetime.toLocalDate().get(ChronoField.ALIGNED_WEEK_OF_MONTH)
            if (dayOfWeek != 5) {
                counts[dayOfWeek] += it.overallTotal
            }
        }

        return counts
    }

    fun calculateMonthlyCounts(
        cameraId: String,
        targetId: Int,
        now: Instant,
        localDate: LocalDate,
    ): List<Int> {
        val counts = MutableList(12) { 0 }
        val startDate = LocalDate.of(localDate.year, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC)

        val records =
            countReportRepository.getReportsByCameraIdAndTargetIdAndDate(
                cameraId,
                targetId,
                startDate,
                now,
            )

        records.forEach {
            val zonedDatetime = Instant.parse(it.timestamp).atZone(ZoneOffset.UTC)
            val month = zonedDatetime.toLocalDate().monthValue - 1
            counts[month] += it.overallTotal
        }
        return counts
    }
}
