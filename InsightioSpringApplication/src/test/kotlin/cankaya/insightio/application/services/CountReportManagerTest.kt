package cankaya.insightio.application.services

import cankaya.insightio.application.persistance.CountReportRepository
import cankaya.insightio.application.utils.InstantToLocalDateConverter
import cankaya.insightio.domain.CountReport
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.Random

@ExtendWith(MockitoExtension::class)
class CountReportManagerTest {
    @Mock
    private lateinit var mockCountReportRepository: CountReportRepository

    @InjectMocks
    private lateinit var sut: CountReportManager

    private val randomSeed = Random()
    private val converter = InstantToLocalDateConverter()

    @Test
    fun `when creating audit reports hourly, it should generate successfully`() {
        // Given
        val cameraId = randomSeed.nextInt().toString()
        val targetId = randomSeed.nextInt()
        val now = Instant.now()
        val localDate = converter.convert(now)

        val countReport1 =
            CountReport(
                getRandomString(),
                cameraId,
                targetId,
                LocalDateTime.now().minus(2, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC).toString(),
                mapOf(Pair("zone1", 10)),
                10,
            )

        val countReport2 =
            CountReport(
                getRandomString(),
                cameraId,
                targetId,
                LocalDateTime.now().minus(1, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC).toString(),
                mapOf(Pair("zone1", 5)),
                5,
            )

        val totalCount = countReport1.overallTotal + countReport2.overallTotal

        `when`(
            mockCountReportRepository.getReportsByCameraIdAndTargetIdAndDate(
                cameraId,
                targetId,
                localDate.atStartOfDay().toInstant(ZoneOffset.UTC),
                now,
            ),
        ).thenReturn(listOf(countReport1, countReport2))

        // When
        val counts = sut.calculateHourlyCounts(cameraId, targetId, now, localDate)

        // Then
        assertEquals(24, counts.size)
        assertEquals(totalCount, counts.sum())
    }
}

fun getRandomString() = Random().nextInt().toString()
