package cankaya.insightio.domain

import cankaya.insightio.infrastructure.mongodb.impls.ZoneType

data class Zone(
    val zoneName: String,
    val zoneType: ZoneType,
    val startPoint: Point,
    val endPoint: Point,
)


//EDA ZoneType ı buraya declare edebilir misin