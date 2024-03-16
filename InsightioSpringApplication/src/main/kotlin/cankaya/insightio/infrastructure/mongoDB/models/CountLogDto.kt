package cankaya.insightio.infrastructure.mongoDB.models

data class CountLogDto(
    val id: String,
    val countReports: List<CountReportDto>,
    val createdDate: String,
)
