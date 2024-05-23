package cankaya.insightio.domain

data class Zone(
    val zoneName: String,
    val zoneType: ZoneType,
    val startPoint: Point,
    val endPoint: Point,
)

enum class ZoneType {
    LINE,
    RECTANGLE,
}
