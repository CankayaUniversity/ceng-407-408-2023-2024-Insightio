package cankaya.insightio.domain

import cankaya.insightio.infrastructure.mongoDB.config.ZoneType
data class Zone(
    val zoneName: String,
    val zoneType: ZoneType,
    val startPoint: Point,
    val endPoint: Point,
)

