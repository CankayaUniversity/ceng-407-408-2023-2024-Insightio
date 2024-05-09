package cankaya.insightio.infrastructure.api.models

data class AddCountRequest(
    val cameraId: String,
    val targetId: Int,
    val timestamp: String,
    val totalCounts: Map<String, Int>,
    val overallTotal: Int,
)
