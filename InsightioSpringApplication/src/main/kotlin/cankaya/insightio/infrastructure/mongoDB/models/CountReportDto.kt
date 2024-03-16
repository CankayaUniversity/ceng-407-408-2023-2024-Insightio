package cankaya.insightio.infrastructure.mongoDB.models

import jakarta.validation.constraints.NotBlank
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

// ben tabloyu oluşturmadım mongodb kurulu değil diye
@Document(collection = "CountReport")
data class CountReportDto(
    @Id
    val id: String? = null,
    @NotBlank
    val cameraId: String,
    @NotBlank
    val targetCounts: List<TargetCountDto>,
)
