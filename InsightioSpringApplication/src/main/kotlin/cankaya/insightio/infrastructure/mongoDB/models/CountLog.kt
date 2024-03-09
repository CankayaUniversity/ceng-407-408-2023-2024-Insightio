package cankaya.insightio.infrastructure.mongoDB.models

data class CountLog(
    val id: String,
    val countReports: List<CountReport>,
    val createdDate: String,
)
