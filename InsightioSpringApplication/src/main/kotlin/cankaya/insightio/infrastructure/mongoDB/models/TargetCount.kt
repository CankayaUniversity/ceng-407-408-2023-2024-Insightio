package cankaya.insightio.infrastructure.mongoDB.models

data class TargetCount(
    val target: Int,
    val zoneName: String,
    val currentCount: Int,
    val hourlyTotalCount: Int,
    val dailyTotalCount: Int,
    val timestamp: Long,
)
