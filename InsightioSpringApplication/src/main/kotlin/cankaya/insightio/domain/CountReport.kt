package cankaya.insightio.domain

import jakarta.validation.constraints.NotBlank
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "CountReport")
data class CountReport(
    @Id
    val id: String? = null,
    @NotBlank
    val cameraId: String,
    @NotBlank
    val targetId: Int,
    @NotBlank
    val timestamp: String,
    val totalCounts: Map<String, Int>,
    @NotBlank
    val overallTotal: Int,
)
