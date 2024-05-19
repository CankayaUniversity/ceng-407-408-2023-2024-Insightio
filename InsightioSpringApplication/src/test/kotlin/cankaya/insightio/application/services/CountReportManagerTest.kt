package cankaya.insightio.application.services

import cankaya.insightio.application.persistance.CountReportRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class CountReportManagerTest {
    @Mock
    private lateinit var mockCountReportRepository: CountReportRepository

    @InjectMocks
    private lateinit var sut: CountReportManager

    @Test
    fun `when creating audit reports, it should generate successfully`() {
    }
}
